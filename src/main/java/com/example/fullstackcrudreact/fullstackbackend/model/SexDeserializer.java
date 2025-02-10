package com.example.fullstackcrudreact.fullstackbackend.model;

import java.io.IOException;

import com.example.fullstackcrudreact.fullstackbackend.model.UserDetails.Sex;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SexDeserializer extends JsonDeserializer<Sex> {

      @Override
    public Sex deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase(); // Convert input to uppercase
        return Sex.valueOf(value);
    }

}
