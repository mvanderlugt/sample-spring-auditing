package us.vanderlugt.sample.audit

import capital.scalable.restdocs.AutoDocumentation
import capital.scalable.restdocs.SnippetRegistry
import capital.scalable.restdocs.jackson.JacksonResultHandlers
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.http.HttpDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext

abstract class BaseTest {
    private JsonSlurper json = new JsonSlurper()
    protected MockMvc mvc

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider docs) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(configure(docs))
                             .defaultRequest(defaultRequest())
                             .alwaysDo(MockMvcResultHandlers.print())
                             .alwaysDo(JacksonResultHandlers.prepareJackson(new ObjectMapper()))
                             .build()
    }


    private static MockHttpServletRequestBuilder defaultRequest() {
        MockMvcRequestBuilders.get('/')
                              .accept(MediaType.APPLICATION_JSON_UTF8)
                              .contentType(MediaType.APPLICATION_JSON_UTF8)
    }

    private static MockMvcConfigurer configure(RestDocumentationContextProvider docs) {
        MockMvcRestDocumentation.documentationConfiguration(docs)
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
        MockMvcRestDocumentation.document(identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(ResponseModifyingPreprocessors.replaceBinaryContent(),
                        ResponseModifyingPreprocessors.limitJsonArrayLength(new ObjectMapper()),
                        Preprocessors.prettyPrint()))
    }


    def parseJson(MvcResult mvcResult) {
        json.parse(mvcResult.response.contentAsByteArray)
    }
}
