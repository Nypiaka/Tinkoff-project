package edu.java.bot.service.command.commands;

public abstract class AbstractTrackingCommand {

    protected abstract String getAction();

    public String wrongLinkFormatDescription() {
        return "Please, insert link in format \"/track {link}\".";
    }

    public String actionWithLinkSuccessful() {
        return "Link " + getAction() + " successful.";
    }

    public String actionWithLinkUnSuccessful() {
        return "Oops! Link was not " + getAction() + ".";
    }

    public String wrongLinkFormat() {
        return "Wrong link format!";
    }
}
