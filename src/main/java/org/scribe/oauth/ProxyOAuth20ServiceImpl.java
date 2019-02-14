package org.scribe.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

public class ProxyOAuth20ServiceImpl
        extends OAuth20ServiceImpl
{
    protected final DefaultApi20 api;
    protected final OAuthConfig config;
    protected final int connectTimeout;
    protected final int readTimeout;
    protected final String proxyHost;
    protected final int proxyPort;
    protected final boolean getParameter;
    protected final boolean addGrantType;

    public ProxyOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort)
    {
        this(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, true, false);
    }

    public ProxyOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort, boolean getParameter, boolean addGrantType)
    {
        super(api, config);
        this.api = api;
        this.config = config;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.getParameter = getParameter;
        this.addGrantType = addGrantType;
    }

    public Token getAccessToken(Token requestToken, Verifier verifier)
    {
        OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(), this.api.getAccessTokenEndpoint(), this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);
        if (this.getParameter)
        {
            request.addQuerystringParameter("client_id", this.config.getApiKey());
            request.addQuerystringParameter("client_secret", this.config.getApiSecret());
            request.addQuerystringParameter("code", verifier.getValue());
            request.addQuerystringParameter("redirect_uri", this.config.getCallback());
            if (this.config.hasScope()) {
                request.addQuerystringParameter("scope", this.config.getScope());
            }
            if (this.addGrantType) {
                request.addQuerystringParameter("grant_type", "authorization_code");
            }
        }
        else
        {
            request.addBodyParameter("client_id", this.config.getApiKey());
            request.addBodyParameter("client_secret", this.config.getApiSecret());
            request.addBodyParameter("code", verifier.getValue());
            request.addBodyParameter("redirect_uri", this.config.getCallback());
            if (this.config.hasScope()) {
                request.addBodyParameter("scope", this.config.getScope());
            }
            if (this.addGrantType) {
                request.addBodyParameter("grant_type", "authorization_code");
            }
        }
        Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }
}
