package org.pac4j.oauth.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

public class OAuthAttributesDefinition
        extends AttributesDefinition {
    public static final transient String ACCESS_TOKEN = "access_token";
    public static final transient String ACCESS_SECRET = "access_secret";

    public OAuthAttributesDefinition() {
        addAttribute("access_token", Converters.stringConverter, false);
        addAttribute("access_secret", Converters.stringConverter, false);
    }
}
