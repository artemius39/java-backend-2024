package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.BotService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageControllerTest {
    @Test
    void nonMessageUpdatesAreIgnored() {
        Update update = mock(Update.class);
        TelegramBot telegramBot = mock(TelegramBot.class);
        BotService botService = mock(BotService.class);

        MessageController controller = new MessageController(telegramBot, botService);

        controller.process(List.of(update));

        verify(botService, times(0)).process(update);
    }

    @Test
    void messageUpdatesAreBeingProcessed() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(0L);

        BotService botService = mock(BotService.class);
        when(botService.process(update)).thenReturn("response");
        TelegramBot bot = mock(TelegramBot.class);
        MessageController messageController = new MessageController(bot, botService);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        messageController.process(List.of(update));
        verify(bot).execute(captor.capture());
        SendMessage sendMessage = captor.getValue();
        Long chatId = (Long) sendMessage.getParameters().get("chat_id");
        String text = (String) sendMessage.getParameters().get("text");

        assertThat(chatId).isZero();
        assertThat(text).isEqualTo("response");
    }
}
