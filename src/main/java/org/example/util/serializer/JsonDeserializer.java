package org.example.util.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private Class<T> deserializedClass;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (deserializedClass == null) {
            deserializedClass = (Class<T>) configs.get("serializedClass");
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        return gson.fromJson(new String(data), deserializedClass);
    }

    @Override
    public void close() {
    }
}
