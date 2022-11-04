package com.zeahrctracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ZeahRCTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ZeahRCTrackerPlugin.class);
		RuneLite.main(args);
	}
}