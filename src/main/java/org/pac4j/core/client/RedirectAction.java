package org.pac4j.core.client;

public class RedirectAction
{
    private RedirectType type;
    private String location;
    private String content;

    public static enum RedirectType
    {
        REDIRECT,  SUCCESS;

        private RedirectType() {}
    }

    public static RedirectAction redirect(String location)
    {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.REDIRECT;
        action.location = location;
        return action;
    }

    public static RedirectAction success(String content)
    {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.SUCCESS;
        action.content = content;
        return action;
    }

    public RedirectType getType()
    {
        return this.type;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getContent()
    {
        return this.content;
    }
}
