package net.tropicraft.core.common.dimension.feature.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;
import net.tropicraft.Constants;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of SingleJigsawPiece that properly uses structure void
 */
public class FixedSingleJigsawPiece extends SingleJigsawPiece {
    public static final Codec<FixedSingleJigsawPiece> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(templateCodec(), processorsCodec(), projectionCodec())
                .apply(instance, FixedSingleJigsawPiece::new);
    });

    private static final IJigsawDeserializer<FixedSingleJigsawPiece> TYPE = IJigsawDeserializer.register(Constants.MODID + ":fixed", CODEC);

    public FixedSingleJigsawPiece(Either<ResourceLocation, Template> template, Supplier<StructureProcessorList> processors, JigsawPattern.PlacementBehaviour placementBehaviour) {
        super(template, processors, placementBehaviour);
    }

    public FixedSingleJigsawPiece(Template template) {
        super(template);
    }

    public static Function<JigsawPattern.PlacementBehaviour, FixedSingleJigsawPiece> create(String id, StructureProcessorList processors) {
        return placementBehaviour -> new FixedSingleJigsawPiece(Either.left(new ResourceLocation(id)), () -> processors, placementBehaviour);
    }

    @Override
    public IJigsawDeserializer<?> getType() {
        return TYPE;
    }

    @Override
    protected PlacementSettings getSettings(Rotation rotation, MutableBoundingBox box, boolean b) {
        PlacementSettings settings = super.getSettings(rotation, box, b);
        settings.popProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
        settings.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        return settings;
    }
}
