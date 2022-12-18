package me.zelha.nextbots;

import me.zelha.nextbots.commands.CreateCommand;
import me.zelha.nextbots.commands.HelpCommand;
import me.zelha.nextbots.commands.ListCommand;
import me.zelha.nextbots.commands.NextbotCommand;

public enum NextbotSubCommands {
    HELP(new HelpCommand()),
    CREATE(new CreateCommand()),
    LIST(new ListCommand());

    private final NextbotCommand command;

    NextbotSubCommands(NextbotCommand command) {
        this.command = command;
    }

    public NextbotCommand getCommand() {
        return command;
    }
}
