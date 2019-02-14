package org.pac4j.core.credentials;

public abstract interface Authenticator<T extends Credentials>
{
    public abstract void validate(T paramT);
}
