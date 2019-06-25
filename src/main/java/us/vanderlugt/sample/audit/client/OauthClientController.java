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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.vanderlugt.sample.audit.common.AuditRecord;
import us.vanderlugt.sample.audit.common.NotFoundResponse;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/oauth/client")
@RequiredArgsConstructor
public class OauthClientController {
    private final OauthClientRepository repository;
    private final OauthClientMapper mapper;

    /**
     * Create a new user account.
     */
    @PostMapping
    @ResponseStatus(CREATED)
    public OauthClient createClient(@Valid @RequestBody NewOauthClient newClient) {
        return repository.save(mapper.create(newClient));
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public OauthClient getClient(@PathVariable("id") UUID id, @PathVariable("id") OauthClient client) {
        if (client != null) {
            return client;
        } else {
            throw new NotFoundResponse(OauthClient.class, id);
        }
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<OauthClient> getClients(@PageableDefault Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PutMapping("/{id}")
    public OauthClient updateClient(@PathVariable("id") UUID id,
                                         @PathVariable("id") OauthClient existing,
                                         @RequestBody UpdateOauthClient update) {
        if (existing != null) {
            mapper.apply(update, existing);
            return repository.save(existing);
        } else {
            throw new NotFoundResponse(OauthClient.class, id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public OauthClient deleteClient(@PathVariable("id") UUID id, @PathVariable("id") OauthClient client) {
        if (client != null) {
            repository.delete(client);
            return client;
        } else {
            throw new NotFoundResponse(OauthClient.class, id);
        }
    }

    @GetMapping("/{id}/audit")
    @ResponseStatus(OK)
    public Page<AuditRecord<OauthClient>> getClientAuditHistory(@PathVariable("id") OauthClient client, @PageableDefault Pageable pageable) {
        return repository.findRevisions(client.getId(), pageable)
                .map(rev -> new AuditRecord<>(rev.getRevisionNumber(), rev.getRevisionInstant(), rev.getEntity()));
    }
}
