package com.zeahrctracker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;

@Slf4j
@PluginDescriptor(name = "Zeah RC Tracker")
public class ZeahRCTrackerPlugin extends Plugin
{
	private HashMap<Integer, Integer> sessionTracking;

	private int bloodRunesCrafted;
	private int soulRunesCrafted;
	public boolean isBloodRunecrafting;
	public boolean isSoulRunecrafting;
	public final int bloodRuneID = 565;
	public final int soulRuneID = 566;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ZeahRCTrackerOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("ZeahRCTracker started!");
		sessionTracking = new HashMap<>();
		bloodRunesCrafted = 0;
		soulRunesCrafted = 0;
		isBloodRunecrafting = false;
		isSoulRunecrafting = false;
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("ZeahRCTracker stopped!");
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{

		//Load to hashmap on login
		if (sessionTracking.isEmpty())
		{
			ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

			if (container == null)
			{
				return;
			}

			if (!container.contains(bloodRuneID))
			{
				sessionTracking.put(bloodRuneID, 0);
			}
			if (!container.contains(soulRuneID))
			{
				sessionTracking.put(soulRuneID, 0);
			}

			for (int i = 0; i <= 27; i++)
			{
				if (container.getItem(i) != null && (container.getItem(i).getId() == bloodRuneID || container.getItem(i).getId() == soulRuneID))
				{
					sessionTracking.put(container.getItem(i).getId(), container.getItem(i).getQuantity());
				}
			}
			return;
		}

		//Runecrafting event
		if (event.getItemContainer().contains(bloodRuneID) || event.getItemContainer().contains(soulRuneID))
		{
			//Region check (Blood/Soul altar regions)
			boolean inZeah = false;
			int region = client.getLocalPlayer().getWorldLocation().getRegionID();
			if (region == 6715 || region == 7228)
			{
				inZeah = true;
			}

			//Prevents tracker from including runes outside zeah
			if (!inZeah)
			{
				sessionTracking.replace(bloodRuneID, 0);
				sessionTracking.replace(soulRuneID, 0);
				return;
			}

			//Final checks
			for (Item i : event.getItemContainer().getItems())
			{
				if (i.getId() == bloodRuneID)
				{
					if (sessionTracking.get(i.getId()) != i.getQuantity())
					{
						//Updates the amount of runes crafted
						bloodRunesCrafted = bloodRunesCrafted + (i.getQuantity() - sessionTracking.get(bloodRuneID));
						sessionTracking.replace(i.getId(), i.getQuantity());
						isBloodRunecrafting = true;
						isSoulRunecrafting = false;
					}
				}
				else if (i.getId() == soulRuneID)
				{
					if (sessionTracking.get(i.getId()) != i.getQuantity())
					{
						//Updates the amount of runes crafted
						soulRunesCrafted = soulRunesCrafted + (i.getQuantity() - sessionTracking.get(soulRuneID));
						sessionTracking.replace(i.getId(), i.getQuantity());
						isSoulRunecrafting = true;
						isBloodRunecrafting = false;
					}
				}
			}
		}
	}

	public Integer getCraftedRunes(int itemId)
	{
		if (itemId == bloodRuneID)
		{
			return bloodRunesCrafted;
		}
		else if (itemId == soulRuneID)
		{
			return soulRunesCrafted;
		}
		return null;
	}

	@Provides
	ZeahRCTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZeahRCTrackerConfig.class);
	}
}
