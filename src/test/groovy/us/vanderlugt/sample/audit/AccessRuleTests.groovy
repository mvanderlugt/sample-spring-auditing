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

package us.vanderlugt.sample.audit

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import javax.transaction.Transactional
import java.beans.Transient

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccessRuleTests extends BaseTest {
    @Test
    @Transactional
    void createAccessRule() throws Exception {
        def role = [
                name: 'Manager',
                code: 'MANAGER'
        ]

        role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(role)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def request = [
                name       : 'Create User',
                description: 'Ability to create new users in the system'
        ]
        mvc.perform(post('/role/{id}/rule', role.id)
                .with(bearer(token))
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$[0].id').exists())
                .andExpect(jsonPath('$[0].created').doesNotExist())
                .andExpect(jsonPath('$[0].modified').doesNotExist())
                .andExpect(jsonPath('$[0].role').doesNotExist())
                .andExpect(jsonPath('$[0].name', is('Create User')))
                .andExpect(jsonPath('$[0].description', is('Ability to create new users in the system')))
                .andExpect(jsonPath('$[0].target').doesNotExist())
                .andExpect(jsonPath('$[0].condition').doesNotExist())
                .andDo(document())
    }

    @Test
    @Transactional
    void getAccessRules() throws Exception {
        def role = [
                name: 'Manager',
                code: 'MANAGER'
        ]

        role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(role)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def request = [
                name       : 'Create User',
                description: 'Ability to create new users in the system'
        ]
        mvc.perform(post('/role/{id}/rule', role.id)
                .with(bearer(token))
                .content(toJson(request)))
                .andExpect(status().isCreated())

        mvc.perform(get('/role/{id}/rule', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$[0].id').exists())
                .andExpect(jsonPath('$[0].created').doesNotExist())
                .andExpect(jsonPath('$[0].modified').doesNotExist())
                .andExpect(jsonPath('$[0].role').doesNotExist())
                .andExpect(jsonPath('$[0].name', is('Create User')))
                .andExpect(jsonPath('$[0].description', is('Ability to create new users in the system')))
                .andExpect(jsonPath('$[0].target').doesNotExist())
                .andExpect(jsonPath('$[0].condition').doesNotExist())
                .andDo(document())
    }

    @Test
    @Transactional
    void getNoAccessRules() throws Exception {
        def role = [
                name: 'Manager',
                code: 'MANAGER'
        ]

        role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(role)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(get('/role/{id}/rule', role.id)
                .with(bearer(token)))
                .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void updateAccessRule() throws Exception {
        def role = [
                name: 'Manager',
                code: 'MANAGER'
        ]

        role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(role)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def request = [
                name       : 'Create User',
                description: 'Ability to create new users in the system'
        ]
        def rules = parseJson(
                mvc.perform(post('/role/{id}/rule', role.id)
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def update = [
                name       : 'Create New User',
                description: 'Ability to create new users'
        ]
        mvc.perform(put('/role/{roleId}/rule/{ruleId}', role.id, rules[0].id)
                .with(bearer(token))
                .content(toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$[0].id').exists())
                .andExpect(jsonPath('$[0].created').doesNotExist())
                .andExpect(jsonPath('$[0].modified').doesNotExist())
                .andExpect(jsonPath('$[0].role').doesNotExist())
                .andExpect(jsonPath('$[0].name', is('Create New User')))
                .andExpect(jsonPath('$[0].description', is('Ability to create new users')))
                .andExpect(jsonPath('$[0].target').doesNotExist())
                .andExpect(jsonPath('$[0].condition').doesNotExist())
                .andDo(document())
    }

    @Test
    @Transactional
    void deleteAccessRule() throws Exception {
        def role = [
                name: 'Manager',
                code: 'MANAGER'
        ]

        role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(role)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def request = [
                name       : 'Create New User',
                description: 'Ability to create new users'
        ]
        def rules = parseJson(
                mvc.perform(post('/role/{roleId}/rule', role.id)
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(delete('/role/{roleId}/rule/{ruleId}', role.id, rules[0].id)
                .with(bearer(token)))
                .andExpect(status().isNoContent())
                .andDo(document())

        mvc.perform(get('/role/{id}/rule', role.id)
                .with(bearer(token)))
                .andExpect(status().isNoContent())
    }

//    @Test
    @DirtiesContext
    // envers writes data when the transaction closes, instead of rolling back flush the context afterwards to reset the database
    void auditAccessRole() throws Exception {
        def request = [
                name: 'Admin',
                code: 'MANAGER'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def update = [
                name: 'Administrator',
                code: 'ADMINISTRATOR'
        ]

        mvc.perform(put('/role/{id}', role.id)
                .with(bearer(token))
                .content(toJson(update)))

        mvc.perform(get('/role/{id}/audit', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('totalElements', is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath('numberOfElements', is(2)))
                .andExpect(jsonPath('content[*].id', contains(1, 2)))
                .andExpect(jsonPath('content[*].instant').exists())
                .andExpect(MockMvcResultMatchers.jsonPath('content[0].entity.name', is('Admin')))
                .andExpect(MockMvcResultMatchers.jsonPath('content[1].entity.name', is('Administrator')))
                .andDo(document())
    }
}
