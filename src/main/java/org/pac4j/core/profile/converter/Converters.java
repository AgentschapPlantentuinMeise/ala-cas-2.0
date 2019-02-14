package org.pac4j.core.profile.converter;

public final class Converters
{
    public static final LocaleConverter localeConverter = new LocaleConverter();
    public static final StringConverter stringConverter = new StringConverter();
    public static final BooleanConverter booleanConverter = new BooleanConverter();
    public static final IntegerConverter integerConverter = new IntegerConverter();
    public static final LongConverter longConverter = new LongConverter();
    public static final ColorConverter colorConverter = new ColorConverter();
    public static final GenderConverter genderConverter = new GenderConverter("male", "female");
    public static final FormattedDateConverter dateConverter = new FormattedDateConverter("yyyy-MM-dd'T'HH:mm:ssz");
    public static final StringReplaceConverter urlConverter = new StringReplaceConverter("\\/", "/");
}