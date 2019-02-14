package org.pac4j.oauth.profile.twitter;

import java.util.Locale;
import org.pac4j.core.profile.converter.FormattedDateConverter;

public final class TwitterConverters
{
    public static final FormattedDateConverter dateConverter = new FormattedDateConverter("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
}
