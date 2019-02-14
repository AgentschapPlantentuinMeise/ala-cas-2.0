package org.pac4j.core.credentials;

import java.io.Serializable;

public abstract class Credentials
        implements Serializable
{
    private static final long serialVersionUID = 4864923514027378583L;
    private String clientName;

    public String getClientName()
    {
        return this.clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }
}