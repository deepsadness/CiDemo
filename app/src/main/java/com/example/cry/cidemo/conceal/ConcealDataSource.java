/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cry.cidemo.conceal;

import android.net.Uri;

import com.example.cry.cidemo.App;
import com.example.cry.cidemo.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Exoplayer官方Demo的AesDataSource
 * A {@link DataSource} that decrypts data read from an upstream source, encrypted with AES-128 with
 * a 128-bit key and PKCS7 padding.
 * <p>
 * Note that this {@link DataSource} does not support being opened from arbitrary offsets. It is
 * designed specifically for reading whole files as defined in an HLS media playlist. For this
 * reason the implementation is private to the HLS package.
 */
/* package */ final class ConcealDataSource implements DataSource {

    private final DataSource upstream;

    private InputStream concealInputSteam;

    /**
     * @param upstream The upstream {@link DataSource}.
     */
    public ConcealDataSource(DataSource upstream) {
        this.upstream = upstream;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {

        InputStream inputStream = ConcealUtils.unConceal(App.getApplication(), Constants.DOWNLOAD_HELP_KEY, new DataSourceInputStream(upstream, dataSpec));
        if (inputStream == null) {
            throw new RuntimeException("Can not get conceal inputsteam");
        }
        concealInputSteam = inputStream;
        return C.LENGTH_UNSET;
    }

    @Override
    public void close() throws IOException {
        concealInputSteam = null;
        upstream.close();
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        Assertions.checkState(concealInputSteam != null);
        int bytesRead = concealInputSteam.read(buffer, offset, readLength);
        if (bytesRead < 0) {
            return C.RESULT_END_OF_INPUT;
        }
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return upstream.getUri();
    }

}
