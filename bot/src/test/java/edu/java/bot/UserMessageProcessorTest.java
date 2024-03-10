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
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserMessageProcessorTest {
    UserMessageProcessor userMessageProcessor;

    private final HelpCommand helpCommand = mock(HelpCommand.class);
    private final ListCommand listCommand = mock(ListCommand.class);
    private final StartCommand startCommand = mock(StartCommand.class);
    private final TrackCommand trackCommand = mock(TrackCommand.class);
    private final UntrackCommand untrackCommand = mock(UntrackCommand.class);

    {
        when(helpCommand.getCommandName()).thenReturn("/help");
        when(listCommand.getCommandName()).thenReturn("/list");
        when(startCommand.getCommandName()).thenReturn("/start");
        when(trackCommand.getCommandName()).thenReturn("/track");
        when(untrackCommand.getCommandName()).thenReturn("/untrack");
        userMessageProcessor =
            new UserMessageProcessor(helpCommand, listCommand, startCommand, trackCommand, untrackCommand);
    }

    private Update mockUpdate(String expectedMessage) {
        var update = mock(Update.class);
        var message = mock(Message.class);
        var chat = mock(Chat.class);

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
        when(helpCommand.handle(update, List.of("/help"))).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorListTest() {
        var update = mockUpdate("/list");
        var expected = new SendMessage(0L, "list was called");
        when(listCommand.handle(update, List.of("/list"))).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorStartTest() {
        var update = mockUpdate("/start");
        var expected = new SendMessage(0L, "start was called");
        when(startCommand.handle(update, List.of("/start"))).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorTrackTest() {
        var update = mockUpdate("/track something");
        var expected = new SendMessage(0L, "track was called");
        when(trackCommand.handle(update, List.of("/track", "something"))).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void userMessageProcessorUntrackTest() {
        var update = mockUpdate("/untrack something");
        var expected = new SendMessage(0L, "untrack was called");
        when(untrackCommand.handle(update, List.of("/untrack", "something"))).thenReturn(expected);
        var result = userMessageProcessor.process(update);
        Assertions.assertEquals(expected, result);
    }
}
