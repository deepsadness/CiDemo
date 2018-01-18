package com.example.cry.cidemo;

import android.util.Log;

import com.example.cry.cidemo.conceal.ConcealUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.cry.cidemo.Constants.DOWNLOAD_HELP_KEY;

/**
 * Created by Cry on 2018/1/18.
 */

public class DownloadHelper {

    public final static String TAG = "DownloadHelper";

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @param file    存储目标目录
     */
    public static void downLoadFile(String fileUrl, final File file, final boolean needAes, final int type) {
        final Request request = new Request.Builder().url(fileUrl).build();
        Call.Factory mOkHttpClient = new OkHttpClient.Builder().build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(DOWNLOAD_HELP_KEY, "下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                OutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    //将is 包装成加密的
                    if (needAes) {
                        try {
                            if (type == 1) {
                                Cipher cipher = AESUtils.getCipher(DOWNLOAD_HELP_KEY, true);
                                fos = new CipherOutputStream(fileOutputStream, cipher);
                            } else {
                                fos = ConcealUtils.conceal(App.getApplication(), DOWNLOAD_HELP_KEY, fileOutputStream);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        fos = fileOutputStream;
                    }

                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                        if (needAes) {
                            Log.d(TAG, "加密 current------>" + current);
                        }
                    }
                    fos.flush();
                    Log.d(TAG, "下载成功");
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    Log.d(TAG, "下载失败");
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @param file    存储目标目录
     */
    public static void downLoadFileRandom(String fileUrl, final File file, final boolean needAes, final int type) {
        final Request request = new Request.Builder().url(fileUrl).build();
        Call.Factory mOkHttpClient = new OkHttpClient.Builder().build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(DOWNLOAD_HELP_KEY, "下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                OutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
                    //将is 包装成加密的
                    if (needAes) {
                        try {
                            if (type == 1) {
                                Cipher cipher = AESUtils.getCipher(DOWNLOAD_HELP_KEY, true);
                                fos = new CipherOutputStream(fileOutputStream, cipher);
                            } else {
                                fos = ConcealUtils.conceal(App.getApplication(), DOWNLOAD_HELP_KEY, fileOutputStream);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        fos = fileOutputStream;
                    }

                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                        if (needAes) {
                            Log.d(TAG, "加密 current------>" + current);
                        }
                    }
                    fos.flush();
                    Log.d(TAG, "下载成功");
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    Log.d(TAG, "下载失败");
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }
}
