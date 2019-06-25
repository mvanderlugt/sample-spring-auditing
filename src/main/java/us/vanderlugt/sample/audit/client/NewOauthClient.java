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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.lowerCase;

@Data
public class NewOauthClient {
    @NotNull
    @Size(min = 3, max = 255)
    private String clientId;

    @NotNull
    @Size(min = 8, max = 100)
    private String clientSecret;

    @NotNull
    @Size(min = 1)
    private Set<String> resourceIds = new HashSet<>();

    @NotNull
    @Size(min = 1)
    private Set<GrantType> grantTypes = new HashSet<>();

    @NotNull
    private Set<String> scope = new HashSet<>();

    @NotNull
    private Set<String> redirectUri = new HashSet<>();

    @NotNull
    private Set<String> grantedAuthorities = new HashSet<>();

    @NotNull
    @Min(0)
    private Integer accessTokenExpiration = 0;

    @NotNull
    @Min(0)
    private Integer refreshTokenExpiration = 0;

    @NotNull
    private Map<String, Object> additionalInformation = new HashMap<>();

    @NotNull
    private Set<String> autoApprove = new HashSet<>();

    public void setClientId(String clientId) {
        this.clientId = lowerCase(clientId);
    }
}
