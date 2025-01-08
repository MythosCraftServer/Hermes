package xyz.saturnvolv.hermes;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.saturnvolv.hermes.chat.MessageBuilder;
import xyz.saturnvolv.hermes.commands.HermesCommands;
import xyz.saturnvolv.hermes.compat.placeholder_api.HermesExpansion;
import xyz.saturnvolv.hermes.player.PlayerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class Hermes extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger("Hermes");
    private static Hermes INSTANCE;
    private static List<Pattern> FILTERED_PATTERNS;

    @Override
    public @NotNull Logger getLogger() {
        return LOGGER;
    }
    public static JavaPlugin getPlugin() {
        return INSTANCE;
    }
    public static Logger logger() {
        return getPlugin().getLogger();
    }

    public static List<Pattern> getFilteredPatterns() {
        return FILTERED_PATTERNS;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        FILTERED_PATTERNS = getConfig().getStringList("filters").stream().map(Pattern::compile).toList();
        LOGGER.info("Starting plugin!");
        Bukkit.getPluginManager().registerEvents(this, this);
        HermesCommands.registerCommands(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HermesExpansion().register();
        }
    }

    @Override
    public void onDisable() {
        PlayerAdapter.savePlayersData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerAdapter user = PlayerAdapter.of(event.getPlayer());
        user.updatePlayer(); // Just updates the player from the DB

        event.joinMessage(Component.empty()
                .append(user.serializeNickname())
                .append(Component.text(" joined the game"))
                .style(Style.style(TextColor.fromHexString("#FFFF55")))
        );
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerAdapter user = PlayerAdapter.of(event.getPlayer());
        event.quitMessage(Component.empty()
                .append(user.serializeNickname())
                .append(Component.text(" left the game"))
                .style(Style.style(TextColor.fromHexString("#FFFF55"))));

        user.savePlayerData();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatMessage(AsyncChatEvent event) {
        event.setCancelled(true);
        Bukkit.getServer().broadcast(MessageBuilder.message(PlayerAdapter.of(event.getPlayer()), event.originalMessage()));
    }
}
