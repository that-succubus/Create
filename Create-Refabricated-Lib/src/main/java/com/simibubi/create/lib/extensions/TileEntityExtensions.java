package com.simibubi.create.lib.extensions;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public interface TileEntityExtensions {
	CompoundNBT create$getExtraCustomData();

	void create$deserializeNBT(BlockState state, CompoundNBT nbt);
}
