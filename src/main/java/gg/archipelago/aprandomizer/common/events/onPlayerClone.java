package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.APClient.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onPlayerClone {
    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        //check if this dimension transition is the cause of entering the end portal.
        if (
                event.getOriginal().level.dimension().equals(Level.END)
                        && !event.isWasDeath()
                        && event.getPlayer().level.dimension().equals(Level.OVERWORLD)
                        && APRandomizer.getAdvancementManager().getFinishedAmount() >= APRandomizer.getAdvancementManager().getRequiredAmount()
        ) {
            if (APRandomizer.getAP().isConnected()) {
                APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
            }
        }
        Player player = event.getOriginal();
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if (loPlayerData.isPresent()) {
            PlayerData originalPlayerData = loPlayerData.orElseThrow(AssertionError::new);
            event.getPlayer().getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA).orElseThrow(AssertionError::new).setIndex(originalPlayerData.getIndex());
        }
    }
}
