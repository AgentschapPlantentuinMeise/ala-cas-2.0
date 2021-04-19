package au.org.ala.cas.facebook;

import org.scribe.builder.api.StateApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * Copy of the FacebookApi class that provides a FacebookJsonTokenExtractor instead of the standard Token20ExtractorImpl.
 * This is the only change compared to the regular FacebookApi
 */
public class ExtendedExtendedFacebookApi extends StateApi20 {

    private static final String AUTHORIZE_URL_WITH_STATE = "https://www.facebook.com/v2.2/dialog/oauth?client_id=%s&redirect_uri=%s&state=%s";
    private static final String SCOPED_AUTHORIZE_URL_WITH_STATE = AUTHORIZE_URL_WITH_STATE + "&scope=%s";

    /**
     * This is the only change from the FacebookApi
     */
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new FacebookJsonTokenExtractor();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://graph.facebook.com/v2.2/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig config, final String state) {
        Preconditions.checkEmptyString(config.getCallback(),
                "Must provide a valid url as callback. Facebook does not support OOB");

        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                    OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(state),
                    OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                    OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(state));
        }
    }
}
