package us.vanderlugt.sample.audit.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;
import us.vanderlugt.sample.audit.user.UserAccount;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class AccessRole extends BaseEntity {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<UserAccount> users;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "access_role_permissions",
            joinColumns = @JoinColumn(name = "access_role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions;
}
