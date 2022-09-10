package net.swofty.seniorteamapplication.plugin.gui.guiitem;

import net.swofty.seniorteamapplication.plugin.gui.GUI;

public interface GUIChatQueryItem extends GUIClickableItem {
    GUI onQueryFinish(String query);

    default boolean acceptRightClick() {
        return true;
    }
}
