package com.zeahrctracker;

import lombok.extern.slf4j.Slf4j;
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
		if (number >= 1000 && number < 10000)
		{
			sb.insert(1, ",");
			return sb.toString();
		}
		else if (number >= 10000 && number < 100000)
		{
			sb.insert(2, ",");
			return sb.toString();
		}
		else if (number >= 100000 && number < 1000000)
		{
			sb.insert(3, ",");
			return sb.toString();
		}
		else if (number >= 1000000)
		{
			sb.insert(4, ",");
			sb.insert(1, ",");
			return sb.toString();
		}
		return string;
	}

	private String convertGP(int number)
	{
		String string = String.valueOf(number);
		StringBuilder sb;

		if (number >= 1000 && number < 100000)
		{
			sb = new StringBuilder(string);
			sb.insert(1, ",");
			return sb.toString();
		}
		else if (number >= 100000 && number < 1000000)
		{
			return string.substring(0, 3) + "K";
		}
		else if (number >= 1000000 && number < 10000000)
		{
			string = string.substring(0, 3);
			sb = new StringBuilder(string);
			sb.insert(1, ".");
			sb.append("M");
			return sb.toString();
		}
		else if (number >= 10000000 && number < 100000000)
		{
			string = string.substring(0, 3);
			sb = new StringBuilder(string);
			sb.insert(2, ".");
			sb.append("M");
			return sb.toString();
		}
		else if (number >= 100000000 && number < 1000000000)
		{
			string = string.substring(0, 3);
			sb = new StringBuilder(string);
			sb.append("M");
			return sb.toString();
		}
		else if (number >= 1000000000)
		{
			return "Lots!";
		}
		return string;
	}
}