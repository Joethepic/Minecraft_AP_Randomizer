package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class StructureEvents {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    static public void addDimensionalSpacing(WorldEvent.Load event) {
        if (!(event.getWorld() instanceof ServerLevel))
            return;
        ServerLevel serverWorld = (ServerLevel) event.getWorld();

        /*
         * Prevent spawning our structure in Vanilla's superflat world as
         * people seem to want their superflat worlds free of modded structures.
         * Also that vanilla superflat is really tricky and buggy to work with in my experience.
         */
        if (serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource &&
                serverWorld.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        /*
         * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
         * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
         *
         * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
         * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
         * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
         */
        Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
        tempMap.putIfAbsent(APStructures.VILLAGE_NETHER.get(), StructureSettings.DEFAULTS.get(APStructures.VILLAGE_NETHER.get()));
        tempMap.putIfAbsent(APStructures.END_CITY_NETHER.get(), StructureSettings.DEFAULTS.get(APStructures.END_CITY_NETHER.get()));
        tempMap.putIfAbsent(APStructures.PILLAGER_OUTPOST_NETHER.get(), StructureSettings.DEFAULTS.get(APStructures.PILLAGER_OUTPOST_NETHER.get()));
        serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
    }


/*    @SubscribeEvent(priority = EventPriority.HIGH)
    static public void onBiomeLoad(BiomeLoadingEvent event) {
        event.getGeneration().getStructures().add(() -> APConfiguredStructures.CONFIGURED_END_CITY_NETHER);
    }*/


}
