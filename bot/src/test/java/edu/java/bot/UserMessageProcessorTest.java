package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import edu.java.bot.service.processor.UserMessageProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserMessageProcessorTest {
    @InjectMocks
    UserMessageProcessor userMessageProcessor;

    private final HelpCommand helpCommand = mock(HelpCommand.class);
    private final ListCommand listCommand = mock(ListCommand.class);
    private final StartCommand startCommand = mock(StartCommand.class);
    private final TrackCommand trackCommand = mock(TrackCommand.class);
    private final UntrackCommand untrackCommand = mock(UntrackCommand.class);

    private Update mockUpdate(String expectedMessage) {
        var update = Mockito.mock(Update.class);
        var message = Mockito.mock(Message.class);
        var chat = Mockito.mock(Chat.class);

        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(0L);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(message.text()).thenReturn(expectedMessage);
        Mockito.when(chat.id()).thenReturn(0L);
        return update;
    }

    @Test
    public void userMessageProcessorHelpTest() {
        var update = mockUpdate("/help");
        var expected = new SendMessage(0L, "help was called");
        when(helpCommand.handle(update)).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorListTest() {
        var update = mockUpdate("/list");
        var expected = new SendMessage(0L, "list was called");
        when(listCommand.handle(update)).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorStartTest() {
        var update = mockUpdate("/start");
        var expected = new SendMessage(0L, "start was called");
        when(startCommand.handle(update)).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorTrackTest() {
        var update = mockUpdate("/track something");
        var expected = new SendMessage(0L, "track was called");
        when(trackCommand.handle(update)).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorUntrackTest() {
        var update = mockUpdate("/untrack something");
        var expected = new SendMessage(0L, "untrack was called");
        when(untrackCommand.handle(update)).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }
}
