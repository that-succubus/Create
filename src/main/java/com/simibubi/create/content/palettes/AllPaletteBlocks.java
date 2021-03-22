package com.simibubi.create.content.palettes;

import static com.simibubi.create.foundation.data.WindowGen.customWindowBlock;
import static com.simibubi.create.foundation.data.WindowGen.customWindowPane;
import static com.simibubi.create.foundation.data.WindowGen.framedGlass;
import static com.simibubi.create.foundation.data.WindowGen.framedGlassPane;
import static com.simibubi.create.foundation.data.WindowGen.woodenWindowBlock;
import static com.simibubi.create.foundation.data.WindowGen.woodenWindowPane;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import me.pepperbell.reghelper.BlockRegBuilder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.simibubi.create.foundation.block.connected.StandardCTBehaviour;
import com.simibubi.create.foundation.data.WindowGen;

public class AllPaletteBlocks {
	private static ItemGroup itemGroup = Create.palettesCreativeTab;
	private static AllSections currentSection = AllSections.PALETTES;

	// Windows and Glass

	public static final GlassBlock TILED_GLASS = createBuilder("tiled_glass", GlassBlock::new)
		.initialProperties(() -> Blocks.GLASS)
		.addLayer(() -> RenderLayer::getCutoutMipped)
//		.recipe((c, p) -> p.stonecutting(DataIngredient.tag(Tags.Items.GLASS_COLORLESS), c::get))
//		.blockstate(palettesCubeAll())
//		.tag(Tags.Blocks.GLASS_COLORLESS, BlockTags.IMPERMEABLE)
		.item()
//		.tag(Tags.Items.GLASS_COLORLESS)
		.build()
		.register();

	public static final ConnectedGlassBlock FRAMED_GLASS =
		framedGlass("framed_glass", new StandardCTBehaviour(AllSpriteShifts.FRAMED_GLASS)),
		HORIZONTAL_FRAMED_GLASS = framedGlass("horizontal_framed_glass",
			new HorizontalCTBehaviour(AllSpriteShifts.HORIZONTAL_FRAMED_GLASS, AllSpriteShifts.FRAMED_GLASS)),
		VERTICAL_FRAMED_GLASS =
			framedGlass("vertical_framed_glass", new HorizontalCTBehaviour(AllSpriteShifts.VERTICAL_FRAMED_GLASS));

	public static final GlassPaneBlock TILED_GLASS_PANE =
		WindowGen.standardGlassPane("tiled_glass", () -> TILED_GLASS, Create.id("block/palettes/tiled_glass"),
			new Identifier("block/glass_pane_top"), () -> RenderLayer::getCutoutMipped);

	public static final ConnectedGlassPaneBlock FRAMED_GLASS_PANE =
		framedGlassPane("framed_glass", () -> FRAMED_GLASS, AllSpriteShifts.FRAMED_GLASS),
		HORIZONTAL_FRAMED_GLASS_PANE = framedGlassPane("horizontal_framed_glass", () -> HORIZONTAL_FRAMED_GLASS,
			AllSpriteShifts.HORIZONTAL_FRAMED_GLASS),
		VERTICAL_FRAMED_GLASS_PANE =
			framedGlassPane("vertical_framed_glass", () -> VERTICAL_FRAMED_GLASS, AllSpriteShifts.VERTICAL_FRAMED_GLASS);

	public static final WindowBlock OAK_WINDOW = woodenWindowBlock(SignType.OAK, Blocks.OAK_PLANKS),
		SPRUCE_WINDOW = woodenWindowBlock(SignType.SPRUCE, Blocks.SPRUCE_PLANKS),
		BIRCH_WINDOW = woodenWindowBlock(SignType.BIRCH, Blocks.BIRCH_PLANKS, () -> RenderLayer::getTranslucent),
		JUNGLE_WINDOW = woodenWindowBlock(SignType.JUNGLE, Blocks.JUNGLE_PLANKS),
		ACACIA_WINDOW = woodenWindowBlock(SignType.ACACIA, Blocks.ACACIA_PLANKS),
		DARK_OAK_WINDOW = woodenWindowBlock(SignType.DARK_OAK, Blocks.DARK_OAK_PLANKS),
		CRIMSON_WINDOW = woodenWindowBlock(SignType.CRIMSON, Blocks.CRIMSON_PLANKS),
		WARPED_WINDOW = woodenWindowBlock(SignType.WARPED, Blocks.WARPED_PLANKS),
		ORNATE_IRON_WINDOW = customWindowBlock("ornate_iron_window", () -> AllItems.ANDESITE_ALLOY,
			AllSpriteShifts.ORNATE_IRON_WINDOW, () -> RenderLayer::getCutoutMipped);

	public static final ConnectedGlassPaneBlock OAK_WINDOW_PANE =
		woodenWindowPane(SignType.OAK, () -> OAK_WINDOW),
		SPRUCE_WINDOW_PANE = woodenWindowPane(SignType.SPRUCE, () -> SPRUCE_WINDOW),
		BIRCH_WINDOW_PANE = woodenWindowPane(SignType.BIRCH, () -> BIRCH_WINDOW, () -> RenderLayer::getTranslucent),
		JUNGLE_WINDOW_PANE = woodenWindowPane(SignType.JUNGLE, () -> JUNGLE_WINDOW),
		ACACIA_WINDOW_PANE = woodenWindowPane(SignType.ACACIA, () -> ACACIA_WINDOW),
		DARK_OAK_WINDOW_PANE = woodenWindowPane(SignType.DARK_OAK, () -> DARK_OAK_WINDOW),
		CRIMSON_WINDOW_PANE = woodenWindowPane(SignType.CRIMSON, () -> CRIMSON_WINDOW),
		WARPED_WINDOW_PANE = woodenWindowPane(SignType.WARPED, () -> WARPED_WINDOW),
		ORNATE_IRON_WINDOW_PANE = customWindowPane("ornate_iron_window", () -> ORNATE_IRON_WINDOW,
			AllSpriteShifts.ORNATE_IRON_WINDOW, () -> RenderLayer::getCutoutMipped);

