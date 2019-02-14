package org.pac4j.core.profile.converter;

import org.pac4j.core.util.CommonHelper;

public final class StringReplaceConverter
        implements AttributeConverter<String>
{
    private final String regex;
    private final String replacement;

    public StringReplaceConverter(String regex, String replacement)
    {
        this.regex = regex;
        this.replacement = replacement;
    }

    public String convert(Object attribute)
    {
        if ((attribute != null) && ((attribute instanceof String)))
        {
            String s = (String)attribute;
            if ((CommonHelper.isNotBlank(s)) && (CommonHelper.isNotBlank(this.regex)) &&
                    (CommonHelper.isNotBlank(this.replacement))) {
                return s.replaceAll(this.regex, this.replacement);
            }
        }
        return null;
    }
}