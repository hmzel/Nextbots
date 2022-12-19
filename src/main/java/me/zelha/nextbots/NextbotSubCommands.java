package me.zelha.nextbots;

import me.zelha.nextbots.commands.*;

public enum NextbotSubCommands {
    HELP(new HelpCommand()),
    CREATE(new CreateCommand()),
    SUMMON(new SummonCommand()),
    LIST(new ListCommand()),
    REMOVE(new RemoveCommand()),
    IMAGELINK(new ImageLinkCommand()),
    IMAGEFILE(new ImageFileCommand()),
    PARTICLE(new ParticleCommand()),
    SIZE(new SizeCommand()),
    WIDTH(new WidthCommand()),
    HEIGHT(new HeightCommand()),
    FRAMEDELAY(new FrameDelayCommand()),
    FUZZ(new FuzzCommand()),
    IGNORECOLOR(new IgnoreColorCommand()),
    UNIGNORECOLOR(new UnignoreColorCommand());

    private final NextbotCommand command;

    NextbotSubCommands(NextbotCommand command) {
        this.command = command;
    }

    public NextbotCommand getCommand() {
        return command;
    }
}
