package edu.java.scrapper.client;

import edu.java.scrapper.dto.github.CommitsResponse;
import edu.java.scrapper.dto.github.RepositoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface GithubClient {
    @GetExchange("/repos/{repositoryOwner}/{repositoryName}")
    RepositoryResponse getLastUpdateTime(@PathVariable String repositoryOwner, @PathVariable String repositoryName);

    @GetExchange("/repos/{repositoryOwner}/{repositoryName}")
    HttpStatus testRepositoryUrl(@PathVariable String repositoryOwner, @PathVariable String repositoryName);

    @GetExchange("/repos/{repositoryOwner}/{repositoryName}/commits")
    CommitsResponse getCommitsSince(
        @PathVariable String repositoryOwner,
        @PathVariable String repositoryName,
        @RequestParam String since
    );

    @GetExchange("/repos/{repositoryOwner}/{repositoryName}/commits")
    HttpStatus testCommitsUrl(
        @PathVariable String repositoryOwner,
        @PathVariable String repositoryName
    );
}
