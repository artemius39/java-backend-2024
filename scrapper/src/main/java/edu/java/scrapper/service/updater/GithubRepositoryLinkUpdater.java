package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.dto.github.RepositoryResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GithubRepositoryLinkUpdater extends BaseUpdater<GithubRepositoryLinkUpdater.RepositoryInfo> {
    private static final Pattern GITHUB_REPOSITORY_PATTERN =
        Pattern.compile("https://github.com/([^/]+)/([^/]+)$");

    private final GithubClient githubClient;

    @Override
    public Optional<String> tryUpdate(URI url, OffsetDateTime lastUpdatedAt) {
        RepositoryInfo info = parseUrl(url);
        if (info == null) {
            return Optional.empty();
        }

        RepositoryResponse response = githubClient.getLastUpdateTime(info.owner(), info.name());
        if (response.updatedAt().isAfter(lastUpdatedAt)) {
            return Optional.of("В репозитории %s произошло обновление".formatted(url));
        }
        return Optional.empty();
    }

    @Override
    protected HttpStatus testUrl(RepositoryInfo parsedUrl) {
        return githubClient.testRepositoryUrl(parsedUrl.owner(), parsedUrl.name());
    }

    @Override
    protected RepositoryInfo parseUrl(URI url) {
        Matcher matcher = GITHUB_REPOSITORY_PATTERN.matcher(url.toString());
        return matcher.find() ? new RepositoryInfo(matcher.group(1), matcher.group(2)) : null;
    }

    protected record RepositoryInfo(String owner, String name) {
    }
}
