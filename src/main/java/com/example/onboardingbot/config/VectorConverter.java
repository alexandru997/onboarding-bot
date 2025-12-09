package com.example.onboardingbot.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

@Converter
public class VectorConverter implements AttributeConverter<float[], PGobject> {

    @Override
    public PGobject convertToDatabaseColumn(float[] floats) {
        if (floats == null) return null;

        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("vector");

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < floats.length; i++) {
                sb.append(floats[i]);
                if (i < floats.length - 1) sb.append(",");
            }
            sb.append("]");
            pgObject.setValue(sb.toString());

            return pgObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert float[] to PGobject", e);
        }
    }

    @Override
    public float[] convertToEntityAttribute(PGobject pGobject) {
        if (pGobject == null || pGobject.getValue() == null) return null;

        try {
            String value = pGobject.getValue();
            value = value.substring(1, value.length() - 1);
            String[] parts = value.split(",");
            float[] result = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Float.parseFloat(parts[i].trim());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert PGobject to float[]", e);
        }
    }
}