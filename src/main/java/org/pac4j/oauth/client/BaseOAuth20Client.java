package org.pac4j.oauth.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.oauth.StateOAuth20Service;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseOAuth20Client<U extends OAuth20Profile>
        extends BaseOAuthClient<U>
{
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth20Client.class);
    public static final String OAUTH_CODE = "code";
    private static final String STATE_PARAMETER = "#oauth20StateParameter";

    protected String retrieveAuthorizationUrl(WebContext context)
    {

        String authorizationUrl;
        if (requiresStateParameter())
        {
            String randomState = getStateParameter(context);
            logger.debug("Random state parameter: {}", randomState);
            context.setSessionAttribute(getName() + "#oauth20StateParameter", randomState);
            authorizationUrl = ((StateOAuth20Service)this.service).getAuthorizationUrl(randomState);
        }
        else
        {
            authorizationUrl = this.service.getAuthorizationUrl(null);
        }
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }

    protected abstract boolean requiresStateParameter();

    protected String getStateParameter(WebContext webContext)
    {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    protected OAuthCredentials getOAuthCredentials(WebContext context)
    {
        if (requiresStateParameter())
        {
            String sessionState = (String)context.getSessionAttribute(getName() + "#oauth20StateParameter");
            String stateParameter = context.getRequestParameter("state");
            logger.debug("sessionState : {} / stateParameter : {}", sessionState, stateParameter);
            if ((stateParameter == null) || (!stateParameter.equals(sessionState)))
            {
                String message = "Missing state parameter : session expired or possible threat of cross-site request forgery";
                logger.error("Missing state parameter : session expired or possible threat of cross-site request forgery");
                throw new OAuthCredentialsException("Missing state parameter : session expired or possible threat of cross-site request forgery");
            }
        }
        String verifierParameter = context.getRequestParameter("code");
        if (verifierParameter != null)
        {
            String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("verifier : {}", verifier);
            return new OAuthCredentials(verifier, getName());
        }
        String message = "No credential found";
        logger.error("No credential found");
        throw new OAuthCredentialsException("No credential found");
    }

    protected Token getAccessToken(OAuthCredentials credentials)
    {
        String verifier = credentials.getVerifier();
        logger.debug("verifier : {}", verifier);
        Verifier clientVerifier = new Verifier(verifier);
        Token accessToken = this.service.getAccessToken(null, clientVerifier);
        logger.debug("accessToken : {}", accessToken);
        return accessToken;
    }

    protected boolean isDirectRedirection()
    {
        return true;
    }
}