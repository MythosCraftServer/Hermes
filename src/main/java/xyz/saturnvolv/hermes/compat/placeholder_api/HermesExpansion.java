package xyz.saturnvolv.hermes.compat.placeholder_api;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.saturnvolv.hermes.Hermes;
import xyz.saturnvolv.hermes.player.PlayerAdapter;

import java.sql.SQLOutput;

public class HermesExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    private final Hermes plugin;
    public HermesExpansion() {
        this.plugin = (Hermes) Hermes.getPlugin();
        plugin.getLogger().info("Placeholder expansion registered!");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hermes";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("nickname"))
            return LegacyComponentSerializer.legacyAmpersand().serialize(PlayerAdapter.of(player.getUniqueId()).displayName());
        return null;
    }
}
