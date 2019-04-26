package us.vanderlugt.sample.audit

import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.context.WebApplicationContext

import javax.transaction.Transactional

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SpringAuditingApplicationTests {
    private JsonSlurper json = new JsonSlurper()
    private MockMvc mvc

    @BeforeEach
    void setup(WebApplicationContext context) {
        mvc = webAppContextSetup(context)
                .defaultRequest(get('/')
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .alwaysDo(print())
                .build()
    }

    @Test
    void createUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        mvc.perform(post('/user')
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('username', is('mvanderlugt')))
                .andExpect(jsonPath('password').doesNotExist())
                .andExpect(jsonPath('firstName', is('Mark')))
                .andExpect(jsonPath('lastName', is('Vander Lugt')))
                .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
    }

    @Test
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
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('username', is('mvanderlugt')))
                .andExpect(jsonPath('password').doesNotExist())
                .andExpect(jsonPath('firstName', is('Mark')))
                .andExpect(jsonPath('lastName', is('Vander Lugt')))
                .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
    }

    @Test
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
                .andExpect(jsonPath('deleted').doesNotExist())
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
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('username', is('bboucher')))
                .andExpect(jsonPath('password').doesNotExist())
                .andExpect(jsonPath('firstName', is('Bobby')))
                .andExpect(jsonPath('lastName', is('Boucher')))
                .andExpect(jsonPath('email', is('bobby@boucher.com')))

        mvc.perform(get('/user/{id}', user.id))
                .andExpect(status().isOk())
                .andExpect(jsonPath('id').exists())
                .andExpect(jsonPath('created').doesNotExist())
                .andExpect(jsonPath('modified').doesNotExist())
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('username', is('bboucher')))
                .andExpect(jsonPath('password').doesNotExist())
                .andExpect(jsonPath('firstName', is('Bobby')))
                .andExpect(jsonPath('lastName', is('Boucher')))
                .andExpect(jsonPath('email', is('bobby@boucher.com')))
    }

    @Test
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
                .andExpect(jsonPath('deleted').doesNotExist())
                .andExpect(jsonPath('username', is('mvanderlugt')))
                .andExpect(jsonPath('password').doesNotExist())
                .andExpect(jsonPath('firstName', is('Mark')))
                .andExpect(jsonPath('lastName', is('Vander Lugt')))
                .andExpect(jsonPath('email', is('mvanderlugt@live.com')))

        mvc.perform(get('/user/{id}', user.id))
                .andExpect(status().isNotFound())
    }

    def parseJson(MvcResult mvcResult) {
        json.parse(mvcResult.response.contentAsByteArray)
    }
}
