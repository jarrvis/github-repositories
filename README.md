# github-repositories

## Requirements

Create a simple REST service which will return details of given Github repository. Details should
include:

* full name of repository
* description of repository
* git clone url
* number of stargazers
* date of creation (ISO format)


The API of the service should look as follows:
GET /repositories/{owner}/{repository-name}
```json
{
"fullName": "...",
"description": "...",
"cloneUrl": "...",
"stars": 0,
"createdAt": "..."
}
```

GitHub API reference can be found at: https://developer.github.com/v3/
Non functional requirements:

* should be able to serve 20 requests per second (concurrently; assuming we have GitHub
account; simply put: application should not have obvious scaling bottlenecks)
* set of end-to-end tests that can be run using build tool (Gradle or Maven preferred)
* good design and quality of code
* almost ready to deploy on production (if additional work is needed, please describe it in
README file)

It is okay to make tradeoffs or to simplify the solution as long as you leave a note describing your
thought process.

## Solution details
What was used:
- Github api v3
- Non blocking web client
- Caching (to save up requests rate)
- Bitbucket pipelines
- Swagger documentation
- Deployed to heroku for easy testing

Main challenge:
non functional requirement - "should be able to serve 20 requests per second...":

1. 20 * 3600 = 72000 requests per hour max.
2. 20 concurrent requests per second
 
- Github API operation used does not require authentication however it only allows to perform 60 requests per hour.
- With authentication (basic auth or OAuth) or with clientId + clientSecret (as used) 5000 requests per hour possible (<72000)
- it is assumed that the missing requests rate will be handled by caching: each successful (200) response it cached. 
Before each request cache is checked. If response for given params is in cache then **Etag** header from cached response is sent with github API request. 
If API returns 304 not modified (request was not 'used') then cached response is returned
- Concurrency requirement can be achieved by non-blocking client plus (if needed) more instances with load balancing

Solution will work if requested repositories will not be totally random 
and number of repositories that could be requested is known and relatively low (best <=5000 - no additional assumtions needed).
If number of possible repositories > 5000 then proposed solution will not work right away with requested performance (will need some time for putting repos to cache)      
 


## TODOs

- Performance of solution to be checked by API testing (e.g. JMeter or postman tests) with production data.
- On production / testing environments in-memory cache should be replaced by e.g. Redis
- Spring profiles
- Synchronization of threads when accessing and writing values to cache
- If needed (based on results from testing) horizontal scaling with load balancing should be used

## Rest Template solution

can be checked at:
https://bitbucket.org/jarrvis/github-repositories/commits/tag/1.0.0

## Tests

to run all tests:

`./gradlew cleanTest :test --tests '*'`

to run all tests in file:

`./gradlew cleanTest :test --tests "com.recruit.githubrepositories.api.controller.GitRepositoryControllerSpec"`

to run specific test:

`./gradlew cleanTest :test --tests "com.recruit.githubrepositories.service.GithubRepositoryServiceSpec.Github repository service should return RepositoryDetails from cache"`