package com.packetl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Packetl implements ModInitializer {
	public static final String MOD_ID = "template";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final int MAX_BOOK_SIZE = 30000; // 50KB in bytes

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				checkBooks(player);
			}
		});
	}

	private void checkBooks(ServerPlayerEntity player) {
		for (ItemStack itemStack : player.getInventory().main) {
			if (itemStack.getItem() == Items.WRITTEN_BOOK) {
				WrittenBookContentComponent component = itemStack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
				if (component != null) {
					// Combine content into a single string for size calculation
					String content = component.title() + component.author() + component.pages().toString();
					int dataSize = content.getBytes().length;
					if (dataSize > MAX_BOOK_SIZE) {
						player.getInventory().removeOne(itemStack);
						player.sendMessage(Text.literal("A book exceeding 30KB was removed from your inventory."), false);
					}
				}
			}
		}
	}
}
