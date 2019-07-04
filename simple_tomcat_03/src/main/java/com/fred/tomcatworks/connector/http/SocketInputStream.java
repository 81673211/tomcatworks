package com.fred.tomcatworks.connector.http;

import java.io.InputStream;

public class SocketInputStream extends InputStream {

    private InputStream is;
    private byte[] buf;

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        this.buf = new byte[bufferSize];
    }

    public void readRequestLine(HttpRequestLine requestLine) {
    }

    public void readHeader(HttpHeader header) {

    }

    @Override
    public int read() {
        return 0;
    }
}
