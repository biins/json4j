/*
 * Copyright 2016 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.biins.json4j.jackson;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import org.biins.json4j.BaseJson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Martin Janys
 */
public class Json extends BaseJson<Json> {

    private static final ObjectMapper SERIALIZATION_MAPPER = new ObjectMapper();
    private static final ObjectMapper DESERIALIZATION_MAPPER = new ObjectMapper();
    static {
        SERIALIZATION_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        SERIALIZATION_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SERIALIZATION_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        SERIALIZATION_MAPPER.enableDefaultTyping();

        SimpleModule module = new SimpleModule();
        module.addSerializer(Supplier.class, new StdSerializer<Supplier>(Supplier.class) {
            @Override
            public void serialize(Supplier supplier, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                jsonGenerator.writeObject(supplier.get());
            }

            @Override
            public void serializeWithType(Supplier value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                this.serialize(value, gen, serializers);
            }
        });
        SERIALIZATION_MAPPER.registerModule(module);
    }

    public Json() {
        this(SERIALIZATION_MAPPER);
    }

    @SuppressWarnings("unchecked")
    private Json(ObjectMapper objectMapper) {
        super((o) -> JacksonReflectionUtils.toLazyMap(o, objectMapper));
    }

    public Json(Map<String, Object> jsonMap) {
        super(jsonMap);
    }


    @Override
    public Json load(String jsonString) {
        try {
            return new Json(JacksonReflectionUtils.toMap(jsonString, DESERIALIZATION_MAPPER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String asString(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(getJsonData());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String asString() {
       return asString(SERIALIZATION_MAPPER);
    }

}
