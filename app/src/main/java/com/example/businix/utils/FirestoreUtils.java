package com.example.businix.utils;

import com.google.firebase.firestore.PropertyName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FirestoreUtils {
    public static Map<String, Object> prepareUpdates(Object object) throws IllegalAccessException {
        Map<String, Object> updates = new HashMap<>();

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String propertyName = field.getName();
            Object value = field.get(object);
            if (value != null && !propertyName.equals("id")) {
                updates.put(propertyName, value);
            }
        }

        return updates;
    }
}