package client.manager;

import client.gui.impl.setting.Setting;
import client.modules.Feature;
import client.modules.core.Notify;
import client.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FriendManager extends Feature {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private List<Friend> friends = new ArrayList <> ( );

    public FriendManager() {
        super("Friends");
    }

    public boolean isFriend(String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public void addFriend(String name) {
        Friend friend = this.getFriendByName(name);
        if (friend != null) {
            this.friends.add(friend);
            if (Notify.getInstance().friendMessages.getCurrentState()) {
                mc.player.sendChatMessage("/msg " + friend.getUsername() + " You just got added my friend list on Client 2.0.0!");
            }
        }
        this.cleanFriends();
    }

    public void removeFriend(String name) {
        this.cleanFriends();
        for (Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) continue;
            this.friends.remove(friend);
            if (Notify.getInstance().friendMessages.getCurrentState()) {
                mc.player.sendChatMessage("/msg " + friend.getUsername() + " You just got remove my friend list on Client 2.0.0, Get ready to die!");
            }
           break;
        }
    }

    public void onLoad() {
        this.friends = new ArrayList <> ( );
        this.clearSettings();
    }

    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        for (Friend friend : this.friends) {
            this.register( new Setting <> ( friend.getUuid ( ).toString ( ) , friend.getUsername ( ) ));
        }
    }

    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }

    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }

    public Friend getFriendByName(String input) {
        UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            Friend friend = new Friend(input, uuid);
            return friend;
        }
        return null;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public static class Friend {
        private final String username;
        private final UUID uuid;

        public Friend(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}

