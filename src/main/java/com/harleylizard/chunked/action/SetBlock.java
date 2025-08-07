package com.harleylizard.chunked.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SetBlock(BlockPredicate predicate, BlockStateProvider block) implements Action {
    public static final MapCodec<SetBlock> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(BlockPredicate.CODEC.fieldOf("predicate").orElse(BlockPredicate.alwaysTrue()).forGetter(SetBlock::predicate), BlockStateProvider.CODEC.fieldOf("block").forGetter(SetBlock::block)).apply(builder, SetBlock::new));

    @Override
    public void run(ServerLevel level, BlockPos blockPos, RandomSource random) {
        var chunk = level.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk != null && predicate.test(level, blockPos)) {

            level.setBlock(blockPos, block.getState(random, blockPos), Block.UPDATE_ALL);
        }
    }

    @Override
    public MapCodec<? extends Action> getCodec() {
        return CODEC;
    }
}
