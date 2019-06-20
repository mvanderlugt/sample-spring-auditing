package us.vanderlugt.sample.audit

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.junit.jupiter.SpringExtension

import javax.transaction.Transactional

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ExtendWith([SpringExtension.class, RestDocumentationExtension.class])
class UserRolesTests extends BaseTest {
    @Test
    @Transactional
    void addAccessRoleToUser() throws Exception {
        def userRequest = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(userRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def roleRequest = [name: 'Administrator']
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role.id)))
           .andExpect(status().isOk())
           .andExpect(jsonPath('$').isArray())
           .andExpect(jsonPath('$[*].id', containsInAnyOrder(role.id)))
           .andExpect(jsonPath('$[*].name', containsInAnyOrder('Administrator')))
           .andDo(document())
    }

    @Test
    @Transactional
    void addInvalidRoleToUser() throws Exception {
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

        request = UUID.randomUUID()
        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(request)))
           .andExpect(status().isNotFound())
    }

    @Test
    @Transactional
    void getUserAccessRoles() throws Exception {
        def userRequest = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(userRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def roleRequest = [name: 'Administrator']
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role.id)))
           .andExpect(status().isOk())

        roleRequest = [name: 'Manager']
        def role2 = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role2.id)))
           .andExpect(status().isOk())

        mvc.perform(get('/user/{id}/role', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('$').isArray())
           .andExpect(jsonPath('$[*].id', containsInAnyOrder(role.id, role2.id)))
           .andExpect(jsonPath('$[*].name', containsInAnyOrder('Administrator', 'Manager')))
           .andDo(document())
    }

    @Test
    @Transactional
    void removeAccessRoleFromUser() throws Exception {
        def userRequest = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(userRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def roleRequest = [name: 'Administrator']
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role.id)))
           .andExpect(status().isOk())

        mvc.perform(get('/user/{id}/role', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('$').isArray())
           .andExpect(jsonPath('$[*].id', containsInAnyOrder(role.id)))
           .andExpect(jsonPath('$[*].name', containsInAnyOrder('Administrator')))

        mvc.perform(delete('/user/{userId}/role/{roleId}', user.id, role.id))
           .andExpect(status().isNoContent())
           .andDo(document())

        mvc.perform(get('/user/{id}/role', user.id))
           .andExpect(status().isNoContent())
    }

    @Test
    @Transactional
    void setUserAccessRoles() throws Exception {
        def userRequest = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post('/user')
                        .content(toJson(userRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        def roleRequest = [name: 'Administrator']
        def role = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())
        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role.id)))
           .andExpect(status().isOk())


        roleRequest = [name: 'Manager']
        def role2 = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())
        mvc.perform(post('/user/{id}/role', user.id)
                .content(toJson(role2.id)))
           .andExpect(status().isOk())


        roleRequest = [name: 'sudoers']
        def role3 = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        roleRequest = [name: 'minions']
        def role4 = parseJson(
                mvc.perform(post('/role')
                        .content(toJson(roleRequest)))
                   .andExpect(status().isCreated())
                   .andReturn())

        mvc.perform(put('/user/{id}/role', user.id)
                .content(toJson([role3.id, role4.id])))
           .andExpect(status().isOk())
           .andExpect(jsonPath('$').isArray())
           .andExpect(jsonPath('$[*].id', containsInAnyOrder(role3.id, role4.id)))
           .andExpect(jsonPath('$[*].name', containsInAnyOrder('sudoers', 'minions')))
           .andDo(document())

        mvc.perform(get('/user/{id}/role', user.id))
           .andExpect(status().isOk())
           .andExpect(jsonPath('$').isArray())
           .andExpect(jsonPath('$[*].id', containsInAnyOrder(role3.id, role4.id)))
           .andExpect(jsonPath('$[*].name', containsInAnyOrder('sudoers', 'minions')))
    }
}
