package org.scribe.builder.api;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

public abstract class StateApi20 extends DefaultApi20 {
    public String getAuthorizationUrl(OAuthConfig config)
    {
        throw new UnsupportedOperationException("Cannot invoke getAuthorizationUrl without state parameter");
    }

    public abstract String getAuthorizationUrl(OAuthConfig paramOAuthConfig, String paramString);
}

