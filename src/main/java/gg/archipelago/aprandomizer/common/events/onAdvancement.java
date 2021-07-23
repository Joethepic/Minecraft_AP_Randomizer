package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Mod.EventBusSubscriber
public class onAdvancement {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent event) {
        //dont do any checking if the apmcdata file is not valid.
        if (APRandomizer.getApmcData().state != APMCData.State.VALID)
            return;

        ServerPlayer player = (ServerPlayer) event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String id = advancement.getId().toString();

        AdvancementManager am = APRandomizer.getAdvancementManager();
        if (!am.hasAdvancement(id)) {
            LOGGER.debug("{} has gotten the advancement {}", player.getDisplayName().getString(), id);
            am.addAdvancement(am.getAdvancementID(id));

            String remaining = String.format(" (%d)", am.getFinishedAmount());
            if (am.getRequiredAmount() > 0)
                remaining = String.format(" (%d / %d)", am.getFinishedAmount(), am.getRequiredAmount());

            APRandomizer.getServer().getPlayerList().broadcastMessage(
                    new TranslatableComponent(
                            "chat.type.advancement."
                                    + advancement.getDisplay().getFrame().getName(),
                            player.getDisplayName(),
                            advancement.getChatComponent()
                    ).append(
                            new TextComponent(
                                    remaining
                            )
                    ),
                    ChatType.SYSTEM,
                    Util.NIL_UUID
            );

            am.syncAdvancement(advancement);
            APRandomizer.getBossBar().setValue(am.getFinishedAmount());
            MutableComponent advBar = new TextComponent("Advancements");
            if(!APRandomizer.getAP().isConnected()) {
                advBar = new TextComponent("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red")));
            }
            APRandomizer.getBossBar().setName(advBar.append(new TextComponent(remaining)));
            if (am.getRequiredAmount() != 0) {
                if (am.getFinishedAmount() >= am.getRequiredAmount()) {
                    ServerLevel end = event.getPlayer().getServer().getLevel(Level.END);
                    assert end != null;
                    assert end.dragonFight != null;
                    WorldData worldData = end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);

                    if (worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
                        Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
                        Utils.sendMessageToAll("The Dragon has awoken.");
                        Utils.sendTitleToAll(new TextComponent("Ender Dragon").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.ORANGE.getRGB()))), new TextComponent("has been awoken"), 40, 120, 40);
                        worldData.setDragonState(WorldData.DRAGON_SPAWNED);
                        Utils.SpawnDragon(end);
                    }
                }
            }
        }
    }
}
