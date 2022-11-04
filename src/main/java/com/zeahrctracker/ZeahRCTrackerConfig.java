package com.zeahrctracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zeahrctracker")
public interface ZeahRCTrackerConfig extends Config
{

    @ConfigItem(keyName = "bloods", name = "Track Bloods", description = "Enables the tracking of blood runes")
    default boolean bloodCheckbox(){
        return true;
    }

    @ConfigItem(keyName = "souls", name = "Track Souls", description = "Enables the tracking of soul runes")
    default boolean soulCheckbox(){
        return true;
    }

}
