package com.zeahrctracker;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class ZeahRCTrackerOverlay extends OverlayPanel {

    static final String RC_RESET = "Reset";

    private final Client client;
    private final ZeahRCTrackerPlugin plugin;
    private final ZeahRCTrackerConfig config;

    @Inject
    private ItemManager itemManager;


    @Inject
    private ZeahRCTrackerOverlay(Client client, ZeahRCTrackerPlugin plugin, ZeahRCTrackerConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, RC_RESET, "Runecrafting Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.bloodCheckbox()) {
            return null;
        }


        if (plugin.isBloodRunecrafting && plugin.isSoulRunecrafting) {
            if (config.bloodCheckbox() && config.soulCheckbox()) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Zeah RC Tracker")
                        .color(Color.BLUE)
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Bloods Crafted:")
                        .right(numberFormat(plugin.getCraftedRunes(565)))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total Profit:")
                        .right(convertGP(itemManager.getItemPrice(566) * plugin.getCraftedRunes(565)) + "gp")
                        .rightColor(Color.green)
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Souls Crafted:")
                        .right(numberFormat(plugin.getCraftedRunes(566)))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total Profit:")
                        .right(convertGP(itemManager.getItemPrice(566) * plugin.getCraftedRunes(566)) + "gp")
                        .rightColor(Color.green)
                        .build());
            }
        } else if (plugin.isBloodRunecrafting && config.bloodCheckbox()) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Zeah RC Tracker")
                    .color(Color.RED)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Bloods Crafted:")
                    .right(numberFormat(plugin.getCraftedRunes(565)))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Profit:")
                    .right(convertGP(itemManager.getItemPrice(566) * plugin.getCraftedRunes(565)) + "gp")
                    .rightColor(Color.green)
                    .build());
        } else if (plugin.isSoulRunecrafting && config.soulCheckbox()) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Zeah RC Tracker")
                    .color(Color.CYAN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Souls Crafted:")
                    .right(numberFormat(plugin.getCraftedRunes(566)))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Profit:")
                    .right(convertGP(itemManager.getItemPrice(566) * plugin.getCraftedRunes(566)) + "gp")
                    .rightColor(Color.green)
                    .build());
        }
        return super.render(graphics);
    }

    private String numberFormat(double number) {
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(number);
    }

    private String convertGP(double number) {
        String string = numberFormat(number);
        if (number >= 100000 && number < 1000000) {
            return string.substring(0, 3) + "K ";
        } else if (number >= 1000000 && number < 10000000) {
            string = string.substring(0, 3);
            StringBuilder sb = new StringBuilder(string);
            sb.insert(1, ".");
            sb.append("M ");
            sb.deleteCharAt(4);
            return sb.toString();
        } else if (number >= 10000000) {
            string = string.substring(0, 3);
            StringBuilder sb = new StringBuilder(string);
            sb.insert(2, ".");
            sb.append("M ");
            sb.deleteCharAt(4);
            return sb.toString();
        }
        return string;
    }
}