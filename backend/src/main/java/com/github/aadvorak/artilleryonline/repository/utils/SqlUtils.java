package com.github.aadvorak.artilleryonline.repository.utils;

import java.util.Map;

public class SqlUtils {

    public static String makeConditionsQuery(Map<String, Object> parameters, Map<String, String> conditions) {
        StringBuilder queryBuilder = new StringBuilder();
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            String condition = conditions.get(key);
            if (condition != null && parameter != null) {
                queryBuilder.append(condition);
            }
        }
        return queryBuilder.toString();
    }
}
