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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Data
@ResponseStatus(NOT_FOUND)
@EqualsAndHashCode(callSuper = false)
public class NotFoundResponse extends RuntimeException {
    private String entityName;
    private String search;

    public NotFoundResponse(Class entity) {
        this(entity, null);
    }

    public NotFoundResponse(Class entity, Object search) {
        this(entity.getSimpleName(), search);
    }

    public NotFoundResponse(String entityName, Object search) {
        super("Resource not found type " + entityName + " searching for " + search);
        this.entityName = entityName;
        this.search = search != null ? search.toString() : null;
    }

    String getCode() {
        return getEntityName() + ".not.found";
    }

    String getDefaultMessage() {
        return StringUtils.capitalize(getEntityName()) + " was not found" + (getSearch() != null ? " for " + getSearch() : "");
    }

    Object[] getMessageArguments() {
        return new Object[]{getEntityName(), getSearch()};
    }
}
