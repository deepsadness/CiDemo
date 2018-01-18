package com.example.cry.cidemo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.cry.cidemo.conceal.CAseDataSourceFactory;
import com.example.cry.cidemo.conceal.ConcealUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import static com.example.cry.cidemo.Constants.DOWNLOAD_HELP_KEY;

public class ConcealActivity extends AppCompatActivity {
    private File srcFile, conceal_file, after_conceal, outFile3, outFile4;
    private SimpleExoPlayer player;

    private Handler UiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conceal);

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        srcFile = new File(storageDirectory, "Y.mp3");
        conceal_file = new File(storageDirectory, "YC.mp3.case");
        after_conceal = new File(storageDirectory, "YC_out_conceal.mp3");

        UiHandler = new Handler(Looper.getMainLooper());
    }

    public void encryption(View view) {
        try {
            ConcealUtils.decryptSteam(DOWNLOAD_HELP_KEY, conceal_file.getAbsolutePath(), after_conceal.getAbsolutePath(), new Listener() {
                @Override
                public void onCompleted() {
                    HandlerToast("解密成功");
                }

                @Override
                public void onStart() {
                    HandlerToast("解密开始");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void HandlerToast(final String msg) {
        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConcealActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cipher(View view) {
        try {
            ConcealUtils.encryptSteam(DOWNLOAD_HELP_KEY, srcFile.getAbsolutePath(), conceal_file.getAbsolutePath(), new Listener() {
                @Override
                public void onCompleted() {
                    HandlerToast("加密成功");
                }

                @Override
                public void onStart() {
                    HandlerToast("加密成功");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(View view) {
//        HandlerToast("还未实现！！");
        String downloadUrl = "https://v.baobaobooks.net/audio/content/201607/146762185165961.mp3";
        DownloadHelper.downLoadFile(downloadUrl, conceal_file, true, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void play(View view) {
        initPlayer();
        DefaultBandwidthMeter bandwidthMeter = initPlayer();
        //自定义解密工厂
        CAseDataSourceFactory cAseDataSourceFactory = new CAseDataSourceFactory(this,
                Util.getUserAgent(this, "cidemo"), bandwidthMeter);
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // 3.创建播放器

        ExtractorMediaSource extractorMediaSource =
                new ExtractorMediaSource(Uri.parse(conceal_file.getAbsolutePath()), cAseDataSourceFactory,
                        extractorsFactory, null, null);
        player.setPlayWhenReady(true);
        player.prepare(extractorMediaSource);

    }

    @NonNull
    private DefaultBandwidthMeter initPlayer() {
        // 1.创建一个默认TrackSelector,测量播放过程中的带宽。 如果不需要，可以为null。
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //从MediaSource中选出media提供给可用的Render S来渲染,在创建播放器时被注入
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        // 2.创建一个默认的LoadControl
        //Create a default LoadControl 控制MediaSource缓存media
        DefaultLoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        return bandwidthMeter;
    }

    public void play_original(View view) {
        if (player == null) {
            initPlayer();
        }
        ExtractorMediaSource extractorMediaSource =
                new ExtractorMediaSource(Uri.parse(srcFile.getAbsolutePath()), new FileDataSourceFactory(),
                        new DefaultExtractorsFactory(), null, null);
        player.setPlayWhenReady(true);
        player.prepare(extractorMediaSource);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }


    }


    public void play_after(View view) {
        if (player == null) {
            initPlayer();
        }
        ExtractorMediaSource extractorMediaSource =
                new ExtractorMediaSource(Uri.parse(after_conceal.getAbsolutePath()), new FileDataSourceFactory(),
                        new DefaultExtractorsFactory(), null, null);
        player.setPlayWhenReady(true);
        player.prepare(extractorMediaSource);

    }
}
