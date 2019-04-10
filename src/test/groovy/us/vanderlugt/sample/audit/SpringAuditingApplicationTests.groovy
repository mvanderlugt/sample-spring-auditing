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
                .defaultRequest(get("/")
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
        mvc.perform(post("/user")
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
    void deleteUser() throws Exception {
        def request = [
                username : 'mvanderlugt',
                password : 'Password1',
                firstName: 'Mark',
                lastName : 'Vander Lugt',
                email    : 'mvanderlugt@live.com'
        ]
        def user = parseJson(
                mvc.perform(post("/user")
                        .content(toJson(request)))
                        .andExpect(status().isCreated())
                        .andReturn())

        mvc.perform(delete("/user/{id}", user.id))
                .andExpect(status().isOk())
    }

    def parseJson(MvcResult mvcResult) {
        json.parse(mvcResult.response.contentAsByteArray)
    }
}
