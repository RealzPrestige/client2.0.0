package client.command;

import client.Client;
import com.mojang.realmsclient.gui.ChatFormatting;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Client.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Client.commandManager.getPrefix() + command.getName());
        }
    }
}

