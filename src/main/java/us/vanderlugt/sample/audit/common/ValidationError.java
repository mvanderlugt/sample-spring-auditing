package us.vanderlugt.sample.audit.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public interface ValidationError {
    String getCode();

    String getDefaultMessage();

    @JsonIgnore
    HttpStatus getHttpStatus();

    default Object[] getMessageArguments() {
        return new Object[]{};
    }
}
