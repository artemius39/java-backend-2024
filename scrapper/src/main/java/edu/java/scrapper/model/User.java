package edu.java.scrapper.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private OffsetDateTime createdAt;

    public User(Long chatId) {
        id = chatId;
    }
}
