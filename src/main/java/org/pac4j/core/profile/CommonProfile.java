package org.pac4j.core.profile;

import java.util.Locale;

public class CommonProfile
        extends UserProfile
{
    private static final long serialVersionUID = -1856159870249261877L;
    public static final transient String USERNAME = "username";

    public String getEmail()
    {
        return (String)getAttribute("email");
    }

    public String getFirstName()
    {
        return (String)getAttribute("first_name");
    }

    public String getFamilyName()
    {
        return (String)getAttribute("family_name");
    }

    public String getDisplayName()
    {
        return (String)getAttribute("display_name");
    }

    public String getUsername()
    {
        return (String)getAttribute("username");
    }

    public Gender getGender()
    {
        Gender gender = (Gender)getAttribute("gender");
        if (gender == null) {
            return Gender.UNSPECIFIED;
        }
        return gender;
    }

    public Locale getLocale()
    {
        return (Locale)getAttribute("locale");
    }

    public String getPictureUrl()
    {
        return (String)getAttribute("picture_url");
    }

    public String getProfileUrl()
    {
        return (String)getAttribute("profile_url");
    }

    public String getLocation()
    {
        return (String)getAttribute("location");
    }
}