package com.yk.telnet.test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/04/02 10:37:07
 */
public class NiuA
{
    private String[] ips;

    private AtomicInteger index = new AtomicInteger(0);

    public NiuA(String[] ips)
    {
        this.ips = ips;
    }

    public String getIp()
    {
        if (ips == null || ips.length == 0)
        {
            throw new RuntimeException("polling calculation error cause of ip array is empty");
        }
        return ips[this.index.getAndUpdate(p -> (p + 1) % ips.length)];
    }
}
