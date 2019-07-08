package com.fred.tomcatworks.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {
    private static final byte CR = 13;
    private static final byte LF = 10;
    private static final byte SP = 32;
    private static final byte HT = 9;
    private static final byte COLON = 58;
    private static final int LC_OFFSET = -32;
    protected byte[] buf;
    protected int count;
    protected int pos;
    protected InputStream is;

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        this.buf = new byte[bufferSize];
    }

    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        if (requestLine.methodEnd != 0) {
            requestLine.recycle();
        }

        boolean var2 = false;

        int chr;
        do {
            try {
                chr = this.read();
            } catch (IOException var8) {
                chr = -1;
            }
        } while(chr == 13 || chr == 10);

        if (chr == -1) {
            throw new EOFException("requestStream.readline.error");
        } else {
            --this.pos;
            int maxRead = requestLine.method.length;
            int readCount = 0;

            boolean space;
            for(space = false; !space; ++this.pos) {
                if (readCount >= maxRead) {
                    if (2 * maxRead > 1024) {
                        throw new IOException("requestStream.readline.toolong");
                    }

                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.method, 0, newBuffer, 0, maxRead);
                    requestLine.method = newBuffer;
                    maxRead = requestLine.method.length;
                }

                if (this.pos >= this.count) {
                    int val = this.read();
                    if (val == -1) {
                        throw new IOException("requestStream.readline.error");
                    }

                    this.pos = 0;
                }

                if (this.buf[this.pos] == 32) {
                    space = true;
                }

                requestLine.method[readCount] = (char)this.buf[this.pos];
                ++readCount;
            }

            requestLine.methodEnd = readCount - 1;
            maxRead = requestLine.uri.length;
            readCount = 0;
            space = false;

            char[] newBuffer;
            boolean eol;
            int val;
            for(eol = false; !space; ++this.pos) {
                if (readCount >= maxRead) {
                    if (2 * maxRead > 32768) {
                        throw new IOException("requestStream.readline.toolong");
                    }

                    newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.uri, 0, newBuffer, 0, maxRead);
                    requestLine.uri = newBuffer;
                    maxRead = requestLine.uri.length;
                }

                if (this.pos >= this.count) {
                    val = this.read();
                    if (val == -1) {
                        throw new IOException("requestStream.readline.error");
                    }

                    this.pos = 0;
                }

                if (this.buf[this.pos] == 32) {
                    space = true;
                } else if (this.buf[this.pos] == 13 || this.buf[this.pos] == 10) {
                    eol = true;
                    space = true;
                }

                requestLine.uri[readCount] = (char)this.buf[this.pos];
                ++readCount;
            }

            requestLine.uriEnd = readCount - 1;
            maxRead = requestLine.protocol.length;

            for(readCount = 0; !eol; ++this.pos) {
                if (readCount >= maxRead) {
                    if (2 * maxRead > 1024) {
                        throw new IOException("requestStream.readline.toolong");
                    }

                    newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.protocol, 0, newBuffer, 0, maxRead);
                    requestLine.protocol = newBuffer;
                    maxRead = requestLine.protocol.length;
                }

                if (this.pos >= this.count) {
                    val = this.read();
                    if (val == -1) {
                        throw new IOException("requestStream.readline.error");
                    }

                    this.pos = 0;
                }

                if (this.buf[this.pos] != 13) {
                    if (this.buf[this.pos] == 10) {
                        eol = true;
                    } else {
                        requestLine.protocol[readCount] = (char)this.buf[this.pos];
                        ++readCount;
                    }
                }
            }

            requestLine.protocolEnd = readCount;
        }
    }

    public void readHeader(HttpHeader header) throws IOException {
        if (header.nameEnd != 0) {
            header.recycle();
        }

        int chr = this.read();
        if (chr != 13 && chr != 10) {
            --this.pos;
            int maxRead = header.name.length;
            int readCount = 0;

            for(boolean colon = false; !colon; ++this.pos) {
                if (readCount >= maxRead) {
                    if (2 * maxRead > 128) {
                        throw new IOException("requestStream.readline.toolong");
                    }

                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(header.name, 0, newBuffer, 0, maxRead);
                    header.name = newBuffer;
                    maxRead = header.name.length;
                }

                if (this.pos >= this.count) {
                    int val = this.read();
                    if (val == -1) {
                        throw new IOException("requestStream.readline.error");
                    }

                    this.pos = 0;
                }

                if (this.buf[this.pos] == 58) {
                    colon = true;
                }

                char val = (char)this.buf[this.pos];
                if (val >= 'A' && val <= 'Z') {
                    val = (char)(val - -32);
                }

                header.name[readCount] = val;
                ++readCount;
            }

            header.nameEnd = readCount - 1;
            maxRead = header.value.length;
            readCount = 0;
            boolean eol = false;
            boolean validLine = true;

            while(true) {
                while(validLine) {
                    boolean space = true;

                    while(true) {
                        int ch;
                        while(space) {
                            if (this.pos >= this.count) {
                                ch = this.read();
                                if (ch == -1) {
                                    throw new IOException("requestStream.readline.error");
                                }

                                this.pos = 0;
                            }

                            if (this.buf[this.pos] != 32 && this.buf[this.pos] != 9) {
                                space = false;
                            } else {
                                ++this.pos;
                            }
                        }

                        for(; !eol; ++this.pos) {
                            if (readCount >= maxRead) {
                                if (2 * maxRead > 4096) {
                                    throw new IOException("requestStream.readline.toolong");
                                }

                                char[] newBuffer = new char[2 * maxRead];
                                System.arraycopy(header.value, 0, newBuffer, 0, maxRead);
                                header.value = newBuffer;
                                maxRead = header.value.length;
                            }

                            if (this.pos >= this.count) {
                                ch = this.read();
                                if (ch == -1) {
                                    throw new IOException("requestStream.readline.error");
                                }

                                this.pos = 0;
                            }

                            if (this.buf[this.pos] != 13) {
                                if (this.buf[this.pos] == 10) {
                                    eol = true;
                                } else {
                                    ch = this.buf[this.pos] & 255;
                                    header.value[readCount] = (char)ch;
                                    ++readCount;
                                }
                            }
                        }

                        ch = this.read();
                        if (ch != 32 && ch != 9) {
                            --this.pos;
                            validLine = false;
                            break;
                        }

                        eol = false;
                        if (readCount >= maxRead) {
                            if (2 * maxRead > 4096) {
                                throw new IOException("requestStream.readline.toolong");
                            }

                            char[] newBuffer = new char[2 * maxRead];
                            System.arraycopy(header.value, 0, newBuffer, 0, maxRead);
                            header.value = newBuffer;
                            maxRead = header.value.length;
                        }

                        header.value[readCount] = ' ';
                        ++readCount;
                        break;
                    }
                }

                header.valueEnd = readCount;
                return;
            }
        } else {
            if (chr == 13) {
                this.read();
            }

            header.nameEnd = 0;
            header.valueEnd = 0;
        }
    }

    public int read() throws IOException {
        if (this.pos >= this.count) {
            this.fill();
            if (this.pos >= this.count) {
                return -1;
            }
        }

        return this.buf[this.pos++] & 255;
    }

    public int available() throws IOException {
        return this.count - this.pos + this.is.available();
    }

    public void close() throws IOException {
        if (this.is != null) {
            this.is.close();
            this.is = null;
            this.buf = null;
        }
    }

    protected void fill() throws IOException {
        this.pos = 0;
        this.count = 0;
        int nRead = this.is.read(this.buf, 0, this.buf.length);
        if (nRead > 0) {
            this.count = nRead;
        }

    }
}
