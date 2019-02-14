package org.pac4j.core.authorization;

import org.pac4j.core.profile.CommonProfile;

public abstract interface AuthorizationGenerator<U extends CommonProfile>
{
    public abstract void generate(U paramU);
}
