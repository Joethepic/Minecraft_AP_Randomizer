package gg.archipelago.aprandomizer.common.Utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintColor;
import gg.archipelago.APClient.Print.APPrintPart;
import gg.archipelago.APClient.Print.APPrintType;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class Utils {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * @param source command source to send the message.
     * @param Message Message to send
     * send a message to whoever ran the command.
     */

    private static final MinecraftServer server = APRandomizer.getServer();

    public static void SendMessage(CommandSourceStack source, String Message) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            player.sendMessage(new TextComponent(Message), Util.NIL_UUID);
        } catch (CommandSyntaxException e) {
            source.getServer().sendMessage(new TextComponent(Message), Util.NIL_UUID);
        }
    }

    public static void sendMessageToAll(String message) {
        sendMessageToAll(new TextComponent(message));
    }

    public static void sendMessageToAll(Component message) {
        //tell the server to send the message in a thread safe way.
        server.execute(() -> {
            server.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
        });

    }

    public static void sendFancyMessageToAll(APPrint apPrint) {
        Component message = Utils.apPrintToTextComponent(apPrint);

        //tell the server to send the message in a thread safe way.
        server.execute(() -> {
            server.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
        });

    }

    public static Component apPrintToTextComponent(APPrint apPrint) {
        TextComponent message = new TextComponent("");
        for (int i = 0; apPrint.parts.length > i; ++i) {
            APPrintPart part = apPrint.parts[i];
            LOGGER.trace("part[" + i + "]: " + part.text + ", " + part.color + ", " + part.type);
            Style style = Style.EMPTY;
            if (part.color == null) {
                if (APRandomizer.getAP().getMyName().equals(part.text)) {
                    style = Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.value)).withBold(true);
                } else if (part.type == APPrintType.playerID) {
                    style = Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.yellow.value));
                } else if (part.type == APPrintType.locationID) {
                    style = Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.green.value));
                } else if (part.type == APPrintType.itemID) {
                    style = Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.cyan.value));
                }
            } else if (part.color == APPrintColor.underline)
                style = Style.EMPTY.withUnderlined(true);
            else if (part.color == APPrintColor.bold)
                style = Style.EMPTY.withBold(true);
            else
                style = Style.EMPTY.withColor(TextColor.fromRgb(part.color.value));

            message.append(new TextComponent(part.text).withStyle(style));
        }
        return message;
    }

    public static void sendTitleToAll(Component title, Component subTitle, int fadeIn, int stay, int fadeOut) {
        server.execute(() -> {
            TitleQueue.queueTitle(new QueuedTitle(server.getPlayerList().getPlayers(), fadeIn, stay, fadeOut, subTitle, title));
        });
    }

    public static void sendTitleToAll(Component title, Component subTitle, Component chatMessage, int fadeIn, int stay, int fadeOut) {
        server.execute(() -> {
            TitleQueue.queueTitle(new QueuedTitle(server.getPlayerList().getPlayers(), fadeIn, stay, fadeOut, subTitle, title, chatMessage));
        });
    }

    public static void sendActionBarToAll(String actionBarMessage, int fadeIn, int stay, int fadeOut) {
        server.execute(() -> {
            TitleUtils.setTimes(server.getPlayerList().getPlayers(), fadeIn, stay, fadeOut);

            Component subTitleMessage = new TextComponent(actionBarMessage);

            TitleUtils.showActionBar(server.getPlayerList().getPlayers(), subTitleMessage);
        });
    }

    public static void sendActionBarToPlayer(ServerPlayer player, String actionBarMessage, int fadeIn, int stay, int fadeOut) {
        server.execute(() -> {
            TitleUtils.setTimes(Collections.singletonList(player), fadeIn, stay, fadeOut);

            Component text = new TextComponent(actionBarMessage);

            TitleUtils.showActionBar(Collections.singletonList(player), text);
        });
    }

    public static void PlaySoundToAll(SoundEvent sound) {
        server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.playNotifySound(sound, SoundSource.MASTER, 1, 1);
            }
        });
    }

    public static void SpawnDragon(ServerLevel end) {
        end.dragonFight.spawnExitPortal(false);
        end.dragonFight.findOrCreateDragon();
        end.dragonFight.dragonKilled = false;
        end.dragonFight.previouslyKilled = false;
        end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new).setDragonState(WorldData.DRAGON_SPAWNED);
    }

    public static StructureFeature<?> getCorrectStructure(StructureFeature<?> structure) {
        // if any of these structures appear in the nether we need to change the compass
        // to point to our structure instead of the vanilla one.

        String structureName = Utils.getAPStructureName(structure);

        //fetch what structures are where from our APMC data.
        HashMap<String, String> structures = APRandomizer.getApmcData().structures;
        if(structures == null)
            return structure;
        String nStructure1 = structures.get("Nether Structure 1");
        String nStructure2 = structures.get("Nether Structure 2");
        String eStructure = structures.get("The End Structure");

        if (structureName.equals(nStructure1) || structureName.equals(nStructure2)) {
            switch (structureName) {
                case "Village":
                    return APStructures.VILLAGE_NETHER.get();
                case "Pillager Outpost":
                    return APStructures.PILLAGER_OUTPOST_NETHER.get();
                case "End City":
                    return APStructures.END_CITY_NETHER.get();
            }
        }

        if (structureName.equals(eStructure)) {
            switch (structureName) {
                case "Village":
                    return APStructures.VILLAGE_NETHER.get();
            }
        }
        return structure;
    }

    public static ResourceKey<Level> getStructureWorld(StructureFeature<?> structure) {

        String structureName = getAPStructureName(structure);
        String world = "overworld";
        //fetch what structures are where from our APMC data.
        HashMap<String, String> structures = APRandomizer.getApmcData().structures;
        for (Map.Entry<String, String> entry : structures.entrySet()) {
            if(entry.getValue().equals(structureName)) {
                if (entry.getKey().contains("Overworld")) {
                    return Level.OVERWORLD;
                }
                if(entry.getKey().contains("Nether")) {
                    return Level.NETHER;
                }
                if(entry.getKey().contains("The End")) {
                    return Level.END;
                }
            }
        }

        return Level.OVERWORLD;
    }

    public static String getAPStructureName(StructureFeature<?> structure) {
        switch(structure.getRegistryName().getPath().toLowerCase()) {
            case "village_nether":
            case "village":
                return "Village";
            case "end_city_nether":
            case "endcity":
                return "End City";
            case "pillager_outpost_nether":
            case "pillager_outpost":
                return "Pillager Outpost";
            case "fortress":
                return "Nether Fortress";
            case "bastion_remnant":
                return "Bastion Remnant";
            default:
                return structure.getRegistryName().getPath().toLowerCase();
        }
    }

    public static void addLodestoneTags(ResourceKey<Level> worldRegistryKey, BlockPos blockPos, CompoundTag nbt) {
        nbt.put("LodestonePos", NbtUtils.writeBlockPos(blockPos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, worldRegistryKey).resultOrPartial(LOGGER::error).ifPresent((p_234668_1_) -> {
            nbt.put("LodestoneDimension", p_234668_1_);
        });
        nbt.putBoolean("LodestoneTracked", false);
    }
}
