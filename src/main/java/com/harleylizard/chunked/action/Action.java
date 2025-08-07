package com.harleylizard.chunked.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public sealed interface Action permits SetBlock {
    Codec<Action> CODEC = Codec.STRING.dispatch(action -> action.getCodec() == SetBlock.CODEC ? "set_block" : null, name -> name.equals("set_block") ? SetBlock.CODEC : null);

    void run(ServerLevel level, BlockPos blockPos, RandomSource random);

    MapCodec<? extends Action> getCodec();
}
