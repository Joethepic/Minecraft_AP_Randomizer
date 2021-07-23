package gg.archipelago.aprandomizer.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.itemmanager.Trap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public class BeeTrap implements Trap {

    final private int numberOfBees;

    public BeeTrap(int numberOfBees) {
        this.numberOfBees = numberOfBees;
    }
    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();
            for (int i = 0; i < numberOfBees; i++) {
                Bee bee = EntityType.BEE.create(world);
                double radius = 5;
                double a = Math.random()*Math.PI*2;
                double b= Math.random()*Math.PI/2;
                double x = radius * Math.cos(a) * Math.sin(b) + pos.x;
                double z = radius * Math.sin(a) * Math.sin(b) + pos.z;
                double y = radius * Math.cos(b) + pos.y;
                Vec3 offset = new Vec3(x,y,z);
                bee.moveTo(offset);
                bee.setPersistentAngerTarget(player.getUUID());
                bee.setRemainingPersistentAngerTime(1200);
                world.addFreshEntity(bee);
            }

        });
    }
}
