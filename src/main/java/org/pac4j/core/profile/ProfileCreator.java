package org.pac4j.core.profile;

import org.pac4j.core.credentials.Credentials;

public abstract interface ProfileCreator<C extends Credentials, U extends UserProfile>
{
    public abstract U create(C paramC);
}
