package com.example.cry.cidemo.conceal;

import android.content.Context;
import android.util.Log;

import com.example.cry.cidemo.App;
import com.example.cry.cidemo.Listener;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 使用Facebook conceal 作为加密
 * Created by Cry on 2018/1/18.
 */
public class ConcealUtils {
    private static final String TAG = "ConcealUtils";
    private static Crypto crypto;

    public static OutputStream conceal(Context context, String raw, OutputStream outputStream) {
        // Creates a new Crypto object with default implementations of a key chain
//        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
//        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

//        //使用秘钥链和原生库的默认实现，来创建一个新的加密对象
        ;

        //检查加密功能是否可用
        //如果Android没有正确载入库，则此步骤可能失败
        if (!initCrypto(context, true)) {
            return null;
        }

        if (!crypto.isAvailable()) {
            Log.e(TAG, "ENCRYPTION FAIL!");
            return null;
        }

        //在解密时，new Entity时使用的String必须与解密时相同，否则不能正确解密。
        com.facebook.crypto.Entity entity = new com.facebook.crypto.Entity(raw);
//        com.facebook.crypto.Entity entity = Entity.create(raw);
        OutputStream fileStream = new BufferedOutputStream(
                outputStream);

        //创建输出流，当数据写入流的时候进行加密，并将加密后的数据输出到文件
        try {
            return crypto.getCipherOutputStream(
                    fileStream,
                    entity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean initCrypto(Context context, boolean needAvailable) {
        if (crypto == null) {
            crypto = new Crypto(
                    new SharedPrefsBackedKeyChain(context),
                    new SystemNativeCryptoLibrary());
        }
        //检查加密功能是否可用
        //如果Android没有正确载入库，则此步骤可能失败
        if (needAvailable && !crypto.isAvailable()) {
            Log.e(TAG, "ENCRYPTION FAIL!");
            return false;
        }
        return true;
    }

    public static InputStream unConceal(Context context, String raw, InputStream inputStream) {
//        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
//        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
////        //使用秘钥链和原生库的默认实现，来创建一个新的加密对象
        if (!initCrypto(context, false)) {
            return null;
        }

        InputStream fileStream = new BufferedInputStream(
                inputStream
        );

//        InputStream fileStream =inputStream;
        //在解密时，new Entity时使用的String必须与解密时相同，否则不能正确解密。
        com.facebook.crypto.Entity entity = new com.facebook.crypto.Entity(raw);
//        com.facebook.crypto.Entity entity = Entity.create(raw);
        //创建一个输入流读取解密数据。
        try {
            return crypto.getCipherInputStream(
                    fileStream,
                    entity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void encryptSteam(String raw, String srcPath, String filePath, Listener listener) throws Exception {
        new Thread(new EncryptRunnable(raw, srcPath, filePath, listener)).start();
    }

    public static void decryptSteam(String raw, String srcPath, String filePath, Listener listener)
            throws Exception {
        new Thread(new DecryptRunnable(raw, srcPath, filePath, listener)).start();
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

                //开始创建加密流
                FileInputStream fileInputStream = new FileInputStream(srcPath);
                OutputStream outputStream = conceal(App.getApplication(), raw, new FileOutputStream(new File(filePath)));

                if (outputStream == null) {
                    Log.e(TAG, "Conceal failed!!");
                } else {
                    System.out.println("开始加密");
                    listener.onStart();
                    byte[] buf = new byte[1024 * 8];
                    int length = 0;
                    while ((length = fileInputStream.read(buf)) != -1) {
                        System.out.println("开始加密 字节==>" + length);
                        outputStream.write(buf, 0, length);
                    }
                    fileInputStream.close();
                    outputStream.close();
                    System.out.println("加密完成");
                    listener.onCompleted();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class DecryptRunnable implements Runnable {
        private final String raw;
        private final String srcPath;
        private final String filePath;
        private final Listener listener;

        public DecryptRunnable(String raw, String srcPath, String filePath, Listener listener) {
            this.raw = raw;
            this.srcPath = srcPath;
            this.filePath = filePath;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                //开始创建加密流
                OutputStream outputStream = new FileOutputStream(filePath);
                FileInputStream fileInputStream = new FileInputStream(srcPath);
                InputStream inputStream = unConceal(App.getApplication(), raw, fileInputStream);
                if (inputStream == null) {
                    Log.e(TAG, "Conceal get InputSteam failed!!");
                } else {
                    byte[] buffer = new byte[1024];
                    System.out.println("开始解密");
                    listener.onStart();
                    int i;
                    while ((i = inputStream.read(buffer)) != -1) {
                        System.out.println("开始解密 字节==>" + i);
                        outputStream.write(buffer, 0, i);
                    }
                    outputStream.close();
                    inputStream.close();
                    System.out.println("解密完成");
                    listener.onCompleted();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
