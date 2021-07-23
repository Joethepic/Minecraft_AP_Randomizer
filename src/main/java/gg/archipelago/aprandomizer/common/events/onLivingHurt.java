package gg.archipelago.aprandomizer.common.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onLivingHurt {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof Pig) {
            if (entity.getPassengers().size() > 0) {
                if (entity.getPassengers().get(0) instanceof ServerPlayer) {
                    if (event.getSource().msgId.equals("fall")) {
                        ServerPlayer player = (ServerPlayer) entity.getPassengers().get(0);
                        Advancement advancement = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:husbandry/ride_pig"));
                        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancement);
                        if (!ap.isDone()) {
                            for (String s : ap.getRemainingCriteria()) {
                                player.getAdvancements().award(advancement, s);
                            }
                        }
                    }
                }
            }
        }
        Entity e = event.getSource().getEntity();
        if (e instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) e;
            if (event.getAmount() >= 18) {
                Advancement a = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/overkill"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(a);
                if (!ap.isDone()) {
                    for (String s : ap.getRemainingCriteria()) {
                        player.getAdvancements().award(a, s);
                    }
                }
            }
        }
    }
}
