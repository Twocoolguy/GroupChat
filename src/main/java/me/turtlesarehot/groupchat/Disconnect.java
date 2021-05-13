package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Disconnect implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        GroupPlayer p = GroupCommand.getGroupPlayer(event.getPlayer().getUniqueId());
        p.setOnline(false);
        GroupCommand.editMember(p.getUUID(), p);
    }
}
