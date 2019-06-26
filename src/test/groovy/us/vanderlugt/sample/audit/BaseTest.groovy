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

import capital.scalable.restdocs.AutoDocumentation
import capital.scalable.restdocs.SnippetRegistry
import capital.scalable.restdocs.jackson.JacksonResultHandlers
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.http.HttpDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext

import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@SpringBootTest
@Profile("test")
@ExtendWith([SpringExtension.class, RestDocumentationExtension.class])
abstract class BaseTest {
    private JsonSlurper json = new JsonSlurper()
    protected MockMvc mvc
    protected def token
    private static final String SYSTEM_CLIENT_ID = '00000000-0000-0000-0000-000000000000'
    private static final String SYSTEM_CLIENT_SECRET = 'o5th!6*S%SglSM9^1&&d'


    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider docs) {
        mvc = webAppContextSetup(context)
                .apply(configure(docs))
                .apply(springSecurity())
                .defaultRequest(defaultRequest())
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(JacksonResultHandlers.prepareJackson(new ObjectMapper()))
                .build()

        token = generateToken(SYSTEM_CLIENT_ID, SYSTEM_CLIENT_SECRET)
    }

    private static MockHttpServletRequestBuilder defaultRequest() {
        get('/')
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
    }

    private static MockMvcConfigurer configure(RestDocumentationContextProvider docs) {
        documentationConfiguration(docs)
                .uris()
                .withScheme("https")
                .withHost("api.vanderlugt.us")
                .withPort(443)
                .and()
                .snippets()
                .withDefaults(HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse(),
                        AutoDocumentation.requestFields(), AutoDocumentation.responseFields(),
                        AutoDocumentation.pathParameters(), AutoDocumentation.requestParameters(),
                        AutoDocumentation.description(), AutoDocumentation.methodAndPath(),
                        AutoDocumentation.sectionBuilder()
                                .snippetNames(//AUTO_AUTHORIZATION, //todo add once security is enabled
                                        SnippetRegistry.AUTO_PATH_PARAMETERS,
                                        SnippetRegistry.AUTO_REQUEST_PARAMETERS,
                                        SnippetRegistry.AUTO_REQUEST_FIELDS,
                                        SnippetRegistry.AUTO_RESPONSE_FIELDS,
                                        SnippetRegistry.HTTP_REQUEST,
                                        SnippetRegistry.HTTP_RESPONSE)
                                .skipEmpty(true)
                                .build())
    }

    protected static ResultHandler document() {
        document("{class-name}/{method-name}")
    }

    protected static ResultHandler document(String identifier) {
        document(identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(ResponseModifyingPreprocessors.replaceBinaryContent(),
                        limitJsonArrayLength(new ObjectMapper()),
                        prettyPrint()))
    }


    def parseJson(MvcResult mvcResult) {
        json.parse(mvcResult.response.contentAsByteArray)
    }

    def generateToken(String client, String secret) {
        parseJson(
                mvc.perform(
                        post('/oauth/token')
                                .param('client_id', client)
                                .param('grant_type', 'client_credentials')
                                .with(httpBasic(client, secret)))
                        .andExpect(status().isOk())
                        .andReturn())
    }

    def generateToken(String clientId, String clientSecret, String username, String password) {
        parseJson(
                mvc.perform(
                        post('/oauth/token')
                                .param('client_id', clientId)
                                .param('grant_type', 'password')
                                .param('username', username)
                                .param('password', password)
                                .with(httpBasic(clientId, clientSecret)))
                        .andExpect(status().isOk())
                        .andReturn())
    }

    static RequestPostProcessor bearer(Object token) {
        { request ->
            request.addHeader('Authorization', "Bearer ${token.access_token}")
            return request
        }
    }
}
