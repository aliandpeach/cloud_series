package com.yk.message.api.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class ApiClassLoader {

    private ClassLoader loader;

    private static final String IMPL_JAR_FILE = "";

    private ApiClassLoader() {
        try {
            String path = ApiClassLoader.class.getClassLoader().getResource(IMPL_JAR_FILE).getPath();
            URI uri = new File(path).toURI();
            loader = new URLClassLoader(new URL[]{uri.toURL()}, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static ApiClassLoader getInstance() {
        return ApiClassLoaderHolder.INSTANCE;
    }

    private static class ApiClassLoaderHolder {
        public static ApiClassLoader INSTANCE = new ApiClassLoader();
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
