package org.pac4j.core.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonHelper
{
    private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);
    public static final String RESOURCE_PREFIX = "resource:";

    public static boolean isNotBlank(String s)
    {
        if (s == null) {
            return false;
        }
        return s.trim().length() > 0;
    }

    public static boolean isBlank(String s)
    {
        return !isNotBlank(s);
    }

    public static boolean areEquals(String s1, String s2)
    {
        return s1 == null ? false : s2 == null ? true : s1.equals(s2);
    }

    public static boolean areNotEquals(String s1, String s2)
    {
        return !areEquals(s1, s2);
    }

    public static void assertTrue(boolean value, String message)
    {
        if (!value) {
            throw new TechnicalException(message);
        }
    }

    public static void assertNotBlank(String name, String value)
    {
        assertTrue(!isBlank(value), name + " cannot be blank");
    }

    public static void assertNotNull(String name, Object obj)
    {
        assertTrue(obj != null, name + " cannot be null");
    }

    public static String addParameter(String url, String name, String value)
    {
        if (url != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (name != null)
            {
                if (url.indexOf("?") >= 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                sb.append(name);
                sb.append("=");
                if (value != null) {
                    sb.append(encodeText(value));
                }
            }
            return sb.toString();
        }
        return null;
    }

    private static String encodeText(String text)
    {
        try
        {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Unable to encode text : {} / {}", text, e);
            throw new TechnicalException(e);
        }
    }

    public static String toString(Class<?> clazz, Object... args)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(clazz.getSimpleName());
        sb.append("> |");
        boolean b = true;
        for (Object arg : args)
        {
            if (b)
            {
                sb.append(" ");
                sb.append(arg);
                sb.append(":");
            }
            else
            {
                sb.append(" ");
                sb.append(arg);
                sb.append(" |");
            }
            b = !b;
        }
        return sb.toString();
    }

    public static InputStream getInputStreamFromName(String name)
    {
        if (name.startsWith("resource:"))
        {
            String path = name.substring("resource:".length());
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return CommonHelper.class.getResourceAsStream(path);
        }
        try
        {
            return new FileInputStream(name);
        }
        catch (FileNotFoundException e)
        {
            throw new TechnicalException(e);
        }
    }
}