package com.yk.collector.telnet;

import java.util.Arrays;
import java.util.Objects;

public class TargetInfo {
    private String ip;

    private String username;

    private char[] pwd;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPwd() {
        return pwd;
    }

    public void setPwd(char[] pwd) {
        this.pwd = pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetInfo that = (TargetInfo) o;
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ip, username);
        return result;
    }
}
