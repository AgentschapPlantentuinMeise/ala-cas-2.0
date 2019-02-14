package org.scribe.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProxyOAuthRequest extends OAuthRequest
{
    private final ProxyRequest proxyRequest;

    public ProxyOAuthRequest(Verb verb, String url, int connectTimeout, int readTimeout, String proxyHost, int proxyPort)
    {
        super(verb, url);
        this.proxyRequest = new ProxyRequest(verb, url);
        if (connectTimeout != 0) {
            this.proxyRequest.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }
        if (readTimeout != 0) {
            this.proxyRequest.setReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        this.proxyRequest.setProxyHost(proxyHost);
        this.proxyRequest.setProxyPort(proxyPort);
    }

    public Response send()
    {
        return this.proxyRequest.send();
    }

    public String getCompleteUrl()
    {
        return this.proxyRequest.getCompleteUrl();
    }

    void addHeaders(HttpURLConnection conn)
    {
        this.proxyRequest.addHeaders(conn);
    }

    void addBody(HttpURLConnection conn, byte[] content)
            throws IOException
    {
        this.proxyRequest.addBody(conn, content);
    }

    public void addHeader(String key, String value)
    {
        this.proxyRequest.addHeader(key, value);
    }

    public void addBodyParameter(String key, String value)
    {
        this.proxyRequest.addBodyParameter(key, value);
    }

    public void addQuerystringParameter(String key, String value)
    {
        this.proxyRequest.addQuerystringParameter(key, value);
    }

    public void addPayload(String payload)
    {
        this.proxyRequest.addPayload(payload);
    }

    public void addPayload(byte[] payload)
    {
        this.proxyRequest.addPayload(payload);
    }

    public ParameterList getQueryStringParams()
    {
        return this.proxyRequest.getQueryStringParams();
    }

    public ParameterList getBodyParams()
    {
        return this.proxyRequest.getBodyParams();
    }

    public String getUrl()
    {
        return this.proxyRequest.getUrl();
    }

    public String getSanitizedUrl()
    {
        return this.proxyRequest.getSanitizedUrl();
    }

    public String getBodyContents()
    {
        return this.proxyRequest.getBodyContents();
    }

    byte[] getByteBodyContents()
    {
        return this.proxyRequest.getByteBodyContents();
    }

    public Verb getVerb()
    {
        return this.proxyRequest.getVerb();
    }

    public Map<String, String> getHeaders()
    {
        return this.proxyRequest.getHeaders();
    }

    public String getCharset()
    {
        return this.proxyRequest.getCharset();
    }

    public void setCharset(String charsetName)
    {
        this.proxyRequest.setCharset(charsetName);
    }

    public void setConnectionKeepAlive(boolean connectionKeepAlive)
    {
        this.proxyRequest.setConnectionKeepAlive(connectionKeepAlive);
    }

    public String toString()
    {
        return String.format("@ProxyOAuthRequest(%s %s)", new Object[] { getVerb(), getUrl() });
    }
}