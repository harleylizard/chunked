package com.harleylizard.chunked;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

public final class Queueable implements Comparable<Queueable> {
    private final Task task;

    private Queueable(Task task) {
        this.task = task;
    }

    @Override
    public int compareTo(@NotNull Queueable o) {
        return task.compareTo(o.task);
    }

    public void run(ServerLevel level, LevelChunk chunk, long time) {
        var seconds = (int) (level.getDayTime() - time) / 20;
        if (seconds <= 0) {
            return;
        }
        var random = level.getRandom();
        var sections = chunk.getSections();

        var blockPos = new BlockPos.MutableBlockPos();
        var pos = chunk.getPos();

        var block = task.block();

        for (var i = chunk.getMinSection(); i < chunk.getMaxSection(); i++) {
            var section = sections[chunk.getSectionIndexFromSectionY(i)];

            if (section.getStates().maybeHas(blockState -> blockState.is(block))) {
                for (var j = 0; j < 16 * 16 * 16; j++) {
                    var x = (j % 16) + (pos.x << 4);
                    var y = ((j / 16) % 16) + (i << 4);
                    var z = (j / (16 * 16)) + (pos.z << 4);

                    var probability = (task.probability() * (16 * 16 * 16)) / seconds;
                    if (random.nextInt(probability + 1) == 0 && chunk.getBlockState(blockPos.set(x, y, z)).is(block)) {
                        task.action().run(level, blockPos, random);
                    }
                }
            }
        }
    }

    public void save(CompoundTag tag) {
        var tasks = Tasks.TASKS;
        tag.putString("Task", tasks.inverse().get(task).toString());
    }

    public boolean is(Task task) {
        return this.task == task;
    }

    public static Queueable of(Task task) {
        return new Queueable(task);
    }

    public static Queueable of(CompoundTag tag) {
        return new Queueable(Tasks.TASKS.get(ResourceLocation.parse(tag.getString("Task"))));
    }
}
