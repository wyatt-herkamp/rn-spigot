package me.kingtux.redditnobility.auth;

import dev.nitrocommand.bukkit.annotations.BukkitPermission;
import dev.nitrocommand.core.annotations.BaseCommand;
import dev.nitrocommand.core.annotations.CommandArgument;
import dev.nitrocommand.core.annotations.NitroCommand;
import dev.nitrocommand.core.annotations.SubCommand;
import me.kingtux.redditnobility.RedditNobility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@NitroCommand(command = "auth", description = "Auth Tool For RedditNobility ", format = "/auth login {RedditUsername}")
public class AuthCommand {
    private RedditNobility redditNobility;
    private AuthManager authManager;

    public AuthCommand(RedditNobility redditNobility, AuthManager authManager) {
        this.redditNobility = redditNobility;
        this.authManager = authManager;
    }

    @BaseCommand
    public void init() {

    }

    @SubCommand(format = "login {RedditUsername}")
    public void login(Player sender, @CommandArgument("RedditUsername") String redditUsername) {
        if (authManager.isLoggedIn(sender.getUniqueId())) {
            sender.sendMessage("You have already been registered");
            return;
        }
        boolean valid = authManager.nobilityClient.isValid(redditUsername);
        if (!valid) {
            sender.sendMessage("Invalid Reddit Username. If you believe this is a mistake please contact KingTux#0042");
            return;
        }
        authManager.getAuthDao().create(new AuthModel(sender.getUniqueId().toString(), AuthStatus.Nobility, redditUsername));
        sender.sendMessage(ChatColor.GREEN + "You have successfully been validated");
    }

    @SubCommand(format = "friend {mcUsername}")
    public void addFriend(Player sender, @CommandArgument("mcUsername") String mcUsername) {
        if (!authManager.isLoggedIn(sender.getUniqueId())) {
            sender.sendMessage("Please register via /auth login {reddit_username}");
            sender.sendMessage("If you believe this is a mistake please contact KingTux#0042");
            return;
        }
        AuthModel redditUsername = authManager.getRedditUsername(sender.getUniqueId()).get();
        if (redditUsername.getStatus() == AuthStatus.Friend) {
            sender.sendMessage("Friends may not have Friends");
        }
        Optional<UUID> uuid = MojangClient.getUUID(mcUsername);
        if (uuid.isEmpty()) {
            sender.sendMessage("Invalid MC username");
            return;
        }
        authManager.getAuthDao().create(new AuthModel(uuid.get().toString(), AuthStatus.Friend, redditUsername.getRedditUsername()));
        sender.sendMessage(ChatColor.GREEN + "Your friend has successfully been added");
    }

    @SubCommand(format = "classicRoyal {mcUsername} {redditUsername}")
    @BukkitPermission("rn.admin")
    public void addClassicRoyal(CommandSender sender, @CommandArgument("mcUsername") String mcUsername, @CommandArgument("redditUsername") String redditUsername) {
        Optional<UUID> uuid = MojangClient.getUUID(mcUsername);
        if (uuid.isEmpty()) {
            sender.sendMessage("Invalid MC username");
            return;
        }
        authManager.getAuthDao().create(new AuthModel(uuid.get().toString(), AuthStatus.ClassicRoyal, redditUsername));
        sender.sendMessage(ChatColor.GREEN + "Your friend has successfully been added");
    }
}