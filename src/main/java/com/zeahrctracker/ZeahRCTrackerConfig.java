package com.zeahrctracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zeahrctracker")
public interface ZeahRCTrackerConfig extends Config
{

	@ConfigItem(keyName = "bloods", name = "Track Bloods", description = "Enables the tracking of blood runes", position = 0)
	default boolean bloodCheckbox()
	{
		return true;
	}

	@ConfigItem(keyName = "souls", name = "Track Souls", description = "Enables the tracking of soul runes", position = 1)
	default boolean soulCheckbox()
	{
		return true;
	}

	@ConfigItem(keyName = "trackprofit", name = "Track Profit", description = "Enables profit tracking", position = 2)
	default boolean profitCheckbox()
	{
		return true;
	}

}