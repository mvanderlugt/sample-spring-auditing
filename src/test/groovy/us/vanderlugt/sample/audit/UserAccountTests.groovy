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

@SpringBootTest
@ExtendWith([SpringExtension.class, RestDocumentationExtension.class])
class UserAccountTests extends BaseTest {
    @Test
    @Transactional
    void createUser() throws Exception {
        def request = [
                username : 'MarkV',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'MVanderLugt@live.com'
        ]
        mvc.perform(post('/user')
                .content(toJson(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('markv')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Mark')))
           .andExpect(jsonPath('lastName', is('Vander Lugt')))
           .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
           .andDo(document())
    }

    @Test
    @Transactional
    void getUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/user/{id}', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('mvanderlugt')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Mark')))
           .andExpect(jsonPath('lastName', is('Vander Lugt')))
           .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
           .andDo(document())
    }

    @Test
    void getNoUsers() throws Exception {
        mvc.perform(get('/user'))
           .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void getUsers() throws Exception {
        [
                [
                        username : 'mvanderlugt',
                        password : 'Password1',
                        firstName: 'Mark',
                        lastName : 'Vander Lugt',
                        email    : 'mvanderlugt@live.com'
                ],
                [
                        username : 'bobbyb',
                        password : 'sn@ckpacks',
                        firstName: 'Bobby',
                        lastName : 'Boucher',
                        email    : 'bobby@snackpacks.com'
                ]
        ].forEach { request ->
            mvc.perform(post('/user')
                    .content(toJson(request)))
               .andExpect(status().isCreated())
        }

        mvc.perform(get('/user'))
           .andExpect(status().isOk())
           .andExpect(jsonPath('numberOfElements', is(2)))
           .andExpect(jsonPath('totalElements', is(2)))
           .andExpect(jsonPath('totalPages', is(1)))
           .andExpect(jsonPath('number').doesNotExist()) //default values don't get serialized
           .andExpect(jsonPath('size', is(10)))
           .andExpect(jsonPath('content[*].id').exists())
           .andExpect(jsonPath('content[*].created').doesNotExist())
           .andExpect(jsonPath('content[*].modified').doesNotExist()) //todo tighten up assertions with sorting
           .andExpect(jsonPath('content[*].username', containsInAnyOrder('mvanderlugt', 'bobbyb')))
           .andExpect(jsonPath('content[*].password').doesNotExist())
           .andExpect(jsonPath('content[*].firstName', containsInAnyOrder('Mark', 'Bobby')))
           .andExpect(jsonPath('content[*].lastName', containsInAnyOrder('Vander Lugt', 'Boucher')))
           .andExpect(jsonPath('content[*].email', containsInAnyOrder('mvanderlugt@live.com', 'bobby@snackpacks.com')))
           .andDo(document())
    }

    @Test
    @Transactional
    void updateUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/user/{id}', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('mvanderlugt')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Mark')))
           .andExpect(jsonPath('lastName', is('Vander Lugt')))
           .andExpect(jsonPath('email', is('mvanderlugt@live.com')))

        def update = [
                username : 'bboucher',
                password : 'Password12345',
                firstName: 'Bobby',
                lastName : 'Boucher',
                email    : 'bobby@boucher.com'
        ]

        mvc.perform(put('/user/{id}', user.id)
                .content(toJson(update)))
           .andExpect(jsonPath('id', is(user.id)))
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('bboucher')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Bobby')))
           .andExpect(jsonPath('lastName', is('Boucher')))
           .andExpect(jsonPath('email', is('bobby@boucher.com')))
           .andDo(document())

        mvc.perform(get('/user/{id}', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('bboucher')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Bobby')))
           .andExpect(jsonPath('lastName', is('Boucher')))
           .andExpect(jsonPath('email', is('bobby@boucher.com')))
    }

    @Test
    @Transactional
    void deleteUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(get('/user/{id}', user.id))
           .andExpect(status().isOk())

        mvc.perform(delete('/user/{id}', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('username', is('mvanderlugt')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Mark')))
           .andExpect(jsonPath('lastName', is('Vander Lugt')))
           .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
           .andDo(document())

        mvc.perform(get('/user/{id}', user.id))
           .andExpect(status().isNotFound())
    }

    @Test
    @DirtiesContext
    // envers writes data when the transaction closes, instead of rolling back flush the context afterwards to reset the database
    void auditUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def update = [
                username : 'bboucher',
                password : 'Password12345',
                firstName: 'Bobby',
                lastName : 'Boucher',
                email    : 'bobby@boucher.com'
        ]

        mvc.perform(put('/user/{id}', user.id)
                .content(toJson(update)))

        mvc.perform(get('/user/{id}/audit', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('totalElements', is(2)))
           .andExpect(jsonPath('numberOfElements', is(2)))
           .andExpect(jsonPath('content[*].id', contains(1, 2)))
           .andExpect(jsonPath('content[*].instant').exists())
           .andExpect(jsonPath('content[0].entity.username', is('mvanderlugt')))
           .andExpect(jsonPath('content[1].entity.username', is('bboucher')))
           .andExpect(jsonPath('content[0].entity.firstName', is('Mark')))
           .andExpect(jsonPath('content[1].entity.firstName', is('Bobby')))
           .andExpect(jsonPath('content[0].entity.lastName', is('Vander Lugt')))
           .andExpect(jsonPath('content[1].entity.lastName', is('Boucher')))
           .andExpect(jsonPath('content[0].entity.email', is('mvanderlugt@live.com')))
           .andExpect(jsonPath('content[1].entity.email', is('bobby@boucher.com')))
           .andDo(document())
    }
}
