package org.pac4j.core.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProfile
        implements Serializable
{
    private static final long serialVersionUID = 9020114478664816338L;
    protected static final transient Logger logger = LoggerFactory.getLogger(UserProfile.class);
    private String id;
    private final Map<String, Object> attributes = new HashMap();
    public static final transient String SEPARATOR = "#";
    private boolean isRemembered = false;
    private final List<String> roles = new ArrayList();
    private final List<String> permissions = new ArrayList();

    public void build(Object id, Map<String, Object> attributes)
    {
        setId(id);
        addAttributes(attributes);
    }

    protected AttributesDefinition getAttributesDefinition()
    {
        return null;
    }

    public void addAttribute(String key, Object value)
    {
        if (value != null)
        {
            AttributesDefinition definition = getAttributesDefinition();
            if (definition == null)
            {
                logger.debug("no conversion => key : {} / value : {} / {}", new Object[] { key, value, value
                        .getClass() });
                this.attributes.put(key, value);
            }
            else
            {
                value = definition.convert(key, value);
                if (value != null)
                {
                    logger.debug("converted to => key : {} / value : {} / {}", new Object[] { key, value, value
                            .getClass() });
                    this.attributes.put(key, value);
                }
            }
        }
    }

    public void addAttributes(Map<String, Object> attributes)
    {
        for (String key : attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
    }

    public void setId(Object id)
    {
        if (id != null)
        {
            String sId = id.toString();
            String type = getClass().getSimpleName();
            if ((type != null) && (sId.startsWith(type + "#"))) {
                sId = sId.substring(type.length() + "#".length());
            }
            logger.debug("identifier : {}", sId);
            this.id = sId;
        }
    }

    public String getId()
    {
        return this.id;
    }

    public String getTypedId()
    {
        return getClass().getSimpleName() + "#" + this.id;
    }

    public Map<String, Object> getAttributes()
    {
        return Collections.unmodifiableMap(this.attributes);
    }

    public Object getAttribute(String name)
    {
        return this.attributes.get(name);
    }

    public void addRole(String role)
    {
        this.roles.add(role);
    }

    public void addPermission(String permission)
    {
        this.permissions.add(permission);
    }

    public boolean hasAccess(String requireAnyRole, String requireAllRoles)
    {
        boolean access = true;
        if (CommonHelper.isNotBlank(requireAnyRole))
        {
            String[] expectedRoles = requireAnyRole.split(",");
            if (!hasAnyRole(expectedRoles)) {
                access = false;
            }
        }
        else if (CommonHelper.isNotBlank(requireAllRoles))
        {
            String[] expectedRoles = requireAllRoles.split(",");
            if (!hasAllRoles(expectedRoles)) {
                access = false;
            }
        }
        return access;
    }

    public boolean hasAnyRole(String[] expectedRoles)
    {
        if ((expectedRoles == null) || (expectedRoles.length == 0)) {
            return true;
        }
        for (String role : expectedRoles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllRoles(String[] expectedRoles)
    {
        if ((expectedRoles == null) || (expectedRoles.length == 0)) {
            return true;
        }
        for (String role : expectedRoles) {
            if (!this.roles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public void setRemembered(boolean rme)
    {
        this.isRemembered = rme;
    }

    public List<String> getRoles()
    {
        return Collections.unmodifiableList(this.roles);
    }

    public List<String> getPermissions()
    {
        return Collections.unmodifiableList(this.permissions);
    }

    public boolean isRemembered()
    {
        return this.isRemembered;
    }

    public String toString()
    {
        return CommonHelper.toString(getClass(), new Object[] { "id", this.id, "attributes", this.attributes, "roles", this.roles, "permissions", this.permissions, "isRemembered",
                Boolean.valueOf(this.isRemembered) });
    }
}