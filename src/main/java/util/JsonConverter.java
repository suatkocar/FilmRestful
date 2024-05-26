package util;

import com.google.gson.Gson;

/*
 * Utility class for converting objects to JSON and JSON to objects using Google's Gson library.
 * Implements Singleton pattern to ensure a single instance is used throughout application.
 */
public class JsonConverter {
    private static JsonConverter instance;
    private Gson gson;

    /*
     * Private constructor to enforce Singleton design pattern.
     */
    private JsonConverter() {
        this.gson = new Gson();
    }

    /*
     * Provides a global point of access to the JsonConverter instance.
     * Ensures that only one instance of JsonConverter is created.
     */
    public static synchronized JsonConverter getInstance() {
        if (instance == null) {
            instance = new JsonConverter();
        }
        return instance;
    }

    /*
     * Converts an object to its JSON representation.
     * @param object The object to be converted to JSON.
     * @return A String that represents the JSON format of the given object.
     */
    public String convertToJson(Object object) {
        return gson.toJson(object);
    }

    /*
     * Converts a JSON string back to an object of the specified type.
     * @param json The JSON string to be converted into an object.
     * @param classOfT The class of the object to which the JSON is to be converted.
     * @return An object of type T converted from the given JSON string.
     */
    public <T> T convertFromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}