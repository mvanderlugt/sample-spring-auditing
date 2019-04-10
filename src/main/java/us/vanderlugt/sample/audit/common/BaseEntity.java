package us.vanderlugt.sample.audit.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@Audited
@MappedSuperclass
@EqualsAndHashCode(exclude = {"created", "modified"})
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @NotNull
    @JsonProperty(access = READ_ONLY)
    private UUID id = UUID.randomUUID();
    @CreatedDate
    @JsonIgnore
    private LocalDateTime created;
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime modified;
}
