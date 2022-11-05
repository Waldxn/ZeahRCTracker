package com.zeahrctracker;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;

@Slf4j
@PluginDescriptor(
        name = "Zeah RC Tracker"
)
public class ZeahRCTrackerPlugin extends Plugin {

    public boolean isBloodRunecrafting;
    public boolean isSoulRunecrafting;

    public HashMap<Integer, Integer> sessionTrackingCurrent;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ZeahRCTrackerOverlay overlay;

    @Inject
    private ZeahRCTrackerConfig config;

    @Override
    protected void startUp() throws Exception {
        log.info("ZeahRCTracker by Waldxn started!");
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
        //Prevents runes being tracked falsely on login (if in zeah)
        boolean initialLogin = false;

        //Set defaults
        if (getCraftedRunes(566) == null || getCraftedRunes(565) == null) {
            if (getCraftedRunes(566) == null) {
                setCraftedRunes(566, 0);
            } else if (getCraftedRunes(565) == null) {
                setCraftedRunes(565, 0);
            }
        }

        //Load to hashmap on login
        if (sessionTrackingCurrent.isEmpty()) {
            for (int i = 0; i <= 27; i++) {
                ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

                if (container.getItem(i) != null) {
                    sessionTrackingCurrent.put(container.getItem(i).getId(), container.getItem(i).getQuantity());
                } else {
                    sessionTrackingCurrent.put(565, 0);
                    sessionTrackingCurrent.put(566, 0);
                }
                initialLogin = true;
            }
        }

        //Runecrafting event
        if (event.getItemContainer().contains(565) || event.getItemContainer().contains(566)) {

            boolean inZeah = false;
            int region = client.getLocalPlayer().getWorldLocation().getRegionID();
            if (region == 6715 || region == 7228) {
                inZeah = true;
            }

            //Prevents tracker from including runes outside zeah
            if (!inZeah) {
                if (event.getItemContainer().contains(565)) {
                    for (Item i : event.getItemContainer().getItems()) {
                        if (i.getId() == 565) {
                            sessionTrackingCurrent.replace(565, i.getQuantity());
                        }
                    }
                } else if (event.getItemContainer().contains(566)) {
                    for (Item i : event.getItemContainer().getItems()) {
                        if (i.getId() == 566) {
                            sessionTrackingCurrent.replace(566, i.getQuantity());
                        }
                    }
                }
            }

            if (inZeah && !initialLogin) {
                for (Item i : event.getItemContainer().getItems()) {
                    if (i.getId() == 565 && config.bloodCheckbox()) {
                        if (!(sessionTrackingCurrent.containsKey(i.getId()))) {
                            //When a player runecrafts from a clear inventory
                            sessionTrackingCurrent.put(i.getId(), i.getQuantity());
                            setCraftedRunes(565, i.getQuantity());
                            isBloodRunecrafting = true;
                        }
                        if (sessionTrackingCurrent.get(i.getId()) != i.getQuantity()) {
                            if (getCraftedRunes(565) == null) {
                                //If config doesn't have data already, create it
                                setCraftedRunes(565, (i.getQuantity() - sessionTrackingCurrent.get(565)));
                                sessionTrackingCurrent.replace(i.getId(), i.getQuantity());
                            } else {
                                //Updates the amount of runes crafted
                                setCraftedRunes(565, (i.getQuantity() - sessionTrackingCurrent.get(565)) + getCraftedRunes(565));
                                sessionTrackingCurrent.replace(i.getId(), i.getQuantity());
                            }
                            isBloodRunecrafting = true;
                        }
                    } else if (i.getId() == 566 && config.soulCheckbox()) {
                        if (!(sessionTrackingCurrent.containsKey(i.getId()))) {
                            //When a player runecrafts from a clear inventory
                            sessionTrackingCurrent.put(i.getId(), i.getQuantity());
                            setCraftedRunes(566, i.getQuantity());
                            isSoulRunecrafting = true;
                        }
                        if (sessionTrackingCurrent.get(i.getId()) != i.getQuantity()) {
                            if (getCraftedRunes(566) == null) {
                                //If config doesn't have data already, create it
                                setCraftedRunes(566, (i.getQuantity() - sessionTrackingCurrent.get(566)));
                                sessionTrackingCurrent.replace(i.getId(), i.getQuantity());
                            } else {
                                //Updates the amount of runes crafted
                                setCraftedRunes(566, (i.getQuantity() - sessionTrackingCurrent.get(566)) + getCraftedRunes(566));
                                sessionTrackingCurrent.replace(i.getId(), i.getQuantity());
                            }
                            isSoulRunecrafting = true;
                        }
                    }
                }
            }
        }
    }

    private void setCraftedRunes(int itemId, double quantity) {
        if (itemId == 565) {
            configManager.setRSProfileConfiguration("zeahrctracker", "bloods", quantity);
        } else if (itemId == 566) {
            configManager.setRSProfileConfiguration("zeahrctracker", "souls", quantity);
        }
    }

    public Double getCraftedRunes(Integer itemId) {
        if (itemId == 565) {
            return configManager.getRSProfileConfiguration("zeahrctracker", "bloods", double.class);
        } else if (itemId == 566) {
            return configManager.getRSProfileConfiguration("zeahrctracker", "souls", double.class);
        }
        return null;
    }

    @Provides
    ZeahRCTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ZeahRCTrackerConfig.class);
    }
}
