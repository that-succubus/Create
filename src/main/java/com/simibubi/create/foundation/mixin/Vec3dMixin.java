package com.simibubi.create.foundation.mixin;

import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.SERVER)
@Mixin(Vec3d.class)
public abstract class Vec3dMixin {
	@Shadow
	public abstract Vec3d multiply(double mult);

	// They are client-only, but not anymore!

	public Vec3d negate() {
		return multiply(-1.0D);
	}

	public Vec3d method_22882() {
		return negate();
	}
}
