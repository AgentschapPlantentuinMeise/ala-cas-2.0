package org.pac4j.core.exception;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

public class RequiresHttpAction
        extends Exception
{
    private static final long serialVersionUID = -3959659239684160075L;
    protected int code;

    protected RequiresHttpAction(String message, int code)
    {
        super(message);
        this.code = code;
    }

    public static RequiresHttpAction redirect(String message, WebContext context, String url)
    {
        context.setResponseHeader("Location", url);
        context.setResponseStatus(302);
        return new RequiresHttpAction(message, 302);
    }

    public static RequiresHttpAction ok(String message, WebContext context)
    {
        return ok(message, context, "");
    }

    public static RequiresHttpAction ok(String message, WebContext context, String content)
    {
        context.setResponseStatus(200);
        context.writeResponseContent(content);
        return new RequiresHttpAction(message, 200);
    }

    public static RequiresHttpAction unauthorized(String message, WebContext context, String realmName)
    {
        if (CommonHelper.isNotBlank(realmName)) {
            context.setResponseHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
        }
        context.setResponseStatus(401);
        return new RequiresHttpAction(message, 401);
    }

    public static RequiresHttpAction forbidden(String message, WebContext context)
    {
        context.setResponseStatus(403);
        return new RequiresHttpAction(message, 403);
    }

    public int getCode()
    {
        return this.code;
    }

    public String toString()
    {
        return CommonHelper.toString(RequiresHttpAction.class, new Object[] { "code", Integer.valueOf(this.code) });
    }
}
