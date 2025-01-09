package xyz.saturnvolv.hermes.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.saturnvolv.hermes.Hermes;
import xyz.saturnvolv.hermes.chat.emoji.TweMojiImpl;
import xyz.saturnvolv.hermes.player.PlayerAdapter;

public class MessageBuilder {
    public static final String PREFIX = "&r%s &8>> &r"; // this looks like garble lmao

    public static Component message(PlayerAdapter user, Component message) {
        user.updatePlayer();
        Component prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(PREFIX).replaceText(
                TextReplacementConfig.builder()
                    .matchLiteral("%s")
                    .replacement(user.displayName()).build()
        );
        return Component.textOfChildren(prefix, TweMojiImpl.parseTweMoji(filterLanguage(message)));
    }

    public static Component filterLanguage(Component component) {
        final Component[] ret = {component};
        Hermes.getFilteredPatterns().forEach(pattern -> ret[0] = ret[0].replaceText(TextReplacementConfig.builder().match(pattern).replacement("#####").build()));
        return ret[0];
    }
}
