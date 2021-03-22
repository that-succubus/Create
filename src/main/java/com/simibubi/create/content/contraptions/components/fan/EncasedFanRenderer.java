package com.simibubi.create.content.contraptions.components.fan;

import static net.minecraft.state.property.Properties.FACING;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

public class EncasedFanRenderer extends KineticBlockEntityRenderer {

	public EncasedFanRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticBlockEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
							  int light, int overlay) {
		if (FastRenderDispatcher.available(te.getWorld())) return;

		Direction direction = te.getCachedState()
			.get(FACING);
		VertexConsumer vb = buffer.getBuffer(RenderLayer.getCutoutMipped());

		int lightBehind = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(direction.getOpposite()));
		int lightInFront = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(direction));
		
		SuperByteBuffer shaftHalf =
			AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouth(te.getCachedState(), direction.getOpposite());
		SuperByteBuffer fanInner =
			AllBlockPartials.ENCASED_FAN_INNER.renderOnDirectionalSouth(te.getCachedState(), direction.getOpposite());
		
		float time = AnimationTickHolder.getRenderTick();
		float speed = te.getSpeed() * 5;
		if (speed > 0)
			speed = MathHelper.clamp(speed, 80, 64 * 20);
		if (speed < 0)
			speed = MathHelper.clamp(speed, -64 * 20, -80);
		float angle = (time * speed * 3 / 10f) % 360;
		angle = angle / 180f * (float) Math.PI;

		standardKineticRotationTransform(shaftHalf, te, lightBehind).renderInto(ms, vb);
		kineticRotationTransform(fanInner, te, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
	}

}
