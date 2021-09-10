package client.command;

import client.Client;
import client.manager.FriendManager;
import com.mojang.realmsclient.gui.ChatFormatting;

public class FriendCommand
        extends Command {
    public FriendCommand() {
        super("friend", new String[]{"[add/del/name/clear]", "[name]"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Client.friendManager.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:.");
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : Client.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    } catch (Exception ignored) {
                    }
                }
                FriendCommand.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    Client.friendManager.onLoad();
                    FriendCommand.sendMessage("Friends list has been reset.");
                    return;
                }
            }
            if(commands[1].equals("Pop")){
                return;
            } else {
                FriendCommand.sendMessage(commands[0] + (Client.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
                return;
            }
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Client.friendManager.addFriend(commands[1]);
                    if(commands[1].equals("Pop")){
                        return;
                    } else {
                        FriendCommand.sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended to your Friend list.");
                    }
                    return;
                }
                case "del": {
                    Client.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.RED + commands[1] + " has been removed from your Friend list.");
                    return;
                }
            }
            FriendCommand.sendMessage("Command not found, try [add/del/name/clear] + [name]");
        }
    }
}

