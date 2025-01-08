package xyz.saturnvolv.hermes.chat.emoji;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

public interface TweMojiImpl {
    static Component parseTweMoji(Component message) {
        return message.replaceText(
                TextReplacementConfig
                        .builder()
                        .match(":[\\w-]+:")
                        .replacement((matchResult, builder) -> Component.translatable(matchResult.group()))
                        .build()
        );
    }
}
