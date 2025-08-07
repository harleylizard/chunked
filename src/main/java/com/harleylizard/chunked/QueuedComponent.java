package com.harleylizard.chunked;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.PriorityQueue;
import java.util.Queue;

public final class QueuedComponent implements ComponentV3, ServerTickingComponent {
    private final Queue<Queueable> queue = new PriorityQueue<>();

    private final ChunkAccess chunk;

    private int ticks;

    private long time;

    public QueuedComponent(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    @Override
    public void serverTick() {
        ticks++;
        if (ticks % 20 == 0 && !queue.isEmpty() && chunk instanceof LevelChunk levelChunk) {
            queue.poll().run((ServerLevel) levelChunk.getLevel(), time);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        time = tag.getLong("Time");

    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLong("Time", time);

    }

}
