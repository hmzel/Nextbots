package me.zelha.nextbots;

import me.zelha.nextbots.commands.*;

public enum NextbotSubCommands {
    HELP(new HelpCommand()),
    CREATE(new CreateCommand()),
    SUMMON(new SummonCommand()),
    LIST(new ListCommand()),
    REMOVE(new RemoveCommand());

    private final NextbotCommand command;

    NextbotSubCommands(NextbotCommand command) {
        this.command = command;
    }

    public NextbotCommand getCommand() {
        return command;
    }
}
