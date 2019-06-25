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

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ExtendWith([SpringExtension.class, RestDocumentationExtension.class])
class OauthClientTests extends BaseTest {
    @Test
    @Transactional
    void createOauthClient() throws Exception {
        def request = [
                clientId : 'test_client',
                clientSecret : 'Password1',
                resourceIds: ['authorization'],
                grantTypes    : ['password']
        ]
        mvc.perform(post('/oauth/client')
                .content(toJson(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('clientId', is('test_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('authorization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('password')))
           .andDo(document())
    }

    @Test
    @Transactional
    void getOauthClient() throws Exception {
        def request = [
                clientId : 'TEST_CLIENT',
                clientSecret : 'Password1',
                resourceIds: ['authorization'],
                grantTypes    : ['password']
        ]
        def client = parseJson(
                mvc.perform(post('/oauth/client')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/oauth/client/{id}', client.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('clientId', is('test_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('authorization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('password')))
           .andDo(document())
    }

    @Test
    void getNoOauthClients() throws Exception {
        mvc.perform(get('/oauth/client'))
           .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void getOauthClients() throws Exception {
        [
                [
                        clientId : 'TEST_CLIENT',
                        clientSecret : 'Password1',
                        resourceIds: ['authorization'],
                        grantTypes    : ['password']
                ],
                [
                        clientId : 'ANOTHER_CLIENT',
                        clientSecret : 'Password17',
                        resourceIds: ['organization'],
                        grantTypes    : ['implicit']
                ]
        ].forEach { request ->
            mvc.perform(post('/oauth/client')
                    .content(toJson(request)))
               .andExpect(status().isCreated())
        }

        mvc.perform(get('/oauth/client'))
           .andExpect(status().isOk())
           .andExpect(MockMvcResultMatchers.jsonPath('numberOfElements', is(2)))
           .andExpect(MockMvcResultMatchers.jsonPath('totalElements', is(2)))
           .andExpect(MockMvcResultMatchers.jsonPath('totalPages', is(1)))
           .andExpect(jsonPath('number').doesNotExist()) //default values don't get serialized
           .andExpect(MockMvcResultMatchers.jsonPath('size', is(10)))
           .andExpect(jsonPath('content[*].id').exists())
           .andExpect(jsonPath('content[*].created').doesNotExist())
           .andExpect(jsonPath('content[*].modified').doesNotExist()) //todo tighten up assertions with sorting
           .andExpect(jsonPath('content[*].deleted').doesNotExist())
           .andExpect(jsonPath('content[*].clientId', containsInAnyOrder('test_client', 'another_client')))
           .andExpect(jsonPath('content[*].clientSecret').doesNotExist())
           .andExpect(jsonPath('content[*].resourceIds[*]', containsInAnyOrder('authorization', 'organization')))
           .andExpect(jsonPath('content[*].grantTypes[*]', containsInAnyOrder('password', 'implicit')))
           .andDo(document())
    }

    @Test
    @Transactional
    void updateOauthClient() throws Exception {
        def request = [
                clientId : 'TEST_CLIENT',
                clientSecret : 'Password1',
                resourceIds: ['authorization'],
                grantTypes    : ['password']
        ]
        def client = parseJson(
                mvc.perform(post('/oauth/client')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/oauth/client/{id}', client.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('clientId', is('test_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('authorization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('password')))

        def update = [
                clientId : 'ANOTHER_CLIENT',
                clientSecret : 'Password17',
                resourceIds: ['organization'],
                grantTypes    : ['implicit']
        ]

        mvc.perform(put('/oauth/client/{id}', client.id)
                .content(toJson(update)))
           .andExpect(MockMvcResultMatchers.jsonPath('id', is(client.id)))
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('deleted').doesNotExist())
           .andExpect(jsonPath('clientId', is('another_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('organization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('implicit')))
           .andDo(document())

        mvc.perform(get('/oauth/client/{id}', client.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('clientId', is('another_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('organization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('implicit')))
    }

    @Test
    @Transactional
    void deleteOauthClient() throws Exception {
        def request = [
                clientId : 'TEST_CLIENT',
                clientSecret : 'Password1',
                resourceIds: ['authorization'],
                grantTypes    : ['password']
        ]
        def client = parseJson(
                mvc.perform(post('/oauth/client')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(get('/oauth/client/{id}', client.id))
           .andExpect(status().isOk())

        mvc.perform(delete('/oauth/client/{id}', client.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('clientId', is('test_client')))
           .andExpect(jsonPath('clientSecret').doesNotExist())
           .andExpect(jsonPath('resourceIds', containsInAnyOrder('authorization')))
           .andExpect(jsonPath('grantTypes', containsInAnyOrder('password')))
           .andDo(document())

        mvc.perform(get('/oauth/client/{id}', client.id))
           .andExpect(status().isNotFound())
    }

    @Test
    @DirtiesContext
    // envers writes data when the transaction closes, instead of rolling back flush the context afterwards to reset the database
    void auditOauthClient() throws Exception {
        def request = [
                clientId : 'TEST_CLIENT',
                clientSecret : 'Password1',
                resourceIds: ['authorization'],
                grantTypes    : ['password']
        ]
        def client = parseJson(
                mvc.perform(post('/oauth/client')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def update = [
                clientId : 'ANOTHER_CLIENT',
                clientSecret : 'Password17',
                resourceIds: ['organization'],
                grantTypes    : ['implicit']
        ]

        mvc.perform(put('/oauth/client/{id}', client.id)
                .content(toJson(update)))

        mvc.perform(get('/oauth/client/{id}/audit', client.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('totalElements', is(2)))
           .andExpect(jsonPath('numberOfElements', is(2)))
           .andExpect(jsonPath('content[*].id', contains(1, 2)))
           .andExpect(jsonPath('content[*].instant').exists())
           .andExpect(jsonPath('content[0].entity.clientId', is('test_client')))
           .andExpect(jsonPath('content[1].entity.clientId', is('another_client')))
           .andExpect(jsonPath('content[0].entity.resourceIds[*]', containsInAnyOrder('authorization')))
           .andExpect(jsonPath('content[1].entity.resourceIds[*]', containsInAnyOrder('organization')))
           .andExpect(jsonPath('content[0].entity.grantTypes[*]', containsInAnyOrder('password')))
           .andExpect(jsonPath('content[1].entity.grantTypes[*]', containsInAnyOrder('implicit')))
           .andDo(document())
    }
}
