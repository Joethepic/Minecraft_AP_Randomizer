package gg.archipelago.aprandomizer.common.Utils;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class TitleUtils {

    static void resetTitle(Collection<ServerPlayer> players) {
        ClientboundClearTitlesPacket stitlepacket = new ClientboundClearTitlesPacket(true);

        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(stitlepacket);
        }
    }

    static void showTitle(Collection<ServerPlayer> players, Component title, Component subTitle) {
        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(new ClientboundSetSubtitleTextPacket(subTitle));
            serverplayerentity.connection.send(new ClientboundSetTitleTextPacket(title));
        }
    }

    static void showActionBar(Collection<ServerPlayer> players, Component actionBarText) {
        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(new ClientboundSetActionBarTextPacket(actionBarText));
        }
    }

    static void setTimes(Collection<ServerPlayer> players, int fadeIn, int stay, int fadeOut) {
        ClientboundSetTitlesAnimationPacket stitlepacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);

        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(stitlepacket);
        }

    }

}
