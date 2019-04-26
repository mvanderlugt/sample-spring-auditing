package us.vanderlugt.sample.audit.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Data
@ResponseStatus(BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class InvalidRequestResponse extends RuntimeException {
    private List<ValidationError> errors;

    public InvalidRequestResponse(List<ValidationError> errors) {
        super("Client ");
        this.errors = Collections.unmodifiableList(errors);
    }
}
