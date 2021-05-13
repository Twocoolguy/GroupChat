package me.turtlesarehot.groupchat;

import java.util.ArrayList;
import java.util.UUID;



public class GroupPlayer {
    private UUID player;
    private String username;
    private boolean chatToggled;
    private int group;
    private boolean online;

    public GroupPlayer(UUID player, String username) {
        this.player = player;
        this.username = username;
        this.chatToggled = false;
        this.group = -1;
        this.online = true;
    }

    //returns the group that the player is in.
    public int getGroup() { return this.group; }

    //returns if the group chat is toggled.
    public boolean isChatToggled() { return this.chatToggled; }

    //returns the UUID of the player
    public UUID getUUID() { return this.player; }

    // Sets the status of chat toggled
    public void setToggle(boolean toggle) {
        this.chatToggled = toggle;
    }

    //Sets the players group
    public void setGroup(int group) {
        this.group = group;
    }

    public boolean isOnline() { return this.online; }

    // Checks if the two UUID objects are equal
    public boolean equals(UUID otherplayer) {
        return this.player.equals(otherplayer);
    }

    // returns the groupplayers username.
    public String getUsername() { return this.username; }

}
