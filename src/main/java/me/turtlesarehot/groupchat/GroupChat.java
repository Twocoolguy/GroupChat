package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.UUID;

public class GroupChat extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GCCommand());
    }
}