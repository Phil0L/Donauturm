package com.pl.donauturm.drinksmenu.util.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;

public class PolymorphicDeserializer<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            Class<?> typeClass;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
                typeClass = Class.forName(type.getTypeName());
            else typeClass = type.getClass();
            JsonType jsonType = typeClass.getDeclaredAnnotation(JsonType.class);
            if (jsonType == null)
                return context.deserialize(json, type);
            JsonSubtype[] subtypes = jsonType.value();
            JsonSubtype subType = Arrays.stream(subtypes)
                    .filter(subtype -> subtype.clazz().getSimpleName().equals(json.getAsJsonObject().get(subtype.field()).getAsString())
                    || subtype.old().equals(json.getAsJsonObject().get(subtype.field()).getAsString()))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            return context.deserialize(json, subType.clazz());
        } catch (Exception e) {
            return context.deserialize(json, type);
        }
    }
}
