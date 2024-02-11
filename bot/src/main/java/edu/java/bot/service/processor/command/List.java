package edu.java.bot.service.processor.command;

import java.util.Set;
import edu.java.bot.model.User;
import org.springframework.stereotype.Service;

@Service
public class List extends BaseCommandProcessor {
    @Override
    protected String processImpl(User user) {
        Set<String> links = user.getLinks();
        return links.isEmpty() ? "Вы не отслеживаете никакие сайты" : String.join("\n", links);
    }
}