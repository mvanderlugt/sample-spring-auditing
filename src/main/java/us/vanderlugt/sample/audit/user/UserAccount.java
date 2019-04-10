package us.vanderlugt.sample.audit.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends BaseEntity {
    @NotNull
    @Size(min = 1, max = 100)
    private String username;
    @Size(min = 1, max = 100)
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    @Size(min = 1, max = 50)
    private String firstName;
    @Size(min = 1, max = 100)
    private String lastName;
    @Email
    private String email;
}
