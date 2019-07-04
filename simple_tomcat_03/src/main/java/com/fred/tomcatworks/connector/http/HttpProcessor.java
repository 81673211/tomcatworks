package com.fred.tomcatworks.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {

    private HttpConnector httpConnector;
    private HttpRequest request;
    private HttpResponse response;
    private HttpRequestLine httpRequestLine;

    public HttpProcessor(HttpConnector httpConnector) {
        this.httpConnector = httpConnector;
    }

    public void process(Socket socket) {
        SocketInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new SocketInputStream(socket.getInputStream(), 2048);
            outputStream = socket.getOutputStream();

            request = new HttpRequest(inputStream);
            response = new HttpResponse(outputStream);
            response.setRequest(request);
            response.setHeader("Server", "fred's server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpRequest httpRequest = new HttpRequest(inputStream);
    }

    private void parseRequest(SocketInputStream inputStream, OutputStream outputStream) {

    }
}
