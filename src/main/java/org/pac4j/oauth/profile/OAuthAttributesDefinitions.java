package org.pac4j.oauth.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.twitter.TwitterAttributesDefinition;
import org.pac4j.oauth.profile.windowslive.WindowsLiveAttributesDefinition;

public final class OAuthAttributesDefinitions {
    public static final AttributesDefinition twitterDefinition = new TwitterAttributesDefinition();
    public static final AttributesDefinition windowsLiveDefinition = new WindowsLiveAttributesDefinition();
}
