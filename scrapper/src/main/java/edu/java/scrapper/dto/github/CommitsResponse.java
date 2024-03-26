package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

public record CommitsResponse(List<CommitResponse> commits) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CommitResponse(String htmlUrl) {
    }
}
