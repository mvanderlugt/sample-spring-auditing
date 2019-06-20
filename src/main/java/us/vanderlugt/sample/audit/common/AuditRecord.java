package us.vanderlugt.sample.audit.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "instant", "entity"})
public class AuditRecord<T> {
    private Optional<Integer> id;
    private Optional<Instant> instant;
    private T entity;
}
