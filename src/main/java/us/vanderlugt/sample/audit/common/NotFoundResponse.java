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
