package com.simibubi.create.content.contraptions.components.actors;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.curiosities.bell.AbstractBellBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class BellMovementBehaviour extends MovementBehaviour {

	@Override
	public boolean renderAsNormalTileEntity() {
		return true;
	}

	@Override
	public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
		double dotProduct = oldMotion.dot(motion);

		if (dotProduct <= 0 && (context.relativeMotion.length() != 0) || context.firstMovement)
			playSound(context);
	}

	@Override
	public void stopMoving(MovementContext context) {
		if (context.position != null)
			playSound(context);
	}

	public static void playSound(MovementContext context) {
		Level world = context.world;
		BlockPos pos = new BlockPos(context.position);
		Block block = context.state.getBlock();

		if (block instanceof AbstractBellBlock) {
			((AbstractBellBlock<?>) block).playSound(world, pos);
		} else {
			// Vanilla bell sound
			world.playSound(null, pos, SoundEvents.BELL_BLOCK,
					SoundSource.BLOCKS, 2f, 1f);
		}
	}
}
