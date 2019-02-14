package org.scribe.oauth;

public abstract interface StateOAuth20Service
{
    public abstract String getAuthorizationUrl(String paramString);
}
