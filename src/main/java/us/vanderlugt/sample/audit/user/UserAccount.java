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

package us.vanderlugt.sample.audit.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import us.vanderlugt.sample.audit.common.BaseEntity;
import us.vanderlugt.sample.audit.role.SecurityRole;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

import static javax.persistence.FetchType.EAGER;
import static org.apache.commons.lang3.StringUtils.lowerCase;

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
    private LocalDateTime expires;
    private LocalDateTime passwordExpires;
    private LocalDateTime locked;
    private LocalDateTime disabled;

    @JsonIgnore
    @Size(max = 20)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = EAGER)
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
        if (roles != null) {
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

    public void setUsername(String username) {
        this.username = lowerCase(username);
    }

    public void setEmail(String email) {
        this.email = lowerCase(email);
    }
}
