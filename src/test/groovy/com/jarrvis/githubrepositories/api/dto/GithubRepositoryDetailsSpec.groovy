package com.jarrvis.githubrepositories.api.dto


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.time.LocalDate

@JsonTest
class GithubRepositoryDetailsSpec extends Specification {

    @Autowired
    private JacksonTester<GithubRepositoryDetails> json

    private static Validator validator

    def setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()

    }

    def "should not be possible to get repository details without repository name"() {
        setup:
            def githubRepositoryDetails = GithubRepositoryDetails.builder()
                    .description("test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.now())
                    .stargazers_count(0)
                    .build()

        when:
            Set<ConstraintViolation<GithubRepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            validationResults.first().getPropertyPath().first().name == "full_name"
    }

    def "should not be possible to get repository details with negative stars count"() {
        setup:
            def githubRepositoryDetails = GithubRepositoryDetails.builder()
                    .full_name("test name")
                    .description("test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.now())
                    .stargazers_count(-5)
                    .build()

        when:
            Set<ConstraintViolation<GithubRepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            !validationResults.empty
            validationResults.first().getPropertyPath().first().name == "stargazers_count"
    }

    def "should not be possible to get repository details with clone url that is not in url form"() {
        setup:
            def githubRepositoryDetails = GithubRepositoryDetails.builder()
                    .full_name("test name")
                    .description("test description")
                    .clone_url("notValidUrl")
                    .created_at(LocalDate.now())
                    .stargazers_count(5)
                    .build()

        when:
            Set<ConstraintViolation<GithubRepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            !validationResults.empty
            validationResults.first().getPropertyPath().first().name == "clone_url"
    }

    def "Github repository details should be deserialized from json"() {
        setup:
            def githubRepositoryDetailsJson =
                    '''
                        { 
                        "full_name" : "test name", 
                        "description" : "test description", 
                        "clone_url" : "https://test/clone" ,
                        "created_at" : "2019-09-11",
                        "stargazers_count" : 0 
                        }
                    '''

            def githubRepositoryDetails = GithubRepositoryDetails.builder()
                    .full_name("test name")
                    .description("test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.parse("2019-09-11"))
                    .stargazers_count(0)
                    .build()

        when:
            def deserializedRepositoryDetails = this.json.parse(githubRepositoryDetailsJson).object

        then:
            deserializedRepositoryDetails == githubRepositoryDetails
    }
}
