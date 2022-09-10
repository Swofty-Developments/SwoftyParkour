package net.swofty.parkour.plugin.gui.guiitem;

import net.swofty.parkour.plugin.gui.GUI;

public interface GUIChatQueryItem extends GUIClickableItem {
    GUI onQueryFinish(String query);

    default boolean acceptRightClick() {
        return true;
    }
}
