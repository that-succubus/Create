package com.simibubi.create.foundation.block.entity.behaviour;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform.Sided;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.NamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.renderState.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.outliner.ChasingAABBOutline;

public class ValueBox extends ChasingAABBOutline {
	protected Text label;
	protected Text sublabel = LiteralText.EMPTY;
	protected Text scrollTooltip = LiteralText.EMPTY;
	protected Vec3d labelOffset = Vec3d.ZERO;

	protected int passiveColor;
	protected int highlightColor;
	public boolean isPassive;

	protected BlockPos pos;
	protected ValueBoxTransform transform;
	protected BlockState blockState;

	public ValueBox(Text label, Box bb, BlockPos pos) {
		super(bb);
		this.label = label;
		this.pos = pos;
		this.blockState = MinecraftClient.getInstance().world.getBlockState(pos);
	}

	public ValueBox transform(ValueBoxTransform transform) {
		this.transform = transform;
		return this;
	}

	public ValueBox offsetLabel(Vec3d offset) {
		this.labelOffset = offset;
		return this;
	}

	public ValueBox subLabel(Text sublabel) {
		this.sublabel = sublabel;
		return this;
	}

	public ValueBox scrollTooltip(Text scrollTip) {
		this.scrollTooltip = scrollTip;
		return this;
	}

	public ValueBox withColors(int passive, int highlight) {
		this.passiveColor = passive;
		this.highlightColor = highlight;
		return this;
	}

	public ValueBox passive(boolean passive) {
		this.isPassive = passive;
		return this;
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		boolean hasTransform = transform != null;
		if (transform instanceof Sided && params.getHighlightedFace() != null)
			((Sided) transform).fromSide(params.getHighlightedFace());
		if (hasTransform && !transform.shouldRender(blockState))
			return;

		ms.push();
		ms.translate(pos.getX(), pos.getY(), pos.getZ());
		if (hasTransform)
			transform.transform(blockState, ms);
		transformNormals = ms.peek()
			.getNormal()
			.copy();
		params.colored(isPassive ? passiveColor : highlightColor);
		super.render(ms, buffer);

		float fontScale = hasTransform ? -transform.getFontScale() : -1 / 64f;
		ms.scale(fontScale, fontScale, fontScale);

		ms.push();
		renderContents(ms, buffer);
		ms.pop();

		if (!isPassive) {
			ms.push();
			ms.translate(17.5, -.5, 7);
			ms.translate(labelOffset.x, labelOffset.y, labelOffset.z);

			renderHoveringText(ms, buffer, label);
			if (!sublabel.toString().isEmpty()) {
				ms.translate(0, 10, 0);
				renderHoveringText(ms, buffer, sublabel);
			}
			if (!scrollTooltip.asString().isEmpty()) {
				ms.translate(0, 10, 0);
				renderHoveringText(ms, buffer, scrollTooltip, 0x998899, 0x111111);
			}

			ms.pop();
		}

		ms.pop();
	}

	public void renderContents(MatrixStack ms, VertexConsumerProvider buffer) {}

	public static class ItemValueBox extends ValueBox {
		ItemStack stack;
		int count;

		public ItemValueBox(Text label, Box bb, BlockPos pos, ItemStack stack, int count) {
			super(label, bb, pos);
			this.stack = stack;
			this.count = count;
		}

		@Override
		public void renderContents(MatrixStack ms, VertexConsumerProvider buffer) {
			super.renderContents(ms, buffer);
			TextRenderer font = MinecraftClient.getInstance().textRenderer;
			Text countString = new LiteralText(count == 0 ? "*" : count + "");
			ms.translate(17.5f, -5f, 7f);

			boolean isFilter = false; //stack.getItem() instanceof FilterItem;
			boolean isEmpty = stack.isEmpty();
			float scale = 1.5f;
			ms.translate(-font.getWidth(countString), 0, 0);
			
			if (isFilter)
				ms.translate(3, 8, 7.25f);
			else if (isEmpty) {
				ms.translate(-17, -2, 3f);
				scale = 2f;
			}
			else
				ms.translate(-7, 10, 10 + 1 / 4f);

			ms.scale(scale, scale, scale);
			drawString(ms, buffer, countString, 0, 0, isFilter ? 0xFFFFFF : 0xEDEDED);
			ms.translate(0, 0, -1 / 16f);
			drawString(ms, buffer, countString, 1 - 1 / 8f, 1 - 1 / 8f, 0x4F4F4F);
		}

	}

	public static class TextValueBox extends ValueBox {
		Text text;

		public TextValueBox(Text label, Box bb, BlockPos pos, Text text) {
			super(label, bb, pos);
			this.text = text;
		}

		@Override
		public void renderContents(MatrixStack ms, VertexConsumerProvider buffer) {
			super.renderContents(ms, buffer);
			TextRenderer font = MinecraftClient.getInstance().textRenderer;
			float scale = 4;
			ms.scale(scale, scale, 1);
			ms.translate(-4, -4, 5);

			int stringWidth = font.getWidth(text);
			float numberScale = (float) font.fontHeight / stringWidth;
			boolean singleDigit = stringWidth < 10;
			if (singleDigit)
				numberScale = numberScale / 2;
			float verticalMargin = (stringWidth - font.fontHeight) / 2f;

			ms.scale(numberScale, numberScale, numberScale);
			ms.translate(singleDigit ? stringWidth / 2 : 0, singleDigit ? -verticalMargin : verticalMargin, 0);

			renderHoveringText(ms, buffer, text, 0xEDEDED, 0x4f4f4f);
		}

	}

	public static class IconValueBox extends ValueBox {
		AllIcons icon;

		public IconValueBox(Text label, NamedIconOptions iconValue, Box bb, BlockPos pos) {
			super(label, bb, pos);
			subLabel(Lang.translate(iconValue.getTranslationKey()));
			icon = iconValue.getIcon();
		}

		@Override
		public void renderContents(MatrixStack ms, VertexConsumerProvider buffer) {
			super.renderContents(ms, buffer);
			float scale = 4 * 16;
			ms.scale(scale, scale, scale);
			ms.translate(-.5f, -.5f, 1 / 32f);
			icon.draw(ms, buffer, 0xFFFFFF);
		}

	}

	// util

	protected void renderHoveringText(MatrixStack ms, VertexConsumerProvider buffer, Text text) {
		renderHoveringText(ms, buffer, text, highlightColor, ColorHelper.mixColors(passiveColor, 0, 0.75f));
	}

	protected void renderHoveringText(MatrixStack ms, VertexConsumerProvider buffer, Text text, int color,
		int shadowColor) {
		ms.push();
		drawString(ms, buffer, text, 0, 0, color);
		ms.translate(0, 0, -.25);
		drawString(ms, buffer, text, 1, 1, shadowColor);
		ms.pop();
	}

	private static void drawString(MatrixStack ms, VertexConsumerProvider buffer, Text text, float x, float y, int color) {
		MinecraftClient.getInstance().textRenderer.draw(text, x, y, color, false, ms.peek()
			.getModel(), buffer, false, 0, 15728880);
	}
}
