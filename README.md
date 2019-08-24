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