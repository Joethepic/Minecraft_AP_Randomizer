package gg.archipelago.aprandomizer.capability;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerData {

    private int index = 0;

    public PlayerData() {
        this(0);
    }

    public PlayerData(int initialIndex) {
        this.index = initialIndex;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
