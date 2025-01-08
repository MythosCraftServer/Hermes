package xyz.saturnvolv.hermes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.saturnvolv.hermes.player.PlayerAdapter;

public class HermesCommands {
    public static final LiteralCommandNode<CommandSourceStack> HERMES_NICKNAME_COMMAND = Commands.literal("nickname")
            .then(Commands.argument("change_to", StringArgumentType.string())
                    .executes(context -> {
                        if (context.getSource().getSender() instanceof Player player) {
                            String name = context.getArgument("change_to", String.class);
                            PlayerAdapter user = PlayerAdapter.of(player);
                            user.setNickname(name);
                            player.sendMessage(Component.text("Set nickname to: ").append(user.serializeNickname()));
                            user.updatePlayer();
                        }
                        return Command.SINGLE_SUCCESS;
                    })
            )
            .build();

    public static <T extends JavaPlugin> void registerCommands(T plugin) {
        LifecycleEventManager<@NotNull Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(HERMES_NICKNAME_COMMAND, "Changes a player's displayed nickname!");
        });
    }
}
