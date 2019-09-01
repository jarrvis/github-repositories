import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails
import com.recruit.githubrepositories.api.dto.response.RepositoryDetails
import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GithubRepositoryDetailsSpec extends Specification{

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
            Set<ConstraintViolation<RepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            !validationResults.empty
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
            Set<ConstraintViolation<RepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            !validationResults.empty
            validationResults.iterator().next().value == -5
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
            Set<ConstraintViolation<RepositoryDetails>> validationResults = validator.validate(githubRepositoryDetails)

        then:
            !validationResults.empty
            validationResults.iterator().next().value == "notValidUrl"
    }
}
