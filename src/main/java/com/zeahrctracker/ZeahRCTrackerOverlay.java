package com.zeahrctracker;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.http.api.item.ItemPrice;

import javax.inject.Inject;
import java.awt.*;

public class ZeahRCTrackerOverlay extends OverlayPanel {

    static final String RC_RESET = "Reset";

    private final Client client;
    private final ZeahRCTrackerPlugin plugin;
    private final ZeahRCTrackerConfig config;

    @Inject
    private ItemManager itemManager;


    @Inject
    private ZeahRCTrackerOverlay(Client client,ZeahRCTrackerPlugin plugin,ZeahRCTrackerConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, RC_RESET, "Runecrafting Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.bloodCheckbox())
        {
            return null;
        }


        if (plugin.isrunecrafting)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Zeah RC Tracker")
                    .color(Color.CYAN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Bloods crafted:")
                    .right(plugin.getCraftedRunes(565).toString())
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Profit:")
                    .right(itemManager.getItemPrice(565) * plugin.getCraftedRunes(565) + "gp")
                    .rightColor(Color.green)
                    .build());
        }
        return super.render(graphics);
    }
}
