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

package us.vanderlugt.sample.audit.client;

import com.fasterxml.jackson.core.type.TypeReference;
import us.vanderlugt.sample.audit.common.GenericJsonConverter;

import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Set;

enum GrantType {
    clientCredentials("client_credentials"),
    password("password"),
    refreshToken("refresh_token"),
    authorizationCode("authorization_code"),
    implicit("implicit");

    private final String code;

    GrantType(String code) {
        this.code = code;
    }

    public static GrantType get(String code) {
        return Arrays.stream(values()).filter(grantType -> grantType.getCode().equals(code)).findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }

    @Converter
    public static class SetConverter extends GenericJsonConverter<Set<GrantType>> {
        private static final TypeReference<Set<GrantType>> TYPE_REFERENCE = new TypeReference<>() {};

        protected TypeReference<Set<GrantType>> getTypeReference() {
            return TYPE_REFERENCE;
        }
    }
}
