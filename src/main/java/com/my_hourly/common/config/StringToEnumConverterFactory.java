package com.my_hourly.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source == null) {
                return null;
            }
            String trimmed = source.trim();
            if (trimmed.isEmpty()
                    || "--".equals(trimmed)
                    || "-".equals(trimmed)
                    || "all".equalsIgnoreCase(trimmed)
                    || "none".equalsIgnoreCase(trimmed)
                    || "null".equalsIgnoreCase(trimmed)
                    || "select".equalsIgnoreCase(trimmed)) {
                return null;
            }

            for (T constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(trimmed)) {
                    return constant;
                }
            }

            throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + source);
        }
    }
}
