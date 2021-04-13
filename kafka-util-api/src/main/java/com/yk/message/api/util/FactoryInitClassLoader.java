package com.yk.message.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactoryInitClassLoader
{
    private static final Logger logger = LoggerFactory.getLogger(FactoryInitClassLoader.class);
    
    private ClassLoader loader;
    
    private static final String IMPL_JAR_FILE = ".jar";
    
    private static final String IMPL_LIB_DIR = FactoryInitClassLoader.class.getClassLoader().getResource("/").getPath();
    
    private static final String IMPL_LIB_DIR2 = FactoryInitClassLoader.class.getResource("/").getPath();
    
    private FactoryInitClassLoader()
    {
        logger.debug("IMPL_LIB_DIR = {}", IMPL_LIB_DIR);
        File lib = new File(IMPL_LIB_DIR);
        Optional.ofNullable(lib.listFiles()).orElseThrow(() -> new RuntimeException("FactoryInitClassLoader listFiles is empty"));
        List<File> list = Arrays.stream(lib.listFiles()).filter(file -> file.getName().endsWith(IMPL_JAR_FILE)).collect(Collectors.toList());
        List<URL> urls = list.stream().map(file -> apply(file)).filter(Objects::nonNull).collect(Collectors.toList());
        
        loader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }
    
    private static URL apply(File file)
    {
        try
        {
            URL url = file.toURI().toURL();
            return url;
        }
        catch (MalformedURLException e)
        {
            logger.error("url MalformedURLException error", e);
            return null;
        }
    }
    
    @FunctionalInterface
    static interface WrapperFunction<T, R>
    {
        R apply(T t) throws Exception;
    }
    
    public static FactoryInitClassLoader getInstance()
    {
        return MsgClassLoaderHolder.INSTANCE;
    }
    
    private static class MsgClassLoaderHolder
    {
        public static FactoryInitClassLoader INSTANCE = new FactoryInitClassLoader();
    }
    
    public ClassLoader getLoader()
    {
        return loader;
    }
}
