package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.Trap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.mob.Husk;
import net.minecraft.world.phys.Vec3;

public class HuskTrap implements Trap {

    final private int numberOfHusks;

    public HuskTrap(int numberOfHusks) {
        this.numberOfHusks = numberOfHusks;
    }
    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();
            for (int i = 0; i < numberOfHusks; i++) {
                Husk husk = EntityType.HUSK.create(world);
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                husk.moveTo(offset);
                world.addFreshEntity(husk);
            }

        });
    }
}
