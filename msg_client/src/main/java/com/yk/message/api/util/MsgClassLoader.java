package com.yk.message.api.util;

import cn.hutool.core.io.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MsgClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(MsgClassLoader.class);

    private ClassLoader loader;

    private static final String IMPL_JAR_FILE = ".jar";

    private static final String IMPL_LIB_DIR = MsgClassLoader.class.getResource("/").getPath();

    private MsgClassLoader() {
        logger.debug("IMPL_LIB_DIR = {}", IMPL_LIB_DIR);
        File lib = new File(IMPL_LIB_DIR);
        Optional.ofNullable(lib.listFiles()).orElseThrow(() -> new RuntimeException("MsgClassLoader listFiles is empty"));
        List<File> list = Arrays.stream(lib.listFiles()).filter(file -> file.getName().endsWith(IMPL_JAR_FILE)).collect(Collectors.toList());
        List<URL> urls = list.stream().map(file -> apply(file)).filter(url -> url != null).collect(Collectors.toList());

        loader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }

    private static URL apply(File file) {
        try {
            URL url = file.toURI().toURL();
            return url;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @FunctionalInterface
    static interface WrapperFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public static MsgClassLoader getInstance() {
        return MsgClassLoaderHolder.INSTANCE;
    }

    private static class MsgClassLoaderHolder {
        public static MsgClassLoader INSTANCE = new MsgClassLoader();
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
