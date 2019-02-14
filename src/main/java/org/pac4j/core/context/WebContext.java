package org.pac4j.core.context;

import java.util.Map;

public abstract interface WebContext
{
    public abstract String getRequestParameter(String paramString);

    public abstract Map<String, String[]> getRequestParameters();

    public abstract String getRequestHeader(String paramString);

    public abstract void setSessionAttribute(String paramString, Object paramObject);

    public abstract Object getSessionAttribute(String paramString);

    public abstract String getRequestMethod();

    public abstract void writeResponseContent(String paramString);

    public abstract void setResponseStatus(int paramInt);

    public abstract void setResponseHeader(String paramString1, String paramString2);

    public abstract String getServerName();

    public abstract int getServerPort();

    public abstract String getScheme();

    public abstract String getFullRequestURL();
}
