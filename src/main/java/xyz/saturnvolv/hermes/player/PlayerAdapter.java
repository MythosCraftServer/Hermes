package xyz.saturnvolv.hermes.player;

import com.mythosmc.mythos_api.database.MythosDB;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.saturnvolv.hermes.Hermes;
import xyz.saturnvolv.hermes.compat.luckperms.LuckPermsAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PlayerAdapter {
    private static final Map<UUID, PlayerAdapter> PLAYER_DATA_CACHE = new HashMap<>();

    public static PlayerAdapter of(UUID uuid) {
        if (PLAYER_DATA_CACHE.containsKey(uuid))
            return PLAYER_DATA_CACHE.get(uuid);
        return new PlayerAdapter(uuid);
    }
    public static PlayerAdapter of(Player player) {
        return of(player.getUniqueId());
    }

    private final UUID uuid;
    private String nickname;

    protected PlayerAdapter(UUID uuid) {
        this.uuid = uuid;
        this.nickname = "";
        Document playerData = getPlayerData(this.uuid);
        if (playerData != null) {
            this.nickname = ((String) playerData.get("nickname"));
        }
        PLAYER_DATA_CACHE.put(uuid, this);
        updatePlayer();
    }
    protected static Document getPlayerData(UUID uuid) {
        return MythosDB.getCollection("hermes-player-data").find(eq("uuid", uuid.toString())).first();
    }
    public static void savePlayersData() {
        PLAYER_DATA_CACHE.values().forEach(PlayerAdapter::savePlayerData);
    }
    public void savePlayerData() {
        if (getPlayerData(this.uuid) == null) MythosDB.getCollection("hermes-player-data").insertOne(this.serialize());
        else MythosDB.getCollection("hermes-player-data").findOneAndReplace(eq("uuid", this.uuid.toString()), this.serialize());
        Hermes.logger().info(String.format("Saved player data for player: %s", Bukkit.getOfflinePlayer(this.uuid).getName() + "(" + this.uuid + ")."));
    }

    public Player getPlayer() {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) return Bukkit.getPlayer(uuid);
        return null;
    }

    public void updatePlayer() {
        this.getPlayer().playerListName(this.displayName());
        this.getPlayer().displayName(this.displayName());
    }

    public boolean hasNickname() {
        return this.nickname != null && !this.nickname.isBlank();
    }
    public void setNickname(String nick) {
        this.nickname = nick;
    }
    public Component serializeNickname() {
        if (!this.hasNickname()) return Component.text(Bukkit.getOfflinePlayer(this.uuid).getName());
        return LegacyComponentSerializer.legacyAmpersand().deserialize(this.nickname);
    }
    public Component getGroupDisplayName() {
        if (LuckPermsAPI.isEnabled()) {
            String groupName = LuckPermsAPI.getGroupDisplayName(LuckPermsAPI.getPrimaryGroup(this.getPlayer()));
            if (groupName == null || groupName.isBlank()) return Component.empty();
            return LegacyComponentSerializer.legacyAmpersand().deserialize(groupName);
        }
        return Component.empty();
    }
    public Component getPrefix() {
        if (LuckPermsAPI.isEnabled()) {
            String prefix = LuckPermsAPI.getPrefix(Bukkit.getPlayer(this.uuid));
            if (prefix == null || prefix.isBlank()) return Component.empty();
            return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
        }
        return Component.empty();
    }
    public Component getSuffix() {
        if (LuckPermsAPI.isEnabled()) {
            String suffix = LuckPermsAPI.getSuffix(Bukkit.getPlayer(this.uuid));
            if (suffix == null || suffix.isBlank()) return Component.empty();
            return LegacyComponentSerializer.legacyAmpersand().deserialize(suffix);
        }
        return Component.empty();
    }

    public Component displayName() {
        return Component.textOfChildren(
                getGroupDisplayName(),
                getPrefix(),
                serializeNickname(),
                getSuffix()
        );
    }

    public Document serialize() {
        Document document = new Document("uuid", this.uuid.toString());
        document.put("nickname", this.nickname);
        return document;
    }
}
