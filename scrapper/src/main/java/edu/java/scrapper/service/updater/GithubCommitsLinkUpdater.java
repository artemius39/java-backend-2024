package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.dto.github.CommitsResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GithubCommitsLinkUpdater extends BaseUpdater<GithubCommitsLinkUpdater.RepositoryInfo> {
    private static final Pattern GITHUB_COMMITS_PATTERN =
        Pattern.compile("https://github.com/([^/]+)/([^/]+)/commits$");
    private static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");


    private final GithubClient githubClient;

    @Override
    public Optional<String> tryUpdate(URI url, OffsetDateTime lastUpdatedAt) {
        RepositoryInfo info = parseUrl(url);
        if (info == null) {
            return Optional.empty();
        }

        CommitsResponse response = githubClient.getCommitsSince(
            info.owner(),
            info.name(),
            lastUpdatedAt.format(ISO8601_FORMATTER)
        );
        List<CommitsResponse.CommitResponse> commits = response.commits();

        if (commits.isEmpty()) {
            return Optional.empty();
        }
        if (commits.size() == 1) {
            CommitsResponse.CommitResponse commit = commits.getFirst();
            return Optional.of("В репозитории %s появился новый коммит: %s".formatted(url, commit.htmlUrl()));
        }
        return commits.stream()
            .map(CommitsResponse.CommitResponse::htmlUrl)
            .collect(Collectors.collectingAndThen(
                Collectors.joining(", ", "В репозитории %s появились новые коммиты: ".formatted(url), ""),
                Optional::of
            ));
    }

    protected RepositoryInfo parseUrl(URI url) {
        Matcher matcher = GITHUB_COMMITS_PATTERN.matcher(url.toString());
        return matcher.find() ? new RepositoryInfo(matcher.group(1), matcher.group(2)) : null;
    }

    @Override
    protected HttpStatus testUrl(RepositoryInfo parsedUrl) {
        return githubClient.testCommitsUrl(parsedUrl.owner(), parsedUrl.name());
    }

    protected record RepositoryInfo(String owner, String name) {
    }
}
