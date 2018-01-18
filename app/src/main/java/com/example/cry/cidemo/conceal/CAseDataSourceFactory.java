package com.example.cry.cidemo.conceal;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;

/**
 * 自定义资源工厂
 * Created by 汪常伟 on 2017/11/13.
 */

public class CAseDataSourceFactory implements DataSource.Factory {

    private final Context context;
    private final TransferListener<? super DataSource> listener;
    private final DataSource.Factory baseDataSourceFactory;

    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     */
    public CAseDataSourceFactory(Context context, String userAgent) {
        this(context, userAgent, null);    }

    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     * @param listener An optional listener.
     */
    public CAseDataSourceFactory(Context context, String userAgent,
                                 TransferListener<? super DataSource> listener) {
        this(context, listener, new DefaultHttpDataSourceFactory(userAgent, listener));
    }

    /**
     * @param context A context.
     * @param listener An optional listener.
     * @param baseDataSourceFactory A {@link DataSource.Factory} to be used to create a base {@link DataSource}
     *     for {@link DefaultDataSource}.
     * @see DefaultDataSource#DefaultDataSource(Context, TransferListener, DataSource)
     */
    public CAseDataSourceFactory(Context context, TransferListener<? super DataSource> listener,
                                 DataSource.Factory baseDataSourceFactory) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSourceFactory = baseDataSourceFactory;
    }

    @Override
    public CAseDataSource createDataSource() {
        return new CAseDataSource(context, listener, baseDataSourceFactory.createDataSource());
    }
}
