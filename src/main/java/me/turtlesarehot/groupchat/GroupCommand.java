package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class GroupCommand {
    // On a bungee (proxy) restart this list would be reset.
    static ArrayList<GroupPlayer> groupMembers = new ArrayList<GroupPlayer>();
    static ArrayList<Group> groups = new ArrayList<Group>();
    static int groupCount = 0;

    // When a player logs in there will be a GroupPlayer object be made for them.
    public static void addMember(ProxiedPlayer player) {
        groupMembers.add(new GroupPlayer(player.getUniqueId(), player.getDisplayName()));
    }

    public static void editMember(UUID player, GroupPlayer newPlayer) {
        for(int i = 0; i < groupMembers.size(); i++) {
            if(groupMembers.get(i).equals(player)) {
                groupMembers.set(i, newPlayer);
                break;
            }
        }
    }

    public static GroupPlayer getGroupPlayer(UUID player) {
        for(GroupPlayer gp : groupMembers) {
            if(gp.getUUID().equals(player)) {
                return gp;
            }
        }
        return null;
    }

    public static GroupPlayer getGroupPlayer(String player) {
        for(GroupPlayer gp : groupMembers) {
            if(gp.getUsername().equals(player)) {
                return gp;
            }
        }
        return null;
    }

    public static void editGroup(Group newGroup) {
        for(int i = 0; i < groups.size(); i++) {
            if(groups.get(i).getId() == newGroup.getId()) {
                groups.set(i, newGroup);
                break;
            }
        }
    }

    public static Group getGroupById(int id) {
        for(Group g : groups) {
            if(g.getId() == id) {
                return g;
            }
        }
        return null;
    }




    // Creates a new group
    public static boolean createGroup(ProxiedPlayer player) {
        GroupPlayer you = getGroupPlayer(player.getUniqueId());
        if(getGroupById(you.getGroup()) != null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are already in a group. If you want to make one, please leave the group you are in."));
            return false;
        }
        groupCount++;
        Group g = new Group(you.getUUID(), groupCount);
        you.setGroup(g.getId());
        editMember(player.getUniqueId(), you);
        groups.add(g);
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Created the group! Use /gc invite <user> to invite people."));
        return true;

    }

    // If the player is in a group we list information about the group.
    public static boolean info(ProxiedPlayer player) {
        // Checking to make sure the player who performed the command is in a group.
        Group playerGroup = getGroupById(getGroupPlayer(player.getUniqueId()).getGroup());
        // ^ impossible to be null pointer
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
        GroupPlayer leader = getGroupPlayer(playerGroup.getLeader());
        if(leader.isOnline()) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + leader.getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
        }
        else {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + leader.getUsername() + " - " + ChatColor.RED + "OFFLINE"));
        }

        // Second, check for the rest of the members.
        for(int i = 0; i < playerGroup.getMembers().size(); i++) {
            GroupPlayer gp = getGroupPlayer(playerGroup.getMembers().get(i));
            if(gp.isOnline()) {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
            }
            else {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.RED + "OFFLINE"));
            }
        }
        return true;
    }

    // Leaves the group you are in.
    public static boolean leaveGroup(ProxiedPlayer player) {
        GroupPlayer gp = getGroupPlayer(player.getUniqueId());
        Group group = getGroupById(gp.getGroup());
        if(group == null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave a group if you are not in one."));
            return false;
        }
        if(group.getLeader().equals(gp.getUUID())) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave the group if you are the leader. You must disband or transfer leadership."));
            return false;
        }
        gp.setGroup(-1);
        editMember(gp.getUUID(), gp);
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You left the group!"));
        return true;
    }

    // Kick a user from the group
    public static boolean kickPlayer(ProxiedPlayer sender, String otherPlayer) {
        GroupPlayer gp = getGroupPlayer(sender.getUniqueId());
        Group group = getGroupById(gp.getGroup());
        if(group == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot kick a player from a group if you aren't in a group."));
            return false;
        }
        if(!(group.getLeader().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of your group so you cannot kick members."));
            return false;
        }
        GroupPlayer op = getGroupPlayer(otherPlayer);
        if(op == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player hasn't been on the server since proxy started or doesn't exist."));
            return false;
        }
        if(gp.getGroup() != op.getGroup()) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group with you."));
            return false;
        }
        group.sendMessage(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + sender.getDisplayName() + " kicked " + otherPlayer + " from the group.");
        // REMOVE GROUP USER HERE
        return true;
    }

    // Transfers leadership of the group to another player
    public static boolean transferLeader(ProxiedPlayer sender, String otherPlayer) {
        GroupPlayer gp = getGroupPlayer(sender.getUniqueId());
        Group group = getGroupById(gp.getGroup());
        if(sender.getDisplayName().equals(otherPlayer)) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot transfer leadership to yourself."));
            return false;
        }
        if(group == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot transfer leadership if you aren't in a group."));
            return false;
        }
        if(!(group.getLeader().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of your group so you cannot transfer leadership."));
            return false;
        }
        GroupPlayer op = getGroupPlayer(otherPlayer);
        if(op == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group or isn't online."));
            return false;
        }
        if(gp.getGroup() != op.getGroup()) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player is not in a group with you."));
            return false;
        }
        group.removeMember(op);
        group.setLeader(op.getUUID());
        group.addMember(gp.getUUID());
        editGroup(group);
        group.sendMessage(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "The leader transferred leadership to " + otherPlayer);
        return true;
    }

    // Disbands the group.
    public boolean disband(ProxiedPlayer sender) {
        //this one I have to think about.
        return true;
    }

    // Sends a chat message to the group.
    public static boolean chat(ProxiedPlayer sender, String message) {
        Group gp = getGroupById(getGroupPlayer(sender.getUniqueId()).getGroup());
        if(gp == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot send a group chat message if you aren't in a group."));
            return false;
        }
        gp.sendMessage(ChatColor.GREEN + "[Group] " + ChatColor.YELLOW + sender.getDisplayName() + ": " + message);
        return true;
    }


    // Toggles auto message sending into group.
    public static boolean toggleChat(ProxiedPlayer sender) {
        GroupPlayer gp = getGroupPlayer(sender.getUniqueId());
        if(gp.getGroup() == -1) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You must be in a group in order to perform this command."));
            return false;
        }
        sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Auto sending messages to the group chat has been set to " + ("" + !gp.isChatToggled()).toUpperCase() + "."));
        gp.setToggle(!gp.isChatToggled());
        editMember(gp.getUUID(), gp);
        return true;
    }


    // Invites a player to the group.
    public boolean invite(ProxiedPlayer sender, String otherPlayer) {
        GroupPlayer p = getGroupPlayer(otherPlayer);
        GroupPlayer send = getGroupPlayer(sender.getUniqueId());
        if(send.getGroup() == -1) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not in a group."));
            return false;
        }
        Group gp = getGroupById(send.getGroup());
        if(!(gp.getLeader().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of the group so you cannot invite people."));
            return false;
        }
        if(p == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player hasn't been on the server since proxy started or doesn't exist."));
            return false;
        }
        if(!(p.isOnline())) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't online."));
            return false;
        }
        if(p.getGroup() != -1) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This user is already in a group."));
            return false;
        }
        // send invite here.
        return true;
    }
}
