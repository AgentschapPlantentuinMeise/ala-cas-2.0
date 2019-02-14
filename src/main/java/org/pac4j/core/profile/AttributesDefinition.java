package org.pac4j.core.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pac4j.core.profile.converter.AttributeConverter;

public class AttributesDefinition {
    protected List<String> allAttributesNames = new ArrayList();
    protected List<String> principalAttributesNames = new ArrayList();
    protected List<String> otherAttributesNames = new ArrayList();
    protected Map<String, AttributeConverter<? extends Object>> attributesConverters = new HashMap();

    public List<String> getAllAttributes()
    {
        return this.allAttributesNames;
    }

    public List<String> getPrincipalAttributes()
    {
        return this.principalAttributesNames;
    }

    public List<String> getOtherAttributes()
    {
        return this.otherAttributesNames;
    }

    protected void addAttribute(String name, AttributeConverter<? extends Object> converter)
    {
        addAttribute(name, converter, true);
    }

    protected void addAttribute(String name, AttributeConverter<? extends Object> converter, boolean principal)
    {
        this.allAttributesNames.add(name);
        this.attributesConverters.put(name, converter);
        if (principal) {
            this.principalAttributesNames.add(name);
        } else {
            this.otherAttributesNames.add(name);
        }
    }

    public Object convert(String name, Object value)
    {
        AttributeConverter<? extends Object> converter = (AttributeConverter)this.attributesConverters.get(name);
        if ((converter != null) && (value != null)) {
            return converter.convert(value);
        }
        return null;
    }
}