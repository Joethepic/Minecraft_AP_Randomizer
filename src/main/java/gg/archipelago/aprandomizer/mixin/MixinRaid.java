package gg.archipelago.aprandomizer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.server.level.ServerLevel;
/*import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;*/

//@Mixin(Raid.class)
public abstract class MixinRaid {

/*    @Shadow
    private ServerLevel level;

    @Shadow
    public abstract BlockPos getCenter();

    @Inject(method = "findRandomSpawnPos(II)Lnet/minecraft/util/math/BlockPos;", at = @At(value = "HEAD"), cancellable = true)
    protected void onFindRandomSpawnPos(int p_221298_1_, int p_221298_2_, CallbackInfoReturnable<BlockPos> cir) {
        if (this.level.dimension() == Level.NETHER) {
            cir.setReturnValue(getCenter());
        }
    }*/
}
