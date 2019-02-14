package org.pac4j.core.profile.converter;

import java.util.Date;
import java.util.Locale;
import org.pac4j.core.profile.FormattedDate;

public class FormattedDateConverter extends DateConverter
{
    public FormattedDateConverter(String format)
    {
        super(format);
    }

    public FormattedDateConverter(String format, Locale locale)
    {
        super(format, locale);
    }

    public FormattedDate convert(Object attribute)
    {
        Object result = super.convert(attribute);
        if ((result != null) && ((result instanceof Date))) {
            return new FormattedDate((Date)result, this.format, this.locale);
        }
        return null;
    }
}
