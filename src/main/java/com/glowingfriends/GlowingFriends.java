package com.glowingfriends;

import com.glowingfriends.util.GetUUID;
import com.glowingfriends.util.GlowingManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GlowingFriends implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("glowing-friends");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Glowing Friends Initialized Successfully");

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			GlowingManager.saveGlowingPlayers();
		});

		// Register the commands and sub commands.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("GlowingFriends")
					.then(ClientCommandManager.literal("add")
							.then(ClientCommandManager.argument("player", StringArgumentType.string())
									.suggests((context, builder) -> suggestPlayers(context, builder))
									.executes(context -> {
										String playerName = StringArgumentType.getString(context, "player");
										MinecraftClient client = MinecraftClient.getInstance();

										UUID playerUUID;
										try {
											playerUUID = UUID.fromString(GetUUID.getUUID(playerName));
										} catch (Exception e) {
											context.getSource().sendError(Text.literal("Failed to fetch UUID for player " + playerName));
											return 0;
										}

										PlayerEntity player = client.world.getPlayerByUuid(playerUUID);

										if (player != null) {
											GlowingManager.addGlowingPlayer(playerUUID);
											context.getSource().sendFeedback(Text.literal("Player " + playerName + " is now glowing!"));
										} else {
											context.getSource().sendError(Text.literal("Player not found"));
										}
										return 1;
									})
							)
					)
					.then(ClientCommandManager.literal("remove")
							.then(ClientCommandManager.argument("player", StringArgumentType.string())
									.suggests((context, builder) -> suggestPlayers(context, builder))
									.executes(context -> {
										String playerName = StringArgumentType.getString(context, "player");
										MinecraftClient client = MinecraftClient.getInstance();

										UUID playerUUID;
										try {
											playerUUID = UUID.fromString(GetUUID.getUUID(playerName));
										} catch (Exception e) {
											context.getSource().sendError(Text.literal("Failed to fetch UUID for player " + playerName));
											return 0;
										}

										PlayerEntity player = client.world.getPlayerByUuid(playerUUID);

										if (player != null) {
											GlowingManager.removeGlowingPlayer(playerUUID);
											context.getSource().sendFeedback(Text.literal("Player " + playerName + " is now NOT glowing!"));
										} else {
											context.getSource().sendError(Text.literal("Player not found"));
										}
										return 1;
									})
							)
					);

			dispatcher.register(command);
		});
	}

	// suggestPlayers is what allows you to tab in the name of the player you want to glow!
	private CompletableFuture<Suggestions> suggestPlayers(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null) {
			for (PlayerEntity player : client.world.getPlayers()) {
				builder.suggest(player.getName().getString());
			}
		}
		return builder.buildFuture();
	}


}