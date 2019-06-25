/*
 * Copyright 2019 Mark Vander Lugt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package us.vanderlugt.sample.audit.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public abstract class GenericJsonConverter<T> implements AttributeConverter<T, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract TypeReference<T> getTypeReference();

    @Override
    @SneakyThrows(IOException.class)
    public String convertToDatabaseColumn(T attribute) {
        String result = null;
        if (attribute != null) {
            result = objectMapper.writeValueAsString(attribute);
        }
        return result;
    }

    @Override
    @SneakyThrows(IOException.class)
    public T convertToEntityAttribute(String dbData) {
        T result = null;
        if (dbData != null) {
            result = objectMapper.readValue(dbData, getTypeReference());
        }
        return result;
    }
}
