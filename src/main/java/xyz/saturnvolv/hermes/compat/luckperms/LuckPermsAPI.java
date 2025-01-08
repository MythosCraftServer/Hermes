package xyz.saturnvolv.hermes.compat.luckperms;

import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;
import xyz.saturnvolv.hermes.Hermes;

import java.util.Optional;

public abstract class LuckPermsAPI {
    private static net.luckperms.api.LuckPerms API;
    private static void init() {
        if (!instance().isPresent()) {
            try {
                API = net.luckperms.api.LuckPermsProvider.get();
            } catch (Throwable ignored) {
                Hermes.logger().info("Luck Perms is not enabled.");
            }
        }
    }

    public static boolean isEnabled() {
        init();
        return instance().isPresent();
    }
    public static Optional<net.luckperms.api.LuckPerms> instance() {
        return Optional.ofNullable(API);
    }

    public static String getPrefix(Player player) {
        if (!isEnabled()) return "";
        return instance().get()
                .getPlayerAdapter(Player.class)
                .getMetaData(player)
                .getPrefix();
    }public static String getSuffix(Player player) {
        if (!isEnabled()) return "";
        return instance().get()
                .getPlayerAdapter(Player.class)
                .getMetaData(player)
                .getSuffix();
    }
    public static String getPrimaryGroup(Player player) {
        if (!isEnabled()) return "";
        return instance().get().getPlayerAdapter(Player.class).getMetaData(player).getPrimaryGroup();
    }
    private static Optional<net.luckperms.api.model.group.Group> getGroup(String groupName) {
        if (isEnabled()) return Optional.ofNullable(instance().get().getGroupManager().getGroup(groupName));
        return Optional.empty();
    }
    public static String getGroupDisplayName(String group) {
        if (!isEnabled()) return "";
        return getGroup(group).map(Group::getDisplayName).orElse("");
    }
    public static boolean getPermission(Player player, String permission) {
        if (!isEnabled()) return false;
        return instance().get().getPlayerAdapter(Player.class).getPermissionData(player).checkPermission(permission).asBoolean();
    }
    public static boolean isPlayerImportant(Player player) {
        if (player == null) return false;
        return player.isOp(); // I'll add more to this later
    }
}
