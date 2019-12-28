package com.villermen.villerbot.command;

import com.villermen.villerbot.VillerbotPlugin;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class TitleCommand implements CommandExecutor {
    protected static final String[] titleBlacklist = new String[] {
        "admin",
        "mod",
        "op",
        "owner",
        "server",
        "viller",
    };

    VillerbotPlugin plugin;

    public TitleCommand(VillerbotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You can only run this command as a player.");
            return true;
        }

        @Nullable Chat chatService = this.plugin.getChatService();
        if (chatService == null) {
            sender.sendMessage("§4No plugin available to change chat prefix.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§4You must specify a title.");
            return false;
        }

        Player player = (Player)sender;
        String title = args[0];
        boolean isClearCommand = title.equals("clear");

        if (!isClearCommand && !this.isAllowedTitle(title)) {
            player.sendMessage("§4That title is not allowed! Use 2-20 alphanumeric characters (a-z, 0-9, _).");
            return true;
        }

        // Use last two characters of player's current prefix as color code to work with
        String nameColor = chatService.getPlayerPrefix(null, player);
        nameColor = nameColor.substring(nameColor.length() - 2);

        if (!nameColor.startsWith("&")) {
            player.sendMessage("§4I'm sorry. Something went wrong...");
            this.plugin.getLogger().info(String.format(
                "Could not set title for player \"%s\". Could not obtain color code from current prefix.",
                player.getName()
            ));
            return true;
        }

        String prefix;
        if (!isClearCommand) {
            prefix = String.format("&7[%2$s%1$s&7]%2$s", title, nameColor);
        } else {
            prefix = nameColor;
        }

        this.plugin.getChatService().setPlayerPrefix(null, player, prefix);

        if (!isClearCommand) {
            player.sendMessage("§eYour title has been changed!");
        } else {
            player.sendMessage("§eYour title has been cleared!");
        }

        return true;
    }

    protected boolean isAllowedTitle(String title) {
        if (!Pattern.matches("^[a-zA-Z0-9_]{2,20}$", title)) {
            return false;
        }

        Pattern blacklistPattern = Pattern.compile(
            String.format("(%s)", String.join("|", TitleCommand.titleBlacklist)),
            Pattern.CASE_INSENSITIVE
        );
        if (blacklistPattern.matcher(title).matches()) {
            return false;
        }

        return true;
    }
}
