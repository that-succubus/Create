package com.simibubi.create.foundation.item.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.lib.util.ItemRendererHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class PartialItemModelRenderer {

	private static final PartialItemModelRenderer INSTANCE = new PartialItemModelRenderer();

	private final Random random = new Random();

	private ItemStack stack;
	private ItemTransforms.TransformType transformType;
	private PoseStack ms;
	private MultiBufferSource buffer;
	private int overlay;

	public static PartialItemModelRenderer of(ItemStack stack, ItemTransforms.TransformType transformType,
		PoseStack ms, MultiBufferSource buffer, int overlay) {
		PartialItemModelRenderer instance = INSTANCE;
		instance.stack = stack;
		instance.transformType = transformType;
		instance.ms = ms;
		instance.buffer = buffer;
		instance.overlay = overlay;
		return instance;
	}

	public void render(BakedModel model, int light) {
		render(model, RenderTypes.getItemPartialTranslucent(), light);
	}

	public void renderSolid(BakedModel model, int light) {
		render(model, RenderTypes.getItemPartialSolid(), light);
	}

	public void renderSolidGlowing(BakedModel model, int light) {
		render(model, RenderTypes.getGlowingSolid(), light);
	}

	public void renderGlowing(BakedModel model, int light) {
		render(model, RenderTypes.getGlowingTranslucent(), light);
	}

	public void render(BakedModel model, RenderType type, int light) {
		if (stack.isEmpty())
			return;

		ms.pushPose();
		ms.translate(-0.5D, -0.5D, -0.5D);

		if (!model.isCustomRenderer())
			// FIXME FRAPI COMPAT
			renderBakedItemModel(model, light, ms,
				ItemRenderer.getFoilBufferDirect(buffer, type, true, stack.hasFoil()));
		else
			Minecraft.getInstance().getItemRenderer()
					.render(stack, transformType, false, ms, buffer, light, overlay, model);

		ms.popPose();
	}

	private void renderBakedItemModel(BakedModel model, int light, PoseStack ms, VertexConsumer buffer) {
		ItemRenderer ir = Minecraft.getInstance()
			.getItemRenderer();
//		IModelData data = EmptyModelData.INSTANCE;

		for (Direction direction : Iterate.directions) {
			random.setSeed(42L);
			ItemRendererHelper.renderQuadList(ir, ms, buffer, model.getQuads(null, direction, random), stack, light, overlay);
		}

		random.setSeed(42L);
		ItemRendererHelper.renderQuadList(ir, ms, buffer, model.getQuads(null, null, random), stack, light, overlay);
	}

}
