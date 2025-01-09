package xyz.saturnvolv.hermes.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;

import java.util.HashMap;
import java.util.Map;

public class MessageBadge {

    private static final Map<Character, MessageBadge> REGISTRY = new HashMap<>();

    private final char unicode;
    private final HoverEvent<Component> text;
    private final ClickEvent onClick;
    protected MessageBadge(Builder builder) {
        this.unicode = builder.unicode;
        this.text = builder.text;
        this.onClick = builder.onClick;

        REGISTRY.put(unicode, this);
    }
    public static MessageBadge fromUnicode(char unicode, boolean replaceAny) {
        if (REGISTRY.containsKey(unicode) && !replaceAny) return REGISTRY.get(unicode);
        return MessageBadge.builder(unicode).build();
    }
    public static MessageBadge fromUnicode(char unicode) {
        return fromUnicode(unicode, false);
    }

    public static Builder builder(char unicode) {
        return new Builder(unicode);
    }

    public char unicode() {
        return unicode;
    }

    private boolean hasHoverEvent() {
        return this.text != null;
    }
    private boolean hasClickEvent() {
        return this.onClick != null;
    }

    public Component asComponent() {
        return Component.text(this.unicode)
                .hoverEvent(this.text)
                .clickEvent(this.onClick);
    }

    public static class Builder {
        private final char unicode;
        private HoverEvent<Component> text;
        private ClickEvent onClick;
        protected Builder(char unicode) {
            this.unicode = unicode;
        }

        public Builder showOnHover(Component component) {
            this.text = HoverEvent.showText(component);
            return this;
        }
        public Builder actionOnClick(ClickEvent clickEvent) {
            this.onClick = clickEvent;
            return this;
        }

        public MessageBadge build() {
            return new MessageBadge(this);
        }

    }
}
