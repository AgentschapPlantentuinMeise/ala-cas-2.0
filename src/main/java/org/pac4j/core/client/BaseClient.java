package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;
import org.pac4j.core.authorization.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient<C extends Credentials, U extends CommonProfile>
        extends InitializableObject
        implements Client<C, U>, Cloneable
{
    protected static final Logger logger = LoggerFactory.getLogger(BaseClient.class);
    public static final String NEEDS_CLIENT_REDIRECTION_PARAMETER = "needs_client_redirection";
    public static final String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";
    protected String callbackUrl;
    private String name;
    private boolean enableContextualRedirects = false;
    private boolean includeClientNameInCallbackUrl = true;
    private List<AuthorizationGenerator<U>> authorizationGenerators = new ArrayList();
    private Authenticator<C> authenticator;
    private ProfileCreator<C, U> profileCreator;

    public BaseClient<C, U> clone()
    {
        BaseClient<C, U> newClient = newClient();
        newClient.setCallbackUrl(this.callbackUrl);
        newClient.setName(this.name);
        newClient.setAuthenticator(this.authenticator);
        newClient.setProfileCreator(this.profileCreator);
        return newClient;
    }

    protected abstract BaseClient<C, U> newClient();

    public void setCallbackUrl(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl()
    {
        return this.callbackUrl;
    }

    public String getContextualCallbackUrl(WebContext context)
    {
        return prependHostToUrlIfNotPresent(this.callbackUrl, context);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        if (CommonHelper.isBlank(this.name)) {
            return getClass().getSimpleName();
        }
        return this.name;
    }

    protected abstract boolean isDirectRedirection();

    public final void redirect(WebContext context, boolean requiresAuthentication, boolean ajaxRequest)
            throws RequiresHttpAction
    {
        RedirectAction action = getRedirectAction(context, requiresAuthentication, ajaxRequest);
        if (action.getType() == RedirectAction.RedirectType.REDIRECT)
        {
            context.setResponseStatus(302);
            context.setResponseHeader("Location", action.getLocation());
        }
        else if (action.getType() == RedirectAction.RedirectType.SUCCESS)
        {
            context.setResponseStatus(200);
            context.writeResponseContent(action.getContent());
        }
    }

    public final RedirectAction getRedirectAction(WebContext context, boolean requiresAuthentication, boolean ajaxRequest)
            throws RequiresHttpAction
    {
        init();
        if (ajaxRequest) {
            throw RequiresHttpAction.unauthorized("AJAX request -> 401", context, null);
        }
        String attemptedAuth = (String)context.getSessionAttribute(getName() + "$attemptedAuthentication");
        if (CommonHelper.isNotBlank(attemptedAuth))
        {
            context.setSessionAttribute(getName() + "$attemptedAuthentication", null);
            if (requiresAuthentication)
            {
                logger.error("authentication already tried and protected target -> forbidden");
                throw RequiresHttpAction.forbidden("authentication already tried -> forbidden", context);
            }
        }
        if ((isDirectRedirection()) || (requiresAuthentication)) {
            return retrieveRedirectAction(context);
        }
        String intermediateUrl = CommonHelper.addParameter(getContextualCallbackUrl(context), "needs_client_redirection", "true");

        return RedirectAction.redirect(intermediateUrl);
    }

    public String getRedirectionUrl(WebContext context)
    {
        try
        {
            return getRedirectAction(context, false, false).getLocation();
        }
        catch (RequiresHttpAction e) {}
        return null;
    }

    protected abstract RedirectAction retrieveRedirectAction(WebContext paramWebContext);

    /*
    public final C getCredentials(WebContext context) throws RequiresHttpAction
    {
        init();
        String value = context.getRequestParameter("needs_client_redirection");
        if (CommonHelper.isNotBlank(value))
        {
            RedirectAction action = retrieveRedirectAction(context);
            String message = "Needs client redirection";
            if (action.getType() == RedirectAction.RedirectType.SUCCESS) {
                throw RequiresHttpAction.ok("Needs client redirection", context, action.getContent());
            }
            throw RequiresHttpAction.redirect("Needs client redirection", context, action.getLocation());
        }
        C credentials = retrieveCredentials(context);
        if (credentials == null) {
            context.setSessionAttribute(getName() + "$attemptedAuthentication", "true");
        } else {
            context.setSessionAttribute(getName() + "$attemptedAuthentication", null);
        }
        return credentials;
    }
    */

    protected abstract C retrieveCredentials(WebContext paramWebContext)
            throws RequiresHttpAction;

    public final U getUserProfile(C credentials, WebContext context)
    {
        init();
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            return null;
        }
        U profile = retrieveUserProfile(credentials, context);
        if (this.authorizationGenerators != null) {
            for (AuthorizationGenerator<U> authorizationGenerator : this.authorizationGenerators) {
                authorizationGenerator.generate(profile);
            }
        }
        return profile;
    }

    protected abstract U retrieveUserProfile(C paramC, WebContext paramWebContext);

    public abstract Mechanism getMechanism();

    public String toString()
    {
        return CommonHelper.toString(getClass(), new Object[] { "callbackUrl", this.callbackUrl, "name", this.name, "isDirectRedirection",
                Boolean.valueOf(isDirectRedirection()), "enableContextualRedirects",
                Boolean.valueOf(isEnableContextualRedirects()) });
    }

    public boolean isEnableContextualRedirects()
    {
        return this.enableContextualRedirects;
    }

    public void setEnableContextualRedirects(boolean enableContextualRedirects)
    {
        this.enableContextualRedirects = enableContextualRedirects;
    }

    public boolean isIncludeClientNameInCallbackUrl()
    {
        return this.includeClientNameInCallbackUrl;
    }

    public void setIncludeClientNameInCallbackUrl(boolean includeClientNameInCallbackUrl)
    {
        this.includeClientNameInCallbackUrl = includeClientNameInCallbackUrl;
    }

    protected String prependHostToUrlIfNotPresent(String url, WebContext webContext)
    {
        if ((webContext != null) && (this.enableContextualRedirects) && (url != null) && (!url.startsWith("http://")) &&
                (!url.startsWith("https://")))
        {
            StringBuilder sb = new StringBuilder();

            sb.append(webContext.getScheme()).append("://").append(webContext.getServerName());
            if (webContext.getServerPort() != 80) {
                sb.append(":").append(webContext.getServerPort());
            }
            sb.append("/" + url);

            return sb.toString();
        }
        return url;
    }

    protected String getStateParameter(WebContext webContext)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addAuthorizationGenerator(AuthorizationGenerator<U> authorizationGenerator)
    {
        if (this.authorizationGenerators != null) {
            this.authorizationGenerators.add(authorizationGenerator);
        }
    }

    public List<AuthorizationGenerator<U>> getAuthorizationGenerators()
    {
        return this.authorizationGenerators;
    }

    public void setAuthorizationGenerators(List<AuthorizationGenerator<U>> authorizationGenerators)
    {
        this.authorizationGenerators = authorizationGenerators;
    }

    @Deprecated
    public void setAuthorizationGenerator(AuthorizationGenerator<U> authorizationGenerator)
    {
        addAuthorizationGenerator(authorizationGenerator);
    }

    public Authenticator<C> getAuthenticator()
    {
        return this.authenticator;
    }

    public void setAuthenticator(Authenticator<C> authenticator)
    {
        this.authenticator = authenticator;
    }

    public ProfileCreator<C, U> getProfileCreator()
    {
        return this.profileCreator;
    }

    public void setProfileCreator(ProfileCreator<C, U> profileCreator)
    {
        this.profileCreator = profileCreator;
    }
}
