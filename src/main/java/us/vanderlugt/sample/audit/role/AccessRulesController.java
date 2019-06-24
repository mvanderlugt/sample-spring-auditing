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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.vanderlugt.sample.audit.common.NotFoundResponse;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/role/{roleId}/rule")
@RequiredArgsConstructor
public class AccessRulesController {
    private final SecurityRoleRepository roleRepository;
    private final AccessRuleMapper mapper;

    @GetMapping
    @ResponseStatus(OK)
    public Collection<AccessRule> getAccessRules(@PathVariable("roleId") SecurityRole role) {
        return role.getRules();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Collection<AccessRule> addAccessRule(@PathVariable("roleId") SecurityRole role, @RequestBody NewAccessRule newRule) {
        AccessRule rule = mapper.create(newRule);
        role.add(rule);
        role = roleRepository.save(role);
        return role.getRules();
    }

    @PutMapping("/{ruleId}")
    @ResponseStatus(OK)
    public Collection<AccessRule> updateAccessRule(@PathVariable("roleId") SecurityRole role, @PathVariable("ruleId") UUID ruleId, @RequestBody UpdateAccessRule update) {
        AccessRule rule = role.findRule(ruleId)
                .orElseThrow(() -> new NotFoundResponse("rule", ruleId));
        mapper.apply(update, rule);
        role.add(rule);
        role = roleRepository.save(role);
        return role.getRules();
    }

    @PutMapping
    @ResponseStatus(OK) //todo does this make sense, if so write tests for it?
    public Collection<AccessRule> setRules(@PathVariable("roleId") SecurityRole role, @RequestBody Set<NewAccessRule> rules) {
        log.debug("Updating role {} to have {} roles", role.getName(), rules.size());
        role.setRules(rules.stream().map(mapper::create).collect(Collectors.toSet()));
        role = roleRepository.save(role);
        return role.getRules();
    }

    @DeleteMapping("/{ruleId}")
    @ResponseStatus(OK)
    public Collection<AccessRule> removeAccessRule(@PathVariable("roleId") SecurityRole role, @PathVariable("ruleId") AccessRule rule) {
        if (role.getRules().contains(rule)) {
            role.remove(rule);
            role = roleRepository.save(role);
        } else {
            throw new NotFoundResponse("role", role.getId());
        }
        return role.getRules();
    }
}
