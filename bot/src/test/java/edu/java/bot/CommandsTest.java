package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

class CommandsTest {
    private final ScrapperClient scrapperClient = Mockito.mock(ScrapperClient.class);
    ListCommand listCommand = new ListCommand(scrapperClient);

    StartCommand startCommand = new StartCommand();

    TrackCommand trackCommand = new TrackCommand(scrapperClient);

    UntrackCommand untrackCommand = new UntrackCommand(scrapperClient);

    HelpCommand helpCommand = new HelpCommand(List.of(listCommand, startCommand, trackCommand, untrackCommand));

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
        var mockedResp = Mockito.mock(Mono.class);
        Mockito.when(scrapperClient.getAllLinks(0L)).thenReturn(mockedResp);
        Mockito.when(mockedResp.block()).thenReturn(null);
        var res = listCommand.handle(mockUpdate(""), new String[] {listCommand.getCommandName()});
        Assertions.assertEquals("""
            Tracked links:
            """, res.getParameters().get("text"));
    }

    @Test
    public void helpCommandTest() {
        var res = helpCommand.handle(mockUpdate(""), new String[] {helpCommand.getCommandName()});
        Assertions.assertEquals(helpCommand.fullDescription(), res.getParameters().get("text"));
    }

    @Test
    public void startCommandTest() {
        var res = startCommand.handle(mockUpdate(""), new String[] {startCommand.getCommandName()});
        Assertions.assertEquals(startCommand.startMessage(), res.getParameters().get("text"));
    }

    @Test
    public void trackCommandEmptyTest() {
        var res = trackCommand.handle(mockUpdate("/track"), new String[] {trackCommand.getCommandName()});
        Assertions.assertEquals(trackCommand.wrongLinkFormatDescription(), res.getParameters().get("text"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    public void trackCommandsFormatTest(int ind) {
        var results =
            List.of(
                Map.entry("https://github.com", trackCommand.actionWithLinkSuccessful()),
                Map.entry("somelink", trackCommand.wrongLinkFormat())
            );
        var mockedResp = Mockito.mock(Mono.class);
        Mockito.when(scrapperClient.addLink(0L, URI.create(results.get(ind).getKey()))).thenReturn(mockedResp);
        Mockito.when(mockedResp.block()).thenReturn(null);
        var res = trackCommand.handle(
            mockUpdate("/track " + results.get(ind).getKey()),
            new String[] {trackCommand.getCommandName(), results.get(ind).getKey()}
        );
        Assertions.assertEquals(results.get(ind).getValue(), res.getParameters().get("text"));
    }

    @Test
    public void trackCommandUnSuccessTest() {
        var mockedResp = Mockito.mock(Mono.class);
        Mockito.when(scrapperClient.addLink(0L, URI.create("https://github.com"))).thenReturn(mockedResp);
        Mockito.when(mockedResp.block()).thenThrow(WebClientResponseException.class);
        var res = trackCommand.handle(
            mockUpdate("/track https://github.com"),
            new String[] {trackCommand.getCommandName(), "https://github.com"}
        );
        Assertions.assertEquals(trackCommand.actionWithLinkUnSuccessful(), res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandEmptyTest() {
        var res = untrackCommand.handle(mockUpdate("/untrack"), new String[] {untrackCommand.getCommandName()});
        Assertions.assertEquals(untrackCommand.wrongLinkFormatDescription(), res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandSuccessTest() {
        var mockedResp = Mockito.mock(Mono.class);
        Mockito.when(scrapperClient.removeLink(0L, URI.create("https://github.com"))).thenReturn(mockedResp);
        Mockito.when(mockedResp.block()).thenReturn(null);
        var res = untrackCommand.handle(
            mockUpdate("/untrack https://github.com"),
            new String[] {untrackCommand.getCommandName(), "https://github.com"}
        );
        Assertions.assertEquals(untrackCommand.actionWithLinkSuccessful(), res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandWrongFormatTest() {
        var res = untrackCommand.handle(
            mockUpdate("/untrack somelink"),
            new String[] {untrackCommand.getCommandName(), "somelink"}
        );
        Assertions.assertEquals(untrackCommand.wrongLinkFormat(), res.getParameters().get("text"));
    }

    @Test
    public void untrackCommandUnSuccessTest() {
        var mockedResp = Mockito.mock(Mono.class);
        Mockito.when(scrapperClient.removeLink(0L, URI.create("https://github.com"))).thenReturn(mockedResp);
        Mockito.when(mockedResp.block()).thenThrow(WebClientResponseException.class);
        var res = untrackCommand.handle(
            mockUpdate("/untrack https://github.com"),
            new String[] {untrackCommand.getCommandName(), "https://github.com"}
        );
        Assertions.assertEquals(untrackCommand.actionWithLinkUnSuccessful(), res.getParameters().get("text"));
    }

}
