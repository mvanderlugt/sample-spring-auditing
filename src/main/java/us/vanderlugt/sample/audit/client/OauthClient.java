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

package us.vanderlugt.sample.audit.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.util.CollectionUtils;
import us.vanderlugt.sample.audit.common.BaseEntity;
import us.vanderlugt.sample.audit.common.JsonMapConverter;
import us.vanderlugt.sample.audit.common.JsonSetConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Set;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({"id", "clientId", "clientSecret", "resourceIds", "grantTypes", "scope", "authorities",
        "accessTokenExpiration", "refreshTokenExpiration", "additionalInformation", "autoApprove", "redirectUri"})
public class OauthClient extends BaseEntity {//implements ClientDetails {
    @NotNull
    @Size(min = 3, max = 100)
    private String clientId;

    @NotNull
    @JsonIgnore
    @Size(max = 100)
    private String clientSecret;

    @NotNull
    @Convert(converter = JsonSetConverter.class)
    private Set<String> resourceIds;

    @NotNull
    @Convert(converter = JsonSetConverter.class)
    private Set<String> scope;

    @NotNull
    @Convert(converter = GrantType.SetConverter.class)
    private Set<GrantType> grantTypes;

    @NotNull
    @Convert(converter = JsonSetConverter.class)
    private Set<String> redirectUri;

    @NotNull
    @Convert(converter = JsonSetConverter.class)
    private Set<String> authorities;

    @NotNull
    @Min(0)
    private Integer accessTokenExpiration;

    @NotNull
    @Min(0)
    private Integer refreshTokenExpiration;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> additionalInformation;

    @NotNull
    @Convert(converter = JsonSetConverter.class)
    private Set<String> autoApprove;

    //    @Override
    public boolean isSecretRequired() {
        return true;
    }

    //    @Override
    public boolean isScoped() {
        return !getScope().isEmpty();
    }

    //    @Override
    public Integer getAccessTokenValiditySeconds() {
        return getAccessTokenExpiration();
    }

    //    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return getRefreshTokenExpiration();
    }

    //    @Override
    public boolean isAutoApprove(String scope) {
        boolean autoApproveScope = false;
        if (!CollectionUtils.isEmpty(autoApprove)) {
            autoApproveScope = autoApprove.contains(scope);
        }
        return autoApproveScope;
    }

//    public Collection<GrantedAuthority> getAuthorities() { //todo once spring security dependency is added
//        Set<GrantedAuthority> result = new HashSet<>();
//        if (StringUtils.hasText(authorities)) {
//            result = Arrays.stream(authorities.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
//        }
//        return result;
//    }
}