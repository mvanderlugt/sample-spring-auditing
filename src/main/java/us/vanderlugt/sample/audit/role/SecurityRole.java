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
