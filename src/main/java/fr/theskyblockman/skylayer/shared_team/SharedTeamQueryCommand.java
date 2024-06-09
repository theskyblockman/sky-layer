package fr.theskyblockman.skylayer.shared_team;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SharedTeamQueryCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "list":
                for(String team : SharedTeamLayer.manager.listTeams()) {
                    sender.sendMessage("- " + team);
                }
                break;
            case "create":
                if(args.length == 2) {
                    SharedTeamLayer.manager.createTeam(args[1], null);
                    sender.sendMessage("Team " + args[1] + " created.");
                    return true;
                } else {
                    sender.sendMessage("Team names must not contain spaces when created through this command.");
                }
                break;
            case "delete":
                if(args.length == 2) {
                    SharedTeamLayer.manager.deleteTeam(args[1]);
                    sender.sendMessage("Team " + args[1] + " deleted.");
                    return true;
                }
                break;
            case "check":
                if(args.length == 2) {
                    sender.sendMessage(args[1] + " is in the following teams:");
                    for(String team : SharedTeamLayer.manager.getTeamNamesForPlayer(args[1])) {
                        sender.sendMessage("- " + team);
                    }
                }
                break;
            case "edit":
                if(args.length < 3) {
                    break;
                }
                switch (args[2]) {
                    case "addmember":
                        if(args.length == 4) {
                            SharedTeamLayer.manager.addPlayerToTeam(args[3], args[1]);
                            sender.sendMessage(args[3] + " added to team " + args[1] + ".");
                        }
                        break;
                    case "removemember":
                        if(args.length == 4) {
                            SharedTeamLayer.manager.removePlayerFromTeam(args[3], args[1], false);
                            sender.sendMessage(args[3] + " removed from team " + args[1] + ".");
                        }
                        break;
                }
                break;
            default:
                return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            return Arrays.asList("list", "create", "delete", "edit", "check");
        } else if(args.length == 2) {
            switch (args[0]) {
                case "delete":
                case "edit":
                    return Arrays.asList(SharedTeamLayer.manager.listTeams());
                default:
                    return Collections.emptyList();
            }
        } else if(Objects.equals(args[0], "delete")
                || Objects.equals(args[0], "list")
                || Objects.equals(args[1], "create")
                || Objects.equals(args[1], "check")) {
            return Collections.emptyList();
        } else if(args.length == 3) {
            if(args[0].equals("edit")) {
                return Arrays.asList("addmember", "removemember");
            }
        }else if(args.length == 4) {
            if(args[0].equals("edit")) {
                switch (args[2]) {
                    case "addmember":
                        return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
                    case "removemember":
                        // We shouldn't be aware of the full list of players in a team.
                        return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }
}
