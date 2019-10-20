package com.jarrvis.githubrepositories.api.dto

import com.jarrvis.githubrepositories.api.dto.response.RepositoryDetails
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import spock.lang.Specification

import java.time.LocalDate

@JsonTest
class RepositoryDetailsSpec extends Specification {

    @Autowired
    private JacksonTester<RepositoryDetails> json

    def "repository details should serialize to json"() {
        setup:
            def jsonSlurper = new JsonSlurper()

            def repositoryDetails = RepositoryDetails.builder()
                    .fullName("test name")
                    .description("test description")
                    .cloneUrl("https://test/clone")
                    .createdAt(LocalDate.parse("2019-09-11"))
                    .stars(0)
                    .build()

            def repositoryDetailsJson = jsonSlurper.parseText(
                    '''
                        { 
                        "fullName" : "test name", 
                        "description" : "test description", 
                        "cloneUrl" : "https://test/clone" ,
                        "stars" : 0 , "createdAt" : "2019-09-11"
                        }
                    '''
            )

        when:
            def serializedRepositoryDetails = this.json.write(repositoryDetails).json

        then:
            jsonSlurper.parseText(serializedRepositoryDetails) == repositoryDetailsJson
    }

}