package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;


public class GroupChat extends Plugin {
    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GCCommand());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerChat());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Login());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Disconnect());
    }
}
