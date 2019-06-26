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

class AuditByTests extends BaseTest {
    @Test
    @DirtiesContext
    void clientCredentials() throws Exception {
        def request = [
                name       : 'Test Client',
                secret     : 'Password1',
                resourceIds: ['authorization'],
                grantTypes : ['clientCredentials'],
                scope      : ['*']
        ]
        def client = parseJson(
                mvc.perform(post('/oauth/client')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        def token = generateToken(client.id, 'Password1')

        request = [
                username : 'MarkV',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'MVanderLugt@live.com'
        ]

        def user = parseJson(
                mvc.perform(post('/user')
                        .with(bearer(token))
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(get('/user/{id}/audit', user.id)
                .with(bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('content[0].by', is(client.id)))
    }

    void password() throws Exception {
        def request = [
                username : 'MarkV',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'MVanderLugt@live.com'
        ]
        mvc.perform(post('/user')
                .with(bearer(token))
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andDo(document())
    }
}
