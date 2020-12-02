package com.yk.collector.task.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer<T> {

    private List<T> list;

    private ReentrantLock lock = new ReentrantLock();

    private Object lockObject = new Object();

    /**
     * addAll
     *
     * @param buffer buffer
     */
    public void addBuffer(List<T> buffer) {
        try {
            lock.lock();
            if (list == null) {
                list = new ArrayList<T>();
            }
            list.addAll(buffer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * add
     *
     * @param buffer buffer
     */
    public void addBuffer(T buffer) {
        try {
            lock.lock();
            if (list == null) {
                list = new ArrayList<T>();
            }
            list.add(buffer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * get
     *
     * @param buffer buffer
     */
    public List<T> getBuffer() {
        List<T> result = new ArrayList<T>();
        try {
            lock.lock();
            if (list == null) {
                list = new ArrayList<T>();
                return result;
            }
            result.addAll(list);
            list.clear();
            list = new ArrayList<T>();
            return result;
        } finally {
            lock.unlock();
        }
    }
}
