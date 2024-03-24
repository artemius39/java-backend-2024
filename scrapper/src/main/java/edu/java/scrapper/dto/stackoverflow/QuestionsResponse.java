package edu.java.scrapper.dto.stackoverflow;

import java.util.List;

public record QuestionsResponse(List<QuestionResponse> items) {
}
