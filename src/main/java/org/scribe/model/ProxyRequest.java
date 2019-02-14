package org.scribe.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.exceptions.OAuthException;

class ProxyRequest
{
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String url;
    private final Verb verb;
    private final ParameterList querystringParams;
    private final ParameterList bodyParams;
    private final Map<String, String> headers;
    private String payload = null;
    private HttpURLConnection connection;
    private String charset;
    private byte[] bytePayload = null;
    private boolean connectionKeepAlive = false;
    private Long connectTimeout = null;
    private Long readTimeout = null;
    private String proxyHost = null;
    private int proxyPort = 8080;

    public ProxyRequest(Verb verb, String url)
    {
        this.verb = verb;
        this.url = url;
        this.querystringParams = new ParameterList();
        this.bodyParams = new ParameterList();
        this.headers = new HashMap();
    }

    public Response send()
    {
        try
        {
            createConnection();
            return doSend();
        }
        catch (Exception e)
        {
            throw new OAuthConnectionException(e);
        }
    }

    private void createConnection()
            throws IOException
    {
        String completeUrl = getCompleteUrl();
        if (this.connection == null)
        {
            if (this.connectionKeepAlive) {
                this.connection.setRequestProperty("Connection", "keep-alive");
            }
            if (StringUtils.isNotBlank(this.proxyHost))
            {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
                this.connection = ((HttpURLConnection)new URL(completeUrl).openConnection(proxy));
            }
            else
            {
                this.connection = ((HttpURLConnection)new URL(completeUrl).openConnection());
            }
        }
    }

    public String getCompleteUrl()
    {
        return this.querystringParams.appendTo(this.url);
    }

    Response doSend()
            throws IOException
    {
        this.connection.setRequestMethod(this.verb.name());
        if (this.connectTimeout != null) {
            this.connection.setConnectTimeout(this.connectTimeout.intValue());
        }
        if (this.readTimeout != null) {
            this.connection.setReadTimeout(this.readTimeout.intValue());
        }
        addHeaders(this.connection);
        if ((this.verb.equals(Verb.PUT)) || (this.verb.equals(Verb.POST))) {
            addBody(this.connection, getByteBodyContents());
        }
        return new Response(this.connection);
    }

    void addHeaders(HttpURLConnection conn)
    {
        for (String key : this.headers.keySet()) {
            conn.setRequestProperty(key, (String)this.headers.get(key));
        }
    }

    void addBody(HttpURLConnection conn, byte[] content)
            throws IOException
    {
        conn.setRequestProperty("Content-Length", String.valueOf(content.length));
        if (conn.getRequestProperty("Content-Type") == null) {
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        conn.setDoOutput(true);
        conn.getOutputStream().write(content);
    }

    public void addHeader(String key, String value)
    {
        this.headers.put(key, value);
    }

    public void addBodyParameter(String key, String value)
    {
        this.bodyParams.add(key, value);
    }

    public void addQuerystringParameter(String key, String value)
    {
        this.querystringParams.add(key, value);
    }

    public void addPayload(String payload)
    {
        this.payload = payload;
    }

    public void addPayload(byte[] payload)
    {
        this.bytePayload = payload;
    }

    public ParameterList getQueryStringParams()
    {
        try
        {
            ParameterList result = new ParameterList();
            String queryString = new URL(this.url).getQuery();
            result.addQuerystring(queryString);
            result.addAll(this.querystringParams);
            return result;
        }
        catch (MalformedURLException mue)
        {
            throw new OAuthException("Malformed URL", mue);
        }
    }

    public ParameterList getBodyParams()
    {
        return this.bodyParams;
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getSanitizedUrl()
    {
        return this.url.replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
    }

    public String getBodyContents()
    {
        try
        {
            return new String(getByteBodyContents(), getCharset());
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new OAuthException("Unsupported Charset: " + this.charset, uee);
        }
    }

    byte[] getByteBodyContents()
    {
        if (this.bytePayload != null) {
            return this.bytePayload;
        }
        String body = this.payload != null ? this.payload : this.bodyParams.asFormUrlEncodedString();
        try
        {
            return body.getBytes(getCharset());
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new OAuthException("Unsupported Charset: " + getCharset(), uee);
        }
    }

    public Verb getVerb()
    {
        return this.verb;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public String getCharset()
    {
        return this.charset == null ? Charset.defaultCharset().name() : this.charset;
    }

    public void setConnectTimeout(int duration, TimeUnit unit)
    {
        this.connectTimeout = Long.valueOf(unit.toMillis(duration));
    }

    public void setReadTimeout(int duration, TimeUnit unit)
    {
        this.readTimeout = Long.valueOf(unit.toMillis(duration));
    }

    public void setCharset(String charsetName)
    {
        this.charset = charsetName;
    }

    public void setConnectionKeepAlive(boolean connectionKeepAlive)
    {
        this.connectionKeepAlive = connectionKeepAlive;
    }

    void setConnection(HttpURLConnection connection)
    {
        this.connection = connection;
    }

    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    public String toString()
    {
        return String.format("@ProxyRequest(%s %s)", new Object[] { getVerb(), getUrl() });
    }
}
