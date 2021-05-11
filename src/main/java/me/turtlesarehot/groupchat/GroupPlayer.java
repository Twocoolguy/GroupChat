package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GroupPlayer {
    private UUID player;
    private String username;
    private boolean chatToggled;
    private Group group;

    public GroupPlayer(UUID player, String username) {
        this.player = player;
        this.username = username;
        this.chatToggled = false;
        this.group = null;
    }


    //returns the group that the player is in.
    public Group getGroup() { return this.group; }

    //returns if the group chat is toggled.
    public boolean isChatToggled() { return this.chatToggled; }

    //returns the UUID of the player
    public UUID getUUID() { return this.player; }

    // Sets the status of chat toggled
    public void setToggle(boolean toggle) {
        this.chatToggled = toggle;
    }

    //Sets the players group
    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isOnline(ArrayList<UUID> onlinePlayers) {
        for(int i = 0; i < onlinePlayers.size(); i++) {
            if(onlinePlayers.get(i).equals(this.player)) {
                return true;
            }
        }
        return false;
    }

    // Checks if the two UUID objects are equal
    public boolean equals(UUID otherplayer) {
        return this.player.equals(otherplayer);
    }

    // returns the groupplayers username.
    public String getUsername() { return this.username; }




}
