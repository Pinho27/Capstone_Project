package com.example.luis.capstoneproject;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class SourceTypeConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<Source> stringToSomeObjectList(String data) {
        if (data == null)
            return Collections.emptyList();

        Type listType = new TypeToken<List<Source>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Source> someObjects) {
        return gson.toJson(someObjects);
    }
}
