package org.example.util.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.Charset;
import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {

    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public void configure(Map configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return gson.toJson(data).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public void close() {
    }
}
