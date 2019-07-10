package com.fred.tomcatworks;

import com.fred.tomcatworks.connector.http.HttpRequest;
import com.fred.tomcatworks.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(HttpRequest request, HttpResponse response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