	// Vanilla stone variant patterns

	public static final PalettesVariantEntry GRANITE_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.GRANITE, PaletteBlockPatterns.vanillaRange, () -> Blocks.GRANITE);

	public static final PalettesVariantEntry DIORITE_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.DIORITE, PaletteBlockPatterns.vanillaRange, () -> Blocks.DIORITE);

	public static final PalettesVariantEntry ANDESITE_VARIANTS = new PalettesVariantEntry(PaletteStoneVariants.ANDESITE,
		PaletteBlockPatterns.vanillaRange, () -> Blocks.ANDESITE);

	// Create stone variants

	public static final SandBlock LIMESAND = createBuilder("limesand", p -> new SandBlock(0xD7D7C7, p))
		.initialProperties(() -> Blocks.SAND)
//		.blockstate(palettesCubeAll())
		.simpleItem()
		.register();

	public static final Block LIMESTONE =
		createBaseBuilder("limestone", Block::new, () -> Blocks.SANDSTONE, true)
			.register();

	public static final PalettesVariantEntry LIMESTONE_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.LIMESTONE, PaletteBlockPatterns.standardRange, () -> LIMESTONE);

	public static final Block WEATHERED_LIMESTONE =
		createBaseBuilder("weathered_limestone", Block::new, () -> Blocks.SANDSTONE, true)
			.register();

	public static final PalettesVariantEntry WEATHERED_LIMESTONE_VARIANTS = new PalettesVariantEntry(
		PaletteStoneVariants.WEATHERED_LIMESTONE, PaletteBlockPatterns.standardRange, () -> WEATHERED_LIMESTONE);

	public static final Block DOLOMITE =
		createBaseBuilder("dolomite", Block::new, () -> Blocks.QUARTZ_BLOCK, true)
			.register();

	public static final PalettesVariantEntry DOLOMITE_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.DOLOMITE, PaletteBlockPatterns.standardRange, () -> DOLOMITE);

	public static final Block GABBRO =
		createBaseBuilder("gabbro", Block::new, () -> Blocks.ANDESITE, true)
			.register();

	public static final PalettesVariantEntry GABBRO_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.GABBRO, PaletteBlockPatterns.standardRange, () -> GABBRO);

	public static final Block SCORIA =
		createBaseBuilder("scoria", Block::new, () -> Blocks.ANDESITE, true)
			.register();

	public static final Block NATURAL_SCORIA = createBuilder("natural_scoria", Block::new)
		.initialProperties(() -> Blocks.ANDESITE)
		.onRegister(AllBlocks.blockVertexColors(new ScoriaVertexColor()))
//		.loot((p, g) -> p.addDrop(g, RegistrateBlockLootTables.drops(g, SCORIA.get())))
//		.blockstate(palettesCubeAll())
		.simpleItem()
		.register();

	public static final PalettesVariantEntry SCORIA_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.SCORIA, PaletteBlockPatterns.standardRange, () -> SCORIA);

	public static final Block DARK_SCORIA =
		createBaseBuilder("dark_scoria", Block::new, () -> Blocks.ANDESITE, false)
			.register();

	public static final PalettesVariantEntry DARK_SCORIA_VARIANTS =
		new PalettesVariantEntry(PaletteStoneVariants.DARK_SCORIA, PaletteBlockPatterns.standardRange, () -> DARK_SCORIA);

	public static <T extends Block> BlockRegBuilder<T> createBuilder(String id, Function<FabricBlockSettings, T> function) {
		BlockRegBuilder<T> builder = BlockRegBuilder.create(new Identifier(Create.ID, id), function);
		builder.onRegister(block -> AllSections.addToSection(block, currentSection));
		builder.onRegisterItem(item -> AllSections.addToSection(item, currentSection));
		builder.beforeRegisterItem(builder1 -> builder1.properties(settings -> settings.group(itemGroup)));
		return builder;
	}

	private static <T extends Block> BlockRegBuilder<T> createBaseBuilder(String name,
		Function<FabricBlockSettings, T> factory, Supplier<Block> propertiesFrom, boolean TFworldGen) {
		return createBuilder(name, factory).initialProperties(propertiesFrom)
//			.blockstate((c, p) -> {
//				final String location = "block/palettes/" + c.getName() + "/plain";
//				p.simpleBlock(c.get(), p.models()
//					.cubeAll(c.getName(), p.modLoc(location)));
//				// TODO tag with forge:stone; if TFWorldGen == true tag with forge:wg_stone
//				// aswell
//			})
			.simpleItem();
	}

	public static void register() {}

//	private static <T extends Block> BiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> palettesCubeAll() {
//		return (c, p) -> BlockStateGen.cubeAll(c, p, "palettes/");
//	}
}
