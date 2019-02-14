package org.scribe.oauth;

import org.scribe.builder.api.StateApi20;
import org.scribe.model.OAuthConfig;

public class StateOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl implements StateOAuth20Service
{
    public StateOAuth20ServiceImpl(StateApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort)
    {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    public StateOAuth20ServiceImpl(StateApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort, boolean getParameter, boolean addGrantType)
    {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, getParameter, addGrantType);
    }

    public String getAuthorizationUrl(String state)
    {
        return ((StateApi20)this.api).getAuthorizationUrl(this.config, state);
    }
}
