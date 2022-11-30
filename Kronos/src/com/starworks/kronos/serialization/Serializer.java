package com.starworks.kronos.serialization;

import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class Serializer {
    
    // GSON wrapper
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    public static <T> T fromJson(Reader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonReader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    public static <T> T fromJson(JsonReader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
    
    public static <T> T fromJson(JsonElement json, TypeToken<T> typeOfT) {
        return gson.fromJson(json, typeOfT.getType());
    }
}
