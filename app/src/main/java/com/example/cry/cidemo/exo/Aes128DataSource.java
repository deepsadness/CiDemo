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
package com.example.cry.cidemo.exo;

import android.net.Uri;

import com.example.cry.cidemo.AESUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import static com.example.cry.cidemo.Constants.DOWNLOAD_HELP_KEY;

/**
 * Exoplayer官方Demo的AesDataSource
 * A {@link DataSource} that decrypts data read from an upstream source, encrypted with AES-128 with
 * a 128-bit key and PKCS7 padding.
 * <p>
 * Note that this {@link DataSource} does not support being opened from arbitrary offsets. It is
 * designed specifically for reading whole files as defined in an HLS media playlist. For this
 * reason the implementation is private to the HLS package.
 */
/* package */ final class Aes128DataSource implements DataSource {

  private final DataSource upstream;

  private CipherInputStream cipherInputStream;

  /**
   * @param upstream The upstream {@link DataSource}.
   */
  public Aes128DataSource(DataSource upstream) {
    this.upstream = upstream;
  }

  @Override
  public long open(DataSpec dataSpec) throws IOException {
    Cipher cipher;

    try {
       cipher = AESUtils.getCipher(DOWNLOAD_HELP_KEY,false);
//       cipher = AESHelper.initAESCipher(DOWNLOAD_HELP_KEY, Cipher.ENCRYPT_MODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    cipherInputStream = new CipherInputStream(
        new DataSourceInputStream(upstream, dataSpec), cipher);

    return C.LENGTH_UNSET;
  }

  @Override
  public void close() throws IOException {
    cipherInputStream = null;
    upstream.close();
  }

  @Override
  public int read(byte[] buffer, int offset, int readLength) throws IOException {
    Assertions.checkState(cipherInputStream != null);
    int bytesRead = cipherInputStream.read(buffer, offset, readLength);
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
