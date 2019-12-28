package com.villermen.villerbot;

import com.villermen.villerbot.command.TitleCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillerbotPlugin extends JavaPlugin {
    protected @Nullable Permission permissionService;

    protected @Nullable Chat chatService;

    @Override
    public void onEnable() {
        this.setUpVaultIntegration();

        this.getCommand("title").setExecutor(new TitleCommand(this));
    }

    protected void setUpVaultIntegration() {
        // Bukkit has a built-in permission provider
        this.permissionService = this.getServer().getServicesManager().getRegistration(Permission.class).getProvider();

        if (this.getServer().getServicesManager().isProvidedFor(Chat.class)) {
            this.chatService = this.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        }
    }

    public @NotNull Permission getPermissionService() {
        assert (this.permissionService != null);

        return this.permissionService;
    }

    public @Nullable Chat getChatService() {
        return this.chatService;
    }
}
