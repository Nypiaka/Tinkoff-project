package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.dao.Dao;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class CommandsTest {

    @Autowired ListCommand listCommand;

    @Autowired HelpCommand helpCommand;

    @Autowired StartCommand startCommand;

    @InjectMocks
    TrackCommand trackCommand;

    @InjectMocks
    UntrackCommand untrackCommand;

    private final Dao dao = Mockito.mock(Dao.class);

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
    public void listCommandTest() {
        var res = listCommand.handle(mockUpdate(""));
        Assertions.assertEquals("""
            Tracked links:
            Empty now. But someday it won't be empty.""", res.getParameters().get("text"));
    }

    @Test
    public void helpCommandTest() {
        var res = helpCommand.handle(mockUpdate(""));
        Assertions.assertEquals("""
            List of available commands:

            /list - get a list of all available links

            /start - start working with a bot

            /track {link} - start tracking link

            /untrack {link} - stop tracking link

            """, res.getParameters().get("text"));
    }

    @Test
    public void startCommandTest() {
        var res = startCommand.handle(mockUpdate(""));
        Assertions.assertEquals("""
            Welcome to the link tracking bot! For more information, type /help.""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandEmptyTest() {
        var res = trackCommand.handle(mockUpdate("/track"));
        Assertions.assertEquals("""
            Please, insert link to track in format "/track {link}".""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandSuccessTest() {
        Mockito.when(dao.saveLink(0L, "somelink")).thenReturn(true);
        var res = trackCommand.handle(mockUpdate("/track somelink"));
        Assertions.assertEquals("""
            Link saved successful.""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandUnSuccessTest() {
        Mockito.when(dao.saveLink(0L, "somelink")).thenReturn(false);
        var res = trackCommand.handle(mockUpdate("/track somelink"));
        Assertions.assertEquals("""
            Oops! Link was not saved.""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandEmptyTest() {
        var res = untrackCommand.handle(mockUpdate("/untrack"));
        Assertions.assertEquals("""
            Please, insert link to remove in format "/track {link}".""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandSuccessTest() {
        Mockito.when(dao.removeLink(0L, "somelink")).thenReturn(true);
        var res = untrackCommand.handle(mockUpdate("/untrack somelink"));
        Assertions.assertEquals("""
            Link removed successful.""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandUnSuccessTest() {
        Mockito.when(dao.removeLink(0L, "somelink")).thenReturn(false);
        var res = untrackCommand.handle(mockUpdate("/untrack somelink"));
        Assertions.assertEquals("""
            Oops! Link was not removed.""", res.getParameters().get("text"));
    }

}
