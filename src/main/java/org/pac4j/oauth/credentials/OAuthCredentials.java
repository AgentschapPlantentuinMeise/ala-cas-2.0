package org.pac4j.oauth.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;
import org.scribe.model.Token;

public class OAuthCredentials
        extends Credentials
{
    private static final long serialVersionUID = -7705033802712382951L;
    private final Token requestToken;
    private final String token;
    private final String verifier;

    public OAuthCredentials(String verifier, String clientName)
    {
        this.requestToken = null;
        this.token = null;
        this.verifier = verifier;
        setClientName(clientName);
    }

    public OAuthCredentials(Token requestToken, String token, String verifier, String clientName)
    {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
        setClientName(clientName);
    }

    public Token getRequestToken()
    {
        return this.requestToken;
    }

    public String getToken()
    {
        return this.token;
    }

    public String getVerifier()
    {
        return this.verifier;
    }

    public String toString()
    {
        return CommonHelper.toString(getClass(), new Object[] { "requestToken", this.requestToken, "token", this.token, "verifier", this.verifier, "clientName",
                getClientName() });
    }
}