package com.harleylizard.chunked;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

    public long time;

    public QueuedComponent(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    @Override
    public void serverTick() {
        ticks++;
        if (ticks % 20 == 0 && !queue.isEmpty() && chunk instanceof LevelChunk levelChunk) {
            queue.poll().run((ServerLevel) levelChunk.getLevel(), levelChunk, time);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        time = tag.getLong("Time");
        queue.clear();
        var queued = tag.getList("Queued", Tag.TAG_COMPOUND);
        for (var i = 0; i < queued.size(); i++) {
            queue.offer(Queueable.of(queued.getCompound(i)));
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLong("Time", time);
        var queued = new ListTag();
        for (var queueable : queue) {
            var t = new CompoundTag();
            queueable.save(t);
            queued.add(t);
        }
        tag.put("Queued", queued);
    }

    public void all() {
        for (var task : Tasks.TASKS.values()) {
            if (has(task)) {
                continue;
            }
            queue.offer(Queueable.of(task));
        }
    }

    public boolean has(Task task) {
        for (var queueable : queue) {
            if (queueable.is(task)) {
                return true;
            }
        }
        return false;
    }
}
