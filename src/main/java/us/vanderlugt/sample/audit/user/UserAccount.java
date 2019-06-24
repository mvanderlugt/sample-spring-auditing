package us.vanderlugt.sample.audit.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;
import us.vanderlugt.sample.audit.role.SecurityRole;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends BaseEntity {
    @NotNull
    @Size(min = 1, max = 100)
    private String username;
    @JsonIgnore
    @Size(min = 1, max = 100)
    private String password;
    @Size(min = 1, max = 50)
    private String firstName;
    @Size(min = 1, max = 100)
    private String lastName;
    @Email
    private String email;

    @JsonIgnore
    @Size(max = 20)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_account_security_roles",
            joinColumns = @JoinColumn(
                    name = "user_account_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "security_role_id", referencedColumnName = "id"))
    private Set<SecurityRole> roles = new HashSet<>();


    public Set<SecurityRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(Set<SecurityRole> roles) {
        if(roles != null) {
            this.roles = new HashSet<>(roles);
        } else {
            this.roles = new HashSet<>();
        }
    }

    public boolean add(SecurityRole role) {
        return roles.add(role);
    }

    public Optional<SecurityRole> findRole(UUID roleId) {
        return getRoles().stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst();
    }

    public boolean remove(SecurityRole role) {
        return roles.remove(role);
    }
}
