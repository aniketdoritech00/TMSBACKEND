package com.doritech.tmsservice.service;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {

    private long remaining;

    public LimitedInputStream(InputStream inputStream, long limit) {
        super(inputStream);
        this.remaining = limit;
    }

    @Override
    public int read() throws IOException {

        if (remaining <= 0) {
            return -1;
        }

        int data = super.read();

        if (data != -1) {
            remaining--;
        }

        return data;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {

        if (remaining <= 0) {
            return -1;
        }

        int bytesToRead = (int) Math.min(length, remaining);

        int bytesRead = super.read(buffer, offset, bytesToRead);

        if (bytesRead > 0) {
            remaining -= bytesRead;
        }

        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException {

        long bytesToSkip = Math.min(n, remaining);

        long skipped = super.skip(bytesToSkip);

        remaining -= skipped;

        return skipped;
    }
}