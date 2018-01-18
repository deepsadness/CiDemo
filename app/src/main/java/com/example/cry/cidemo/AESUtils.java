package com.example.cry.cidemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    public static byte[] encryptVoice(String seed, byte[] clearbyte)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, clearbyte);
        return result;
    }

    public static byte[] decryptVoice(String seed, byte[] encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = decrypt(rawKey, encrypted);
        return result;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    public static void encryptSteam(String raw, String srcPath, String filePath,Listener listener) throws Exception {
        new Thread(new EncryptRunnable(raw, srcPath, filePath,listener)).start();
    }

    public static void decryptSteam(String raw, String srcPath, String filePath,Listener listener)
            throws Exception {
        new Thread(new DecryptRunnable(raw, srcPath, filePath,listener)).start();
    }

    private static class EncryptRunnable implements Runnable {
        private final String raw;
        private final String srcPath;
        private final String filePath;
        private final Listener listener;

        public EncryptRunnable(String raw, String srcPath, String filePath, Listener listener) {
            this.raw = raw;
            this.srcPath = srcPath;
            this.filePath = filePath;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                CipherInputStream cipherInputStream;
                FileOutputStream fileOutputStream;

                Cipher cipher = getCipher(raw, true);
//                Cipher  cipher = AESHelper.initAESCipher(DOWNLOAD_HELP_KEY, Cipher.ENCRYPT_MODE);

                //开始创建加密流
                cipherInputStream = new CipherInputStream(new FileInputStream(srcPath), cipher);
                fileOutputStream = new FileOutputStream(new File(filePath));
                System.out.println("开始加密");
                listener.onStart();
                byte[] buf = new byte[1024 * 8];
                int length = 0;
                while ((length = cipherInputStream.read(buf)) != -1) {
                    System.out.println("开始加密 字节==>" + length);
                    fileOutputStream.write(buf, 0, length);
                }
                fileOutputStream.close();
                cipherInputStream.close();
                System.out.println("加密完成");
                listener.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static Cipher getCipher(String raw, boolean encrpyt) throws Exception {
        Cipher cipher = null;
        byte[] rawKey = getRawKey(raw.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        cipher = Cipher.getInstance("AES");

        cipher.init(encrpyt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher;
    }

    private static class DecryptRunnable implements Runnable {
        private final String raw;
        private final String srcPath;
        private final String filePath;
        private final  Listener listener;

        public DecryptRunnable(String raw, String srcPath, String filePath, Listener listener) {
            this.raw = raw;
            this.srcPath = srcPath;
            this.filePath = filePath;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                byte[] rawKey = getRawKey(raw.getBytes());
                SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
                Cipher cipher = null;
                CipherInputStream cipherInputStream;
                FileOutputStream fileOutputStream;

                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                        new byte[cipher.getBlockSize()]));
                //开始创建加密流
//                cipherInputStream = new CipherInputStream(new FileInputStream(srcPath), cipher);
//                fileOutputStream = new FileOutputStream(new File(filePath));

                byte[] buffer = new byte[1024];
                FileInputStream in = new FileInputStream(srcPath);
                OutputStream out = new FileOutputStream(filePath);
                CipherOutputStream cout = new CipherOutputStream(out, cipher);
                System.out.println("开始解密");
                listener.onStart();
                int i;
                while ((i = in.read(buffer)) != -1) {
                    System.out.println("开始解密 字节==>" + i);
                    cout.write(buffer, 0, i);
                }
                cout.close();
                in.close();
                System.out.println("解密完成");
                listener.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
