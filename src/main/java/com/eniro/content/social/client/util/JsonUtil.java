package com.eniro.content.social.client.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;

/**
 * Project: <social-hub>
 * Created by andrew on 27/08/15.
 */
public class JsonUtil {
    private static ObjectMapper objectMapper;


    private JsonUtil(){
        // empty constructor to prohibit instantiation
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
        }
        return objectMapper;
    }

    public static <T> T mergeAll(List<T> toMerge, Class<T> toCast) {
        T master = null;
        for (T eachMerge : toMerge) {
            if (master == null) {
                master = eachMerge;
            } else {
                master = merge(master, eachMerge, toCast);
            }
        }
        return master;
    }

    public static <T> T merge(T mainNode, T updateNode, Class<T> toCast) {
        JsonNode result = merge(getObjectMapper().valueToTree(mainNode), getObjectMapper().valueToTree(updateNode));
        return getObjectMapper().convertValue(result, toCast);
    }

    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        if (updateNode != null && !updateNode.isNull()) {
            Iterator<String> fieldNames = updateNode.fieldNames();
            while (fieldNames.hasNext()) {

                String fieldName = fieldNames.next();
                JsonNode jsonNode = mainNode.get(fieldName);
                //If main node does not have this field or it is empty then replace it
                if (jsonNode == null || jsonNode.isNull() || (jsonNode.isValueNode() && jsonNode.asText().length() == 0)) {
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).replace(fieldName, value);
                    continue;
                }
                if (jsonNode.isObject()) {
                    merge(jsonNode, updateNode.get(fieldName));
                }
            }
        }
        return mainNode;
    }
}
