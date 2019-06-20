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
class RoleTests extends BaseTest {
    @Test
    @Transactional
    void createAccessRole() throws Exception {
        def request = [
                name: 'Administrator'
        ]
        mvc.perform(post('/role')
                .content(toJson(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('name', is('Administrator')))
           .andDo(document())
    }


    @Test
    @Transactional
    void getAccessRole() throws Exception {
        def request = [
                name: 'Administrator'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/role/{id}', role.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('deleted').doesNotExist())
           .andExpect(jsonPath('name', is('Administrator')))
           .andDo(document())
    }

    @Test
    void getNoAccessRoles() throws Exception {
        mvc.perform(get('/role'))
           .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void getAccessRoles() throws Exception {
        [
                [name: 'Administrator'],
                [name: 'Manager']
        ].forEach { request ->
            mvc.perform(post('/role')
                    .content(toJson(request)))
               .andExpect(status().isCreated())
        }

        mvc.perform(get('/role'))
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
           .andDo(document())
    }

    @Test
    @Transactional
    void updateAccessRole() throws Exception {
        def request = [
                name: 'Admin'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(request)))
                   .andReturn())

        mvc.perform(get('/role/{id}', role.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('name', is('Admin')))

        def update = [
                name: 'Administrator'
        ]

        mvc.perform(put('/role/{id}', role.id)
                .content(toJson(update)))
           .andExpect(jsonPath('id', is(role.id)))
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('name', is('Administrator')))
           .andDo(document())

        mvc.perform(get('/role/{id}', role.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('name', is('Administrator')))
    }

    @Test
    @Transactional
    void deleteAccessRole() throws Exception {
        def request = [
                name: 'Administrator'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(get('/role/{id}', role.id))
           .andExpect(status().isOk())

        mvc.perform(delete('/role/{id}', role.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('id').exists())
           .andExpect(jsonPath('created').doesNotExist())
           .andExpect(jsonPath('modified').doesNotExist())
           .andExpect(jsonPath('name', is('Administrator')))
           .andDo(document())

        mvc.perform(get('/role/{id}', role.id))
           .andExpect(status().isNotFound())
    }

    @Test
    @DirtiesContext
    // envers writes data when the transaction closes, instead of rolling back flush the context afterwards to reset the database
    void auditAccessRole() throws Exception {
        def request = [
                name: 'Admin'
        ]
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(request)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def update = [
                name: 'Administrator'
        ]

        mvc.perform(put('/role/{id}', role.id)
                .content(toJson(update)))

        mvc.perform(get('/role/{id}/audit', role.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('totalElements', is(2)))
           .andExpect(jsonPath('numberOfElements', is(2)))
           .andExpect(jsonPath('content[*].id', contains(1, 2)))
           .andExpect(jsonPath('content[*].instant').exists())
           .andExpect(jsonPath('content[0].entity.name', is('Admin')))
           .andExpect(jsonPath('content[1].entity.name', is('Administrator')))
           .andDo(document())
    }
}
