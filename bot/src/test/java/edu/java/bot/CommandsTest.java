package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import edu.java.dao.LinksDao;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class CommandsTest {

    ListCommand listCommand = new ListCommand();

    StartCommand startCommand = new StartCommand();

    TrackCommand trackCommand = new TrackCommand();

    UntrackCommand untrackCommand = new UntrackCommand();

    HelpCommand helpCommand = new HelpCommand(List.of(listCommand, startCommand, trackCommand, untrackCommand));

    private final LinksDao linksDao = Mockito.mock(LinksDao.class);

    {
        ReflectionTestUtils.setField(listCommand, "linksDao", linksDao);
        ReflectionTestUtils.setField(trackCommand, "linksDao", linksDao);
        ReflectionTestUtils.setField(untrackCommand, "linksDao", linksDao);
    }

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
        var res = listCommand.handle(mockUpdate(""), List.of(listCommand.getCommandName()));
        Assertions.assertEquals("""
            Tracked links:
            """, res.getParameters().get("text"));
    }

    @Test
    public void helpCommandTest() {
        var res = helpCommand.handle(mockUpdate(""), List.of(helpCommand.getCommandName()));
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
        var res = startCommand.handle(mockUpdate(""), List.of(startCommand.getCommandName()));
        Assertions.assertEquals("""
            Welcome to the link tracking bot! For more information, type /help.""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandEmptyTest() {
        var res = trackCommand.handle(mockUpdate("/track"), List.of(trackCommand.getCommandName()));
        Assertions.assertEquals("""
            Please, insert link to track in format "/track {link}".""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandSuccessTest() {
        Mockito.when(linksDao.saveLink(0L, "https://github.com", "")).thenReturn(true);
        var res = trackCommand.handle(
            mockUpdate("/track https://github.com"),
            List.of(trackCommand.getCommandName(), "https://github.com")
        );
        Assertions.assertEquals("""
            Link saved successful.""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandWrongFormatTest() {
        Mockito.when(linksDao.saveLink(0L, "somelink", "")).thenReturn(true);
        var res = trackCommand.handle(
            mockUpdate("/track somelink"),
            List.of(trackCommand.getCommandName(), "somelink")
        );
        Assertions.assertEquals("""
            Wrong link format!""", res.getParameters().get("text"));
    }

    @Test
    public void trackCommandUnSuccessTest() {
        Mockito.when(linksDao.saveLink(0L, "https://github.com", "")).thenReturn(false);
        var res = trackCommand.handle(
            mockUpdate("/track https://github.com"),
            List.of(trackCommand.getCommandName(), "https://github.com")
        );
        Assertions.assertEquals("""
            Oops! Link was not saved.""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandEmptyTest() {
        var res = untrackCommand.handle(mockUpdate("/untrack"), List.of(untrackCommand.getCommandName()));
        Assertions.assertEquals("""
            Please, insert link to remove in format "/untrack {link}".""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandSuccessTest() {
        Mockito.when(linksDao.removeLink(0L, "https://github.com")).thenReturn(true);
        var res = untrackCommand.handle(
            mockUpdate("/untrack https://github.com"),
            List.of(untrackCommand.getCommandName(), "https://github.com")
        );
        Assertions.assertEquals("""
            Link removed successful.""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandWrongFormatTest() {
        var res = untrackCommand.handle(
            mockUpdate("/untrack somelink"),
            List.of(untrackCommand.getCommandName(), "somelink")
        );
        Assertions.assertEquals("""
            Wrong link format!""", res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandUnSuccessTest() {
        Mockito.when(linksDao.removeLink(0L, "https://github.com")).thenReturn(false);
        var res = untrackCommand.handle(
            mockUpdate("/untrack https://github.com"),
            List.of(untrackCommand.getCommandName(), "https://github.com")
        );
        Assertions.assertEquals("""
            Oops! Link was not removed.""", res.getParameters().get("text"));
    }

}
