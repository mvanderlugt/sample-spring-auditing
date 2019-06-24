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

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;

public class NoContentAdvice {
    @RestControllerAdvice
    public static class PageAdvice implements ResponseBodyAdvice<Page> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return Page.class.isAssignableFrom(returnType.getParameterType());
        }

        @Override
        public Page beforeBodyWrite(Page body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            Page result;
            if (body.getNumberOfElements() == 0) {
                result = null;
                response.setStatusCode(HttpStatus.NO_CONTENT);
            } else {
                result = body;
            }
            return result;
        }
    }

    @RestControllerAdvice
    public static class CollectionAdvice implements ResponseBodyAdvice<Collection> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return Collection.class.isAssignableFrom(returnType.getParameterType());
        }

        @Override
        public Collection beforeBodyWrite(Collection body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            Collection result;
            if (body.size() == 0) {
                result = null;
                response.setStatusCode(HttpStatus.NO_CONTENT);
            } else {
                result = body;
            }
            return result;
        }
    }
}
