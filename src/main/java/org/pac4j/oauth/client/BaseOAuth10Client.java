package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseOAuth10Client<U extends OAuth10Profile>
        extends BaseOAuthClient<U>
{
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth10Client.class);
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String REQUEST_TOKEN = "requestToken";

    protected String getRequestTokenSessionAttributeName()
    {
        return getName() + "#" + "requestToken";
    }

    protected String retrieveAuthorizationUrl(WebContext context)
    {
        Token requestToken = this.service.getRequestToken();
        logger.debug("requestToken : {}", requestToken);

        context.setSessionAttribute(getRequestTokenSessionAttributeName(), requestToken);
        String authorizationUrl = this.service.getAuthorizationUrl(requestToken);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }

    protected OAuthCredentials getOAuthCredentials(WebContext context)
    {
        String tokenParameter = context.getRequestParameter("oauth_token");
        String verifierParameter = context.getRequestParameter("oauth_verifier");
        if ((tokenParameter != null) && (verifierParameter != null))
        {
            Token tokenSession = (Token)context.getSessionAttribute(getRequestTokenSessionAttributeName());
            logger.debug("tokenRequest : {}", tokenSession);
            String token = OAuthEncoder.decode(tokenParameter);
            String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("token : {} / verifier : {}", token, verifier);
            return new OAuthCredentials(tokenSession, token, verifier, getName());
        }
        String message = "No credential found";
        logger.error("No credential found");
        throw new OAuthCredentialsException("No credential found");
    }

    protected Token getAccessToken(OAuthCredentials credentials)
    {
        Token tokenRequest = credentials.getRequestToken();
        String token = credentials.getToken();
        String verifier = credentials.getVerifier();
        logger.debug("tokenRequest : {}", tokenRequest);
        logger.debug("token : {}", token);
        logger.debug("verifier : {}", verifier);
        if (tokenRequest == null)
        {
            String message = "Token request expired";
            logger.error("Token request expired");
            throw new OAuthCredentialsException("Token request expired");
        }
        String savedToken = tokenRequest.getToken();
        logger.debug("savedToken : {}", savedToken);
        if ((savedToken == null) || (!savedToken.equals(token)))
        {
            String message = "Token received : " + token + " is different from saved token : " + savedToken;
            logger.error(message);
            throw new OAuthCredentialsException(message);
        }
        Verifier clientVerifier = new Verifier(verifier);
        Token accessToken = this.service.getAccessToken(tokenRequest, clientVerifier);
        logger.debug("accessToken : {}", accessToken);
        return accessToken;
    }

    protected boolean isDirectRedirection()
    {
        return false;
    }

    protected void addAccessTokenToProfile(U profile, Token accessToken)
    {
        super.addAccessTokenToProfile(profile, accessToken);
        profile.setAccessSecret(accessToken.getSecret());
    }
}