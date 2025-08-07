package com.harleylizard.chunked;


import com.harleylizard.chunked.action.Action;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record Task(Block block, Action action, int priority) implements Comparable<Task> {
    public static final Codec<Task> CODEC = RecordCodecBuilder.create(builder -> builder.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Task::block), Action.CODEC.fieldOf("action").forGetter(Task::action), Codec.INT.fieldOf("priority").orElse(0).forGetter(Task::priority)).apply(builder, Task::new));

    @Override
    public int compareTo(@NotNull Task o) {
        return Integer.compare(priority, o.priority);
    }
}
