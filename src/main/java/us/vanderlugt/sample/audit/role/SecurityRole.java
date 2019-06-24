package us.vanderlugt.sample.audit.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;
import us.vanderlugt.sample.audit.user.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class SecurityRole extends BaseEntity {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<UserAccount> users;

    @JsonIgnore
    @OneToMany(
            mappedBy = "role",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<AccessRule> rules = new HashSet<>();


    public Set<AccessRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    public void setRules(Set<AccessRule> rules) {
        this.rules = new HashSet<>();
        if(rules != null) {
            rules.forEach(this::add);
        }
    }

    public boolean add(AccessRule rule) {
        rule.setRole(this);
        return rules.add(rule);
    }

    public Optional<AccessRule> findRule(UUID ruleId) {
        return getRules().stream()
                .filter(r -> r.getId().equals(ruleId))
                .findFirst();
    }

    public boolean remove(AccessRule rule) {
        rule.setRole(null);
        return rules.remove(rule);
    }
}
