package com.fred.tomcatworks;

import com.fred.tomcatworks.connector.http.HttpConnector;

public class Bootstrap {

    public static void main(String[] args) {
        new HttpConnector().startup();
    }
}
