import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails
import com.recruit.githubrepositories.api.dto.response.RepositoryDetails
import com.recruit.githubrepositories.converters.GitRepositoryMapper
import spock.lang.Specification

import java.time.LocalDate

class GitRepositoryMapperSpec extends Specification {


    private GitRepositoryMapper gitRepositoryMapper


    def setup() {
        gitRepositoryMapper = GitRepositoryMapper.INSTANCE
    }

    def "Git repository mapper should map fields from GithubRepositoryDetails to RepositoryDetails"() {
        given:
            def githubRepositoryDetails = GithubRepositoryDetails.builder()
                    .full_name("test name")
                    .description("test description")
                    .clone_url("https://test/clone")
                    .created_at(LocalDate.MAX)
                    .stargazers_count(0)
                    .build()

        when:
            RepositoryDetails repositoryDetails = gitRepositoryMapper.githubRepositoryDetailsToRepositoryDetails(githubRepositoryDetails)

        then:
            repositoryDetails.fullName == "test name"
            repositoryDetails.description == "test description"
            repositoryDetails.cloneUrl == "https://test/clone"
            repositoryDetails.createdAt == LocalDate.MAX
            repositoryDetails.stars == 0
    }
}
