package com.simibubi.create.lib.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Shadow
	protected ClientLevel level;

	@Shadow
	@Final
	@Mutable
	private static List<ParticleRenderType> RENDER_ORDER;

	@Unique
	private static boolean create$replacedRenderOrderList = false;

	private static void create$addRenderType(ParticleRenderType type) {
		if (!create$replacedRenderOrderList) {
			List<ParticleRenderType> old = RENDER_ORDER;
			RENDER_ORDER = new ArrayList<>(old);
			create$replacedRenderOrderList = true;
		}
		RENDER_ORDER.add(type);
	}

	@Inject(method = "method_18125", at = @At("RETURN"))
	private static void create$addCustomRenderTypes(ParticleRenderType particleRenderType, CallbackInfoReturnable<Queue<Particle>> cir) {
		if (!RENDER_ORDER.contains(particleRenderType)) {
			create$addRenderType(particleRenderType);
		}
	}
}
