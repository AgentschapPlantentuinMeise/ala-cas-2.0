package org.pac4j.oauth.client.exception;

import java.util.HashMap;
import java.util.Map;
import org.pac4j.core.exception.CredentialsException;

public class OAuthCredentialsException
        extends CredentialsException
{
    private static final long serialVersionUID = -3540979749535811079L;
    public static final String ERROR = "error";
    public static final String ERROR_REASON = "error_reason";
    public static final String ERROR_DESCRIPTION = "error_description";
    private static final String ERROR_URI = "error_uri";
    public static final String[] ERROR_NAMES = { "error", "error_reason", "error_description", "error_uri" };
    private final Map<String, String> errorMessages = new HashMap();

    public OAuthCredentialsException(String message)
    {
        super(message);
    }

    public void setErrorMessage(String name, String message)
    {
        this.errorMessages.put(name, message);
    }

    public Map<String, String> getErrorMessages()
    {
        return this.errorMessages;
    }

    public String getError()
    {
        return (String)this.errorMessages.get("error");
    }

    public String getErrorReason()
    {
        return (String)this.errorMessages.get("error_reason");
    }

    public String getErrorDescription()
    {
        return (String)this.errorMessages.get("error_description");
    }

    public String getErrorUri()
    {
        return (String)this.errorMessages.get("error_uri");
    }
}