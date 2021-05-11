package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class GroupCommands {
    // On a bungee (proxy) restart this list would be reset.
    ArrayList<GroupPlayer> groupMembers = new ArrayList<GroupPlayer>();
    ArrayList<Group> groups = new ArrayList<Group>();

    // Gets the GroupPlayer object for this user.
    public GroupPlayer getGroupPlayer(UUID player) {
        for(int i = 0; i < this.groupMembers.size(); i++) {
            if(this.groupMembers.get(i).getUUID().equals(player)) {
                return this.groupMembers.get(i);
            }
        }
        // If unable to find the groupplayer, return null.
        return null;
    }

    public GroupPlayer getGroupPlayer(String player) {
        for(int i = 0; i < this.groupMembers.size(); i++) {
            if(this.groupMembers.get(i).getUsername().equals(player)) {
                return this.groupMembers.get(i);
            }
        }
        // If unable to find the groupplayer, return null.
        return null;
    }

    // Adds a GroupPlayer to the groupmembers list. This is any player who is in a group. If the player logs off, they will still be in this list if they were in a group.
    public GroupPlayer addGroupPlayer(UUID player, String user) {
        GroupPlayer p = new GroupPlayer(player, user);
        this.groupMembers.add(p);
        return p;
    }

    // Removes a groupplayer to the groupmembers list. This is any player that isn't in a group.
    public void removeGroupPlayer(GroupPlayer gp) {
        Group gr = gp.getGroup();
        gr.removeMember(gp);
        editGroup(gr);
        gp.setGroup(null);
        gp.setToggle(false);
        this.groupMembers.remove(gp);
    }

    // Edits a groupplayer in the groupmembers list.
    public void editGroupPlayer(GroupPlayer newgp) {
        GroupPlayer egp = getGroupPlayer(newgp.getUUID());
        this.groupMembers.set(this.groupMembers.indexOf(egp), newgp);
    }

    // Edits a existing group
    public void editGroup(Group newGroup) {
        this.groups.set(this.groups.indexOf(newGroup.getLeader().getGroup()), newGroup);
    }

    // Returns the group the player is in, if they are not in one it returns null.
    public Group getPlayerGroup(ProxiedPlayer player) {
        GroupPlayer gplayer = getGroupPlayer(player.getUniqueId());
        if(gplayer == null) { return null; }
        for(int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            if(g.getLeader().equals(gplayer.getUUID())) {
                return gplayer.getGroup();
            }
            for(int h = 0; h < g.getMembers().size(); i++) {
                if(g.getMembers().get(i).equals(gplayer.getUUID())) {
                    return g.getMembers().get(i).getGroup();
                }
            }
        }
        return null;
    }

    // Creates a new group
    public boolean createGroup(ProxiedPlayer player) {
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup != null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are already in a group. If you want to make one, please leave the group you are in."));
            return false;
        }
        GroupPlayer you = addGroupPlayer(player.getUniqueId(), player.getDisplayName());
        groups.add(new Group(you));
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Created the group! Use /gc invite <user> to invite people."));
        return true;

    }

    // If the player is in a group we list information about the group.
    public boolean info(ProxiedPlayer player) {
        // Checking to make sure the player who performed the command is in a group.
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup == null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot list information about a group if you are not in a group."));
            return false;
        }

        //First we get all of the online users and store their uuids in a list.
        ArrayList<ProxiedPlayer> allPlayers = new ArrayList<ProxiedPlayer>();
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
            allPlayers.addAll(entry.getValue().getPlayers());
        }
        ArrayList<UUID> onlinePlayers = new ArrayList<UUID>();
        for(int i = 0; i < allPlayers.size(); i++) {
            onlinePlayers.add(allPlayers.get(i).getUniqueId());
        }

        // Now we list out all the information about the group (sepcifically the members and if they are online)
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Group Info"));

        // First, check for the party leader
        if(playerGroup.getLeader().isOnline(onlinePlayers)) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + playerGroup.getLeader().getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
        }
        else {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + playerGroup.getLeader().getUsername() + " - " + ChatColor.RED + "OFFLINE"));
        }

        // Second, check for the rest of the members.
        for(int i = 0; i < playerGroup.getMembers().size(); i++) {
            GroupPlayer gp = playerGroup.getMembers().get(i);
            if(gp.isOnline(onlinePlayers)) {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
            }
            else {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.RED + "OFFLINE"));
            }
        }
        return true;
    }

    // Leaves the group you are in.
    public boolean leaveGroup(ProxiedPlayer player) {
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup == null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave a group if you are not in one."));
            return false;
        }
        GroupPlayer gp = getGroupPlayer(player.getUniqueId());
        if(playerGroup.getLeader().equals(gp)) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave the group if you are the leader. You must disband or transfer leadership."));
            return false;
        }
        removeGroupPlayer(gp);
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You left the group!"));
        return true;
    }

    // Kick a user from the group
    public boolean kickPlayer(ProxiedPlayer sender, String otherPlayer) {
        Group playerGroup = getPlayerGroup(sender);
        if(playerGroup == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot kick a player from a group if you aren't in a group."));
            return false;
        }
        if(!(playerGroup.getLeader().getUUID().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of your group so you cannot kick members."));
            return false;
        }
        GroupPlayer gp = getGroupPlayer(otherPlayer);
        if(gp == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group or doesn't exist."));
            return false;
        }
        playerGroup.sendMessage(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + playerGroup.getLeader().getUsername() + " kicked " + otherPlayer + " from the group.");
        removeGroupPlayer(gp);
        return true;
    }
}
