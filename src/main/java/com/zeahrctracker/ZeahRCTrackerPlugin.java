package com.zeahrctracker;

import com.google.inject.Provides;

import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;

@Slf4j
@PluginDescriptor(
        name = "Zeah RC Tracker"
)
public class ZeahRCTrackerPlugin extends Plugin {

    public boolean isrunecrafting;

    public HashMap<Integer, Integer> sessionTrackingStart;
    public HashMap<Integer, Integer> sessionTrackingCurrent;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ZeahRCTrackerOverlay overlay;

    @Inject
    private ZeahRCTrackerConfig config;

    @Override
    protected void startUp() throws Exception {
        log.info("ZeahRCTracker by Waldxn started!");
        sessionTrackingStart = new HashMap<>();
        sessionTrackingCurrent = new HashMap<>();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("ZeahRCTracker by Waldxn stopped!");
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (sessionTrackingStart.isEmpty()) {
            for (int i = 0; i <= 27; i++) {
                ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
                if (container.getItem(i) != null) {
                    sessionTrackingStart.put(container.getItem(i).getId(), container.getItem(i).getQuantity());
                    sessionTrackingCurrent.put(container.getItem(i).getId(), container.getItem(i).getQuantity());
                }
            }
        }
        if (event.getItemContainer().contains(565)) {
            for (Item i : event.getItemContainer().getItems()) {
                if (i.getId() == 565) {
                    if (!(sessionTrackingStart.containsKey(i.getId())) && !client.isMenuOpen()) {
                        //When a player runecrafts from a clear inventory
                        sessionTrackingStart.put(i.getId(), 0);
                        sessionTrackingCurrent.put(i.getId(), i.getQuantity());
                        isrunecrafting = true;
                    }
                    if (sessionTrackingCurrent.get(i.getId()) != i.getQuantity()) {
                        sessionTrackingCurrent.replace(i.getId(), i.getQuantity());
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "[Zeah RC] Total blood runes crafted: " +
                                getCraftedRunes(565), null);
                        isrunecrafting = true;
                    }
                }
            }
        }
    }

    public Integer getCraftedRunes(Integer itemId){
        return sessionTrackingCurrent.get(itemId) - sessionTrackingStart.get(itemId);
    }

    @Provides
    ZeahRCTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ZeahRCTrackerConfig.class);
    }
}
