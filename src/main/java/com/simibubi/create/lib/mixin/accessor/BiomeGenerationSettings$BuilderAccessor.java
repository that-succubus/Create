package com.simibubi.create.lib.mixin.accessor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeGenerationSettings.Builder.class)
public interface BiomeGenerationSettings$BuilderAccessor {
	@Accessor("surfaceBuilder")
	Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder();

	@Accessor("surfaceBuilder")
	void setSurfaceBuilder(Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder);

	@Accessor("carvers")
	Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> getCarvers();

	@Accessor("carvers")
	void setCarvers(Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers);

	@Accessor("features")
	List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures();

	@Accessor("features")
	void setFeatures(List<List<Supplier<ConfiguredFeature<?, ?>>>> features);

	@Accessor("structureStarts")
	List<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures();

	@Accessor("structureStarts")
	void setStructureFeatures(List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures);
}