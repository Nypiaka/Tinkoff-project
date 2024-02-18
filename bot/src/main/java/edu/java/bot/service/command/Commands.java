package edu.java.bot.service.command;

public enum Commands {
    HELP("/help"),
    LIST("/list"),
    START("/start"),
    TRACK("/track"),
    UNTRACK("/untrack");

    public final String label;

    Commands(String label) {
        this.label = label;
    }
}
