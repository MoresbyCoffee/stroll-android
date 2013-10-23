package com.strollimo.android.utils.gson.adapters;

import com.google.gson.*;
import com.strollimo.android.models.ImageComparisonPickupMode;
import com.strollimo.android.models.PickupMode;

import java.lang.reflect.Type;

public class PickupModeTypeAdapter implements JsonSerializer<PickupMode>, JsonDeserializer<PickupMode> {

    @Override
    public JsonElement serialize(PickupMode src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }

    @Override
    public PickupMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        if (ImageComparisonPickupMode.TYPE.equals(type)) {
            return context.deserialize(jsonObject, ImageComparisonPickupMode.class);
        } else {
            return null;
        }
    }
}
