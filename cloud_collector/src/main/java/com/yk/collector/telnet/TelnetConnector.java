package com.yk.collector.telnet;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * telnet连接的客户端
 */
public class TelnetConnector {
    private static TelnetClient tc = null;

    private BufferedOutputStream out;

    private BufferedInputStream in;

    public String getResult() {

        StringBuffer result = new StringBuffer();
        while (true) {
            break;
        }
        return result.toString();
    }

    public void connect(String username, StringBuffer pwd) {

    }

}
