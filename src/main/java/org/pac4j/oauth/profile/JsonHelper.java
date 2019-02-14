package org.pac4j.oauth.profile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonHelper
{
    private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static JsonNode getFirstNode(String text)
    {
        try
        {
            return (JsonNode)mapper.readValue(text, JsonNode.class);
        }
        catch (JsonParseException e)
        {
            logger.error("JsonParseException", e);
        }
        catch (JsonMappingException e)
        {
            logger.error("JsonMappingException", e);
        }
        catch (IOException e)
        {
            logger.error("IOException", e);
        }
        return null;
    }

    public static Object get(JsonNode json, String name)
    {
        if ((json != null) && (name != null))
        {
            JsonNode node = json;
            for (String nodeName : name.split("\\.")) {
                if (node != null) {
                    node = node.get(nodeName);
                }
            }
            if (node != null)
            {
                if (node.isNumber()) {
                    return node.numberValue();
                }
                if (node.isBoolean()) {
                    return Boolean.valueOf(node.booleanValue());
                }
                if (node.isTextual()) {
                    return node.textValue();
                }
                return node;
            }
        }
        return null;
    }

    public static Object convert(AttributeConverter<? extends Object> converter, JsonNode json, String name)
    {
        return converter.convert(get(json, name));
    }
}