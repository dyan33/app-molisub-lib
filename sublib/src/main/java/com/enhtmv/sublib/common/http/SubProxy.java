package com.enhtmv.sublib.common.http;

public class SubProxy {

    private String host;
    private String username;
    private String password;
    private int port;

    public SubProxy(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

}
