package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChat implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if(event.getSender() instanceof ProxiedPlayer) {
            if(event.getMessage().charAt(0) != '/') {
                ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
                GroupPlayer gp = GroupCommand.getGroupPlayer(sender.getUniqueId());
                if (gp != null && gp.isChatToggled()) {
                    GroupCommand.getGroupById(gp.getGroup()).sendMessage(ChatColor.GREEN + "[Group] " + ChatColor.YELLOW + sender.getDisplayName() + ": " + event.getMessage());
                    event.setCancelled(true);
                }
            }
        }
    }
}
