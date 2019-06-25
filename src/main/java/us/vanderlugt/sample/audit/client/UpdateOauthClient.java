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

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.lowerCase;

@Data
public class UpdateOauthClient {
    @Size(min = 3, max = 255)
    private String clientId;

    @Size(min = 8, max = 100)
    private String clientSecret;

    private Set<String> resourceIds;

    private Set<String> scope;

    private Set<GrantType> grantTypes;

    private Set<String> redirectUri;

    private Set<String> grantedAuthorities;

    @Min(0)
    private Integer accessTokenExpiration;

    @Min(0)
    private Integer refreshTokenExpiration;

    private Map<String, Object> additionalInformation;

    private Set<String> autoApprove;

    public void setClientId(String clientId) {
        this.clientId = lowerCase(clientId);
    }
}
