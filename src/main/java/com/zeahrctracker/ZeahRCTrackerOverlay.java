package com.zeahrctracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;

import net.runelite.api.MenuAction;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
public class ZeahRCTrackerOverlay extends OverlayPanel
{

	static final String RC_RESET = "Reset";

	private final ZeahRCTrackerPlugin plugin;
	private final ZeahRCTrackerConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ZeahRCTrackerOverlay(ZeahRCTrackerPlugin plugin, ZeahRCTrackerConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		this.plugin = plugin;
		this.config = config;
		getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, RC_RESET, "Runecrafting Overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (!config.bloodCheckbox() && !config.soulCheckbox())
		{
			return null;
		}

		if (plugin.isBloodRunecrafting && config.bloodCheckbox())
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Blood Runecrafting")
				.color(Color.RED)
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Bloods Crafted:")
				.right(convertRunes(plugin.getCraftedRunes(plugin.bloodRuneID)))
				.build());
			if (config.profitCheckbox())
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Total Profit:")
					.right(convertGP(itemManager.getItemPrice(plugin.bloodRuneID) * plugin.getCraftedRunes(plugin.bloodRuneID)) + " gp")
					.rightColor(Color.green)
					.build());
			}
		}
		if (plugin.isSoulRunecrafting && config.soulCheckbox())
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Soul Runecrafting")
				.color(Color.CYAN)
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Souls Crafted:")
				.right(convertRunes(plugin.getCraftedRunes(plugin.soulRuneID)))
				.build());
			if (config.profitCheckbox())
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Total Profit:")
					.right(convertGP(itemManager.getItemPrice(plugin.soulRuneID) * plugin.getCraftedRunes(plugin.soulRuneID)) + " gp")
					.rightColor(Color.green)
					.build());
			}
		}
		return super.render(graphics);
	}

	private String convertRunes(int number)
	{
		String string = String.valueOf(number);
		StringBuilder sb = new StringBuilder(string);

		switch (string.length())
		{
			case 4:
				sb.insert(1, ",");
				return sb.toString();
			case 5:
				sb.insert(2, ",");
				return sb.toString();
			case 6:
				sb.insert(3, ",");
				return sb.toString();
			default:
				return string;
		}
	}

	private String convertGP(int number)
	{
		String string = String.valueOf(number);
		StringBuilder sb;
		int length = string.length();

		if (length >= 7)
		{
			string = string.substring(0, 3);
		}
		sb = new StringBuilder(string);

		switch (length)
		{
			case 4:
				sb.insert(1, ",");
				return sb.toString();
			case 5:
				sb.insert(2, ",");
				return sb.toString();
			case 6:
				return string.substring(0, 3) + "K";
			case 7:
				sb.insert(1, ".");
				sb.append("M");
				return sb.toString();
			case 8:
				sb.insert(2, ".");
				sb.append("M");
				return sb.toString();
			default:
				return string;
		}
	}
}