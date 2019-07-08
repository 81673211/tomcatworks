package com.fred.tomcatworks.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;

public final class RequestUtil {
    private static SimpleDateFormat format = new SimpleDateFormat(" EEEE, dd-MMM-yy kk:mm:ss zz");

    public RequestUtil() {
    }

    public static String encodeCookie(Cookie cookie) {
        StringBuffer buf = new StringBuffer(cookie.getName());
        buf.append("=");
        buf.append(cookie.getValue());
        if (cookie.getComment() != null) {
            buf.append("; Comment=\"");
            buf.append(cookie.getComment());
            buf.append("\"");
        }

        if (cookie.getDomain() != null) {
            buf.append("; Domain=\"");
            buf.append(cookie.getDomain());
            buf.append("\"");
        }

        if (cookie.getMaxAge() >= 0) {
            buf.append("; Max-Age=\"");
            buf.append(cookie.getMaxAge());
            buf.append("\"");
        }

        if (cookie.getPath() != null) {
            buf.append("; Path=\"");
            buf.append(cookie.getPath());
            buf.append("\"");
        }

        if (cookie.getSecure()) {
            buf.append("; Secure");
        }

        if (cookie.getVersion() > 0) {
            buf.append("; Version=\"");
            buf.append(cookie.getVersion());
            buf.append("\"");
        }

        return buf.toString();
    }

    public static String filter(String message) {
        if (message == null) {
            return null;
        } else {
            char[] content = new char[message.length()];
            message.getChars(0, message.length(), content, 0);
            StringBuffer result = new StringBuffer(content.length + 50);

            for(int i = 0; i < content.length; ++i) {
                switch(content[i]) {
                    case '"':
                        result.append("&quot;");
                        break;
                    case '&':
                        result.append("&amp;");
                        break;
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    default:
                        result.append(content[i]);
                }
            }

            return result.toString();
        }
    }

    public static String normalize(String path) {
        if (path == null) {
            return null;
        } else {
            String normalized = path;
            if (path.equals("/.")) {
                return "/";
            } else {
                if (!path.startsWith("/")) {
                    normalized = "/" + path;
                }

                while(true) {
                    int index = normalized.indexOf("//");
                    if (index < 0) {
                        while(true) {
                            index = normalized.indexOf("/./");
                            if (index < 0) {
                                while(true) {
                                    index = normalized.indexOf("/../");
                                    if (index < 0) {
                                        return normalized;
                                    }

                                    if (index == 0) {
                                        return null;
                                    }

                                    int index2 = normalized.lastIndexOf(47, index - 1);
                                    normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
                                }
                            }

                            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
                        }
                    }

                    normalized = normalized.substring(0, index) + normalized.substring(index + 1);
                }
            }
        }
    }

    public static String parseCharacterEncoding(String contentType) {
        if (contentType == null) {
            return null;
        } else {
            int start = contentType.indexOf("charset=");
            if (start < 0) {
                return null;
            } else {
                String encoding = contentType.substring(start + 8);
                int end = encoding.indexOf(59);
                if (end >= 0) {
                    encoding = encoding.substring(0, end);
                }

                encoding = encoding.trim();
                if (encoding.length() > 2 && encoding.startsWith("\"") && encoding.endsWith("\"")) {
                    encoding = encoding.substring(1, encoding.length() - 1);
                }

                return encoding.trim();
            }
        }
    }

    public static Cookie[] parseCookieHeader(String header) {
        if (header != null && header.length() >= 1) {
            ArrayList cookies = new ArrayList();

            while(header.length() > 0) {
                int semicolon = header.indexOf(59);
                if (semicolon < 0) {
                    semicolon = header.length();
                }

                if (semicolon == 0) {
                    break;
                }

                String token = header.substring(0, semicolon);
                if (semicolon < header.length()) {
                    header = header.substring(semicolon + 1);
                } else {
                    header = "";
                }

                try {
                    int equals = token.indexOf(61);
                    if (equals > 0) {
                        String name = token.substring(0, equals).trim();
                        String value = token.substring(equals + 1).trim();
                        cookies.add(new Cookie(name, value));
                    }
                } catch (Throwable var7) {
                }
            }

            return (Cookie[])cookies.toArray(new Cookie[cookies.size()]);
        } else {
            return new Cookie[0];
        }
    }

    public static void parseParameters(Map map, String data, String encoding) throws UnsupportedEncodingException {
        if (data != null && data.length() > 0) {
            byte[] bytes = null;

            try {
                if (encoding == null) {
                    bytes = data.getBytes();
                } else {
                    bytes = data.getBytes(encoding);
                }
            } catch (UnsupportedEncodingException var5) {
            }

            parseParameters(map, bytes, encoding);
        }

    }

    public static String URLDecode(String str) {
        return URLDecode((String)str, (String)null);
    }

    public static String URLDecode(String str, String enc) {
        if (str == null) {
            return null;
        } else {
            byte[] bytes = null;

            try {
                if (enc == null) {
                    bytes = str.getBytes();
                } else {
                    bytes = str.getBytes(enc);
                }
            } catch (UnsupportedEncodingException var4) {
            }

            return URLDecode(bytes, enc);
        }
    }

    public static String URLDecode(byte[] bytes) {
        return URLDecode((byte[])bytes, (String)null);
    }

    public static String URLDecode(byte[] bytes, String enc) {
        if (bytes == null) {
            return null;
        } else {
            int len = bytes.length;
            int ix = 0;

            int ox;
            byte b;
            for(ox = 0; ix < len; bytes[ox++] = b) {
                b = bytes[ix++];
                if (b == 43) {
                    b = 32;
                } else if (b == 37) {
                    b = (byte)((convertHexDigit(bytes[ix++]) << 4) + convertHexDigit(bytes[ix++]));
                }
            }

            if (enc != null) {
                try {
                    return new String(bytes, 0, ox, enc);
                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }

            return new String(bytes, 0, ox);
        }
    }

    private static byte convertHexDigit(byte b) {
        if (b >= 48 && b <= 57) {
            return (byte)(b - 48);
        } else if (b >= 97 && b <= 102) {
            return (byte)(b - 97 + 10);
        } else {
            return b >= 65 && b <= 70 ? (byte)(b - 65 + 10) : 0;
        }
    }

    private static void putMapEntry(Map map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = (String[])map.get(name);
        if (oldValues == null) {
            newValues = new String[]{value};
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }

        map.put(name, newValues);
    }

    public static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException {
        if (data != null && data.length > 0) {
            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;

            while(ix < data.length) {
                byte c = data[ix++];
                switch((char)c) {
                    case '%':
                        data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
                        break;
                    case '&':
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            putMapEntry(map, key, value);
                            key = null;
                        }

                        ox = 0;
                        break;
                    case '+':
                        data[ox++] = 32;
                        break;
                    case '=':
                        if (key == null) {
                            key = new String(data, 0, ox, encoding);
                            ox = 0;
                        } else {
                            data[ox++] = c;
                        }
                        break;
                    default:
                        data[ox++] = c;
                }
            }

            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }

    }

    static {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}
