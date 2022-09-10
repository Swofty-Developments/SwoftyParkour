package net.swofty.parkour.plugin.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Message
{
    private final List<Message> messages;
    private final String message;

    private ClickEvent clickAction;
    private HoverEvent.Action hoverAction;
    private String hoverActionValue;

    public Message(String message) {
        this.message = SUtil.translateColorWords(message);
        messages = new ArrayList<>();
        messages.add(this);
    }

    public Message(String message, boolean autoTranslateColourCodes) {
        this.message = autoTranslateColourCodes ? SUtil.translateColorWords(message) : message;
        messages = new ArrayList<>();
        messages.add(this);
    }

    public Message action(ClickEvent action) {
        this.clickAction = new ClickEvent(action.getAction(), SUtil.translateColorWords(action.getValue()));
        return this;
    }

    public Message hover(String value) {
        this.hoverAction = HoverEvent.Action.SHOW_TEXT;
        this.hoverActionValue = value;
        return this;
    }

    public Message hover(HoverEvent.Action action, String value) {
        this.hoverAction = action;
        this.hoverActionValue = value;
        return this;
    }

    public Message append(Message message) {
        messages.add(message);
        return this;
    }

    public Message append(Object message) {
        messages.add(new Message(message.toString()));
        return this;
    }

    public BaseComponent[] build() {
        BaseComponent[] comp = TextComponent.fromLegacyText(message);
        for (BaseComponent component : comp) {
            if (clickAction != null)
                component.setClickEvent(clickAction);
            if (hoverAction != null)
                component.setHoverEvent(new HoverEvent(hoverAction, new ComponentBuilder(SUtil.translateColorWords(hoverActionValue)).create()));
        }

        return comp;
    }

    public BaseComponent[] buildAll() {
        List<BaseComponent[]> list = new ArrayList<>();
        for (Message message : messages)
            list.add(message.build());

        BaseComponent[] comp = list.get(0);
        for (int i = 1; i < list.size(); i++)
            comp = SUtil.combine(comp, list.get(i));

        return comp;
    }

    public void send(Player player) {
        List<BaseComponent[]> list = new ArrayList<>();
        for (Message message : messages)
            list.add(message.build());

        BaseComponent[] comp = list.get(0);
        for (int i = 1; i < list.size(); i++)
            comp = SUtil.combine(comp, list.get(i));

        player.spigot().sendMessage(comp);
    }

    public static Message parse(String jsonString) {
        JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
        String msg = object.get("message").getAsString();

        JsonObject action = object.get("action").getAsJsonObject();
        String actionType = action.get("type").getAsString();
        String actionValue = action.get("value").getAsString();

        String hover = object.get("hover").getAsString();

        return new Message(msg).hover(hover).action(new ClickEvent(ClickEvent.Action.valueOf(actionType), actionValue));
    }
}
