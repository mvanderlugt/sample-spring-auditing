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

import javax.transaction.Transactional

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SecurityRoleTests extends BaseTest {
    @Test
    @Transactional
    void createSecurityRole() throws Exception {
        def request = [
                name: 'Administrator',
                code: 'administrator'
        ]
        mvc.perform(post('/role')
                .with(bearer(token))
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('name', is('Administrator')))
                .andExpect(jsonPath('code', is('administrator')))
                .andDo(document())
    }


    @Test
    @Transactional
    void getSecurityRole() throws Exception {
        def request = [
                name: 'Administrator',
                code: 'ADMINISTRATOR'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(get('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('name', is('Administrator')))
                .andExpect(jsonPath('code', is('administrator')))
                .andDo(document())
    }

    @Test
    void getNoSecurityRoles() throws Exception {
        mvc.perform(get('/role')
                .with(bearer(token)))
                .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void getSecurityRoles() throws Exception {
        [
                [name: 'Administrator', code: 'ADMIN'],
                [name: 'Manager', code: 'MANAGER']
        ].forEach { request ->
            mvc.perform(post('/role')
                    .with(bearer(token))
                    .content(toJson(request)))
                    .andExpect(status().isCreated())
        }

        mvc.perform(get('/role')
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('numberOfElements', is(2)))
                .andExpect(jsonPath('totalElements', is(2)))
                .andExpect(jsonPath('totalPages', is(1)))
                .andExpect(jsonPath('number').doesNotExist()) //default values don't get serialized
                .andExpect(jsonPath('size', is(10)))
                .andExpect(jsonPath('content[*].id').exists())
                .andExpect(jsonPath('content[*].created').doesNotExist())
                .andExpect(jsonPath('content[*].modified').doesNotExist()) //todo tighten up assertions with sorting
                .andExpect(jsonPath('content[*].name', containsInAnyOrder('Administrator', 'Manager')))
                .andExpect(jsonPath('content[*].code', containsInAnyOrder('admin', 'manager')))
                .andDo(document())
    }

    @Test
    @Transactional
    void updateSecurityRole() throws Exception {
        def request = [
                name: 'Admin',
                code: 'ADMIN'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(get('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('name', is('Admin')))
                .andExpect(jsonPath('code', is('admin')))


        def update = [
                name: 'Administrator',
                code: 'ADMINISTRATOR'
        ]

        mvc.perform(put('/role/{id}', role.id)
                .with(bearer(token))
                .content(toJson(update)))
                .andExpect(jsonPath('id', is(role.id)))
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('name', is('Administrator')))
                .andExpect(jsonPath('code', is('administrator')))
                .andDo(document())

        mvc.perform(get('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('name', is('Administrator')))
                .andExpect(jsonPath('code', is('administrator')))

    }

    @Test
    @Transactional
    void deleteSecurityRole() throws Exception {
        def request = [
                name: 'Administrator',
                code: 'ADMINISTRATOR'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(get('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())

        mvc.perform(delete('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('name', is('Administrator')))
                .andExpect(jsonPath('code', is('administrator')))
                .andDo(document())

        mvc.perform(get('/role/{id}', role.id)
                .with(bearer(token)))
                .andExpect(status().isNotFound())
    }

    @Test
    @DirtiesContext
    // envers writes data when the transaction closes, instead of rolling back flush the context afterwards to reset the database
    void auditSecurityRole() throws Exception {
        def request = [
                name: 'Admin',
                code: 'ADMIN'
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
                .andExpect(jsonPath('totalElements', is(2)))
                .andExpect(jsonPath('numberOfElements', is(2)))
                .andExpect(jsonPath('content[*].id', contains(1, 2)))
                .andExpect(jsonPath('content[*].instant').exists())
                .andExpect(jsonPath('content[0].entity.name', is('Admin')))
                .andExpect(jsonPath('content[1].entity.name', is('Administrator')))
                .andExpect(jsonPath('content[0].entity.code', is('admin')))
                .andExpect(jsonPath('content[1].entity.code', is('administrator')))
                .andDo(document())
    }
}
