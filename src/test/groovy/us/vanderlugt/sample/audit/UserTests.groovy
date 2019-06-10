package us.vanderlugt.sample.audit

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext

import javax.transaction.Transactional

import static capital.scalable.restdocs.AutoDocumentation.*
import static capital.scalable.restdocs.SnippetRegistry.*
import static capital.scalable.restdocs.jackson.JacksonResultHandlers.prepareJackson
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.replaceBinaryContent
import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@SpringBootTest
@ExtendWith([SpringExtension.class, RestDocumentationExtension.class])
class UserTests {
    private JsonSlurper json = new JsonSlurper()
    private MockMvc mvc

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider docs) {
        mvc = webAppContextSetup(context)
                .apply(configure(docs))
                .defaultRequest(defaultRequest())
                .alwaysDo(print())
                .alwaysDo(prepareJackson(new ObjectMapper()))
                .build()
    }

    private static MockHttpServletRequestBuilder defaultRequest() {
        get('/')
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
    }

    private static MockMvcConfigurer configure(RestDocumentationContextProvider docs) {
        documentationConfiguration(docs)
                .uris()
                .withScheme("https")
                .withHost("api.vanderlugt.us")
                .withPort(443)
                .and()
                .snippets()
                .withDefaults(httpRequest(), httpResponse(),
                requestFields(), responseFields(),
                pathParameters(), requestParameters(),
                description(), methodAndPath(),
                sectionBuilder()
                        .snippetNames(AUTO_AUTHORIZATION,
                        AUTO_PATH_PARAMETERS,
                        AUTO_REQUEST_PARAMETERS,
                        AUTO_REQUEST_FIELDS,
                        AUTO_RESPONSE_FIELDS,
                        AUTO_LINKS,
                        AUTO_EMBEDDED,
                        HTTP_REQUEST,
                        HTTP_RESPONSE)
                        .skipEmpty(true)
                        .build())
    }

    private static ResultHandler document() {
        document("{class-name}/{method-name}")
    }

    private static ResultHandler document(String identifier) {
        document(identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(replaceBinaryContent(),
                        limitJsonArrayLength(new ObjectMapper()),
                        prettyPrint()))
    }

    @Test
    @Transactional
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
           .andExpect(jsonPath('deleted').doesNotExist())
           .andExpect(jsonPath('username', is('mvanderlugt')))
           .andExpect(jsonPath('password').doesNotExist())
           .andExpect(jsonPath('firstName', is('Mark')))
           .andExpect(jsonPath('lastName', is('Vander Lugt')))
           .andExpect(jsonPath('email', is('mvanderlugt@live.com')))
           .andDo(document())
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
           .andExpect(jsonPath('number', is(0)))
           .andExpect(jsonPath('size', is(10)))
           .andExpect(jsonPath('content[*].id').exists())
           .andExpect(jsonPath('content[*].created').doesNotExist())
           .andExpect(jsonPath('content[*].modified').doesNotExist()) //todo tighten up assertions with sorting
           .andExpect(jsonPath('content[*].deleted').doesNotExist())
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
           .andDo(document())

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
           .andExpect(jsonPath('deleted').doesNotExist())
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

    def parseJson(MvcResult mvcResult) {
        json.parse(mvcResult.response.contentAsByteArray)
    }
}
