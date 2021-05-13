package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Login implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        GroupPlayer player = GroupCommand.getGroupPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            GroupCommand.addMember(event.getPlayer());
        }
        else {
            player.setOnline(true);
            GroupCommand.editMember(player.getUUID(), player);
        }

    }
}
