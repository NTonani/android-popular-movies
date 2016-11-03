package com.nathantonani.popularmovies.sync.extras;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ntonani on 11/1/16.
 */
public class MovieExtrasDeserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        /*
        List<Object> list = new ArrayList<Object>();

        JsonArray jsonArray = json.getAsJsonArray();
        for(JsonElement obj : jsonArray){
            list.add(new Gson().fromJson(obj,typeOfT));
        }

        return (T)list;
        */

        return null;
    }
}
