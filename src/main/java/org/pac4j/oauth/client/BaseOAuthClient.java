package org.pac4j.oauth.client;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseOAuthClient<U extends OAuth20Profile>
        extends BaseClient<OAuthCredentials, U>
{
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthClient.class);
    protected OAuthService service;
    protected String key;
    protected String secret;
    protected boolean tokenAsHeader = false;
    protected int connectTimeout = 500;
    protected int readTimeout = 2000;
    protected String proxyHost = null;
    protected int proxyPort = 8080;

    protected void internalInit()
    {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
    }

    public BaseOAuthClient<U> clone()
    {
        BaseOAuthClient<U> newClient = (BaseOAuthClient)super.clone();
        newClient.setKey(this.key);
        newClient.setSecret(this.secret);
        newClient.setConnectTimeout(this.connectTimeout);
        newClient.setReadTimeout(this.readTimeout);
        newClient.setProxyHost(this.proxyHost);
        newClient.setProxyPort(this.proxyPort);
        return newClient;
    }

    protected RedirectAction retrieveRedirectAction(WebContext context)
    {
        try
        {
            return RedirectAction.redirect(retrieveAuthorizationUrl(context));
        }
        catch (OAuthException e)
        {
            throw new TechnicalException(e);
        }
    }

    protected abstract String retrieveAuthorizationUrl(WebContext paramWebContext);

    protected OAuthCredentials retrieveCredentials(WebContext context)
    {
        if (hasBeenCancelled(context))
        {
            logger.debug("authentication has been cancelled by user");
            return null;
        }
        try
        {
            boolean errorFound = false;
            OAuthCredentialsException oauthCredentialsException = new OAuthCredentialsException("Failed to retrieve OAuth credentials, error parameters found");

            String errorMessage = "";
            for (String key : OAuthCredentialsException.ERROR_NAMES)
            {
                String value = context.getRequestParameter(key);
                if (value != null)
                {
                    errorFound = true;
                    errorMessage = errorMessage + key + " : '" + value + "'; ";
                    oauthCredentialsException.setErrorMessage(key, value);
                }
            }
            if (errorFound)
            {
                logger.error(errorMessage);
                throw oauthCredentialsException;
            }
            return getOAuthCredentials(context);
        }
        catch (OAuthException e)
        {
            throw new TechnicalException(e);
        }
    }

    protected abstract boolean hasBeenCancelled(WebContext paramWebContext);

    protected abstract OAuthCredentials getOAuthCredentials(WebContext paramWebContext);

    protected U retrieveUserProfile(OAuthCredentials credentials, WebContext context)
    {
        try
        {
            Token token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(token);
        }
        catch (OAuthException e)
        {
            throw new TechnicalException(e);
        }
    }

    public U getUserProfile(String accessToken)
    {
        init();
        try
        {
            Token token = new Token(accessToken, "");
            return retrieveUserProfileFromToken(token);
        }
        catch (OAuthException e)
        {
            throw new TechnicalException(e);
        }
    }

    protected abstract Token getAccessToken(OAuthCredentials paramOAuthCredentials);

    protected U retrieveUserProfileFromToken(Token accessToken)
    {
        String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken : " + accessToken);
        }
        U profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }

    protected abstract String getProfileUrl(Token paramToken);

    protected String sendRequestForData(Token accessToken, String dataUrl)
    {
        logger.debug("accessToken : {} / dataUrl : {}", accessToken, dataUrl);
        long t0 = System.currentTimeMillis();
        ProxyOAuthRequest request = createProxyRequest(dataUrl);
        this.service.signRequest(accessToken, request);
        if (isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + accessToken.getToken());
        }
        Response response = request.send();
        int code = response.getCode();
        String body = response.getBody();
        long t1 = System.currentTimeMillis();
        logger.debug("Request took : " + (t1 - t0) + " ms for : " + dataUrl);
        logger.debug("response code : {} / response body : {}", Integer.valueOf(code), body);
        if (code != 200)
        {
            logger.error("Failed to get data, code : " + code + " / body : " + body);
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    public String sendRequestForData(OAuth10Profile profile, String dataUrl)
    {
        String secret = profile.getAccessSecret();
        Token accessToken = new Token(profile.getAccessToken(), secret == null ? "" : secret);
        return sendRequestForData(accessToken, dataUrl);
    }

    protected ProxyOAuthRequest createProxyRequest(String url)
    {
        return new ProxyOAuthRequest(Verb.GET, url, this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);
    }

    protected abstract U extractUserProfile(String paramString);

    protected void addAccessTokenToProfile(U profile, Token accessToken)
    {
        if (profile != null)
        {
            String token = accessToken.getToken();
            logger.debug("add access_token : {} to profile", token);
            profile.setAccessToken(token);
        }
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
    }

    public String getKey()
    {
        return this.key;
    }

    public String getSecret()
    {
        return this.secret;
    }

    public int getConnectTimeout()
    {
        return this.connectTimeout;
    }

    public int getReadTimeout()
    {
        return this.readTimeout;
    }

    public String getProxyHost()
    {
        return this.proxyHost;
    }

    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort()
    {
        return this.proxyPort;
    }

    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    public boolean isTokenAsHeader()
    {
        return this.tokenAsHeader;
    }

    public void setTokenAsHeader(boolean tokenAsHeader)
    {
        this.tokenAsHeader = tokenAsHeader;
    }

    public Mechanism getMechanism()
    {
        return Mechanism.OAUTH_PROTOCOL;
    }
}