package com.fast.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;


public class ObjectMapperUtils {
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(Object jsonStr, Class<T> valueType) {
        if (jsonStr == null) {
            return null;
        }
        try {
            if (jsonStr instanceof  String) {
                return mapper.readValue((String) jsonStr, valueType);
            } else if (jsonStr instanceof byte[]) {
                return mapper.readValue((byte[]) jsonStr, valueType);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param jsonStr
     * @param valueType
     * @param collectionType
     * @param <E>
     * @param <T>
     * @return
     *
     * ObjectMapperUtils.mapper.readValue(allRoute, new TypeReference<List<RouteDefinition>>(){});
     */
    public static <E, T extends Collection<E>> T fromJson(String jsonStr, Class<E> valueType,
                                                          Class<? extends Collection> collectionType) {
        try {
            return mapper.readValue(jsonStr, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String routeStr = "{\"id\":\"zookeeper_test\",\"predicates\":[{\"name\":\"Path\",\"args\":{\"_genkey_0\":\"/zookeeper_test\"}}],\"filters\":[],\"uri\":\"https://www.bilibili.com/\",\"order\":0}";
        RouteDefinition routeDefinition = ObjectMapperUtils.fromJson(routeStr, RouteDefinition.class);
        System.out.println(routeDefinition);
    }
}
