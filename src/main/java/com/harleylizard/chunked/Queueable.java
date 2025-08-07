package com.harleylizard.chunked;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

    public void run(ServerLevel level, long time) {
        var seconds = (int) (level.getDayTime() - time) / 20;
        if (seconds == 0) {
            return;
        }

    }

    public void save(CompoundTag tag) {
        tag.putString("Task", Tasks.TASKS.inverse().get(task).toString());
    }

    public static Queueable of(Task task) {
        return new Queueable(task);
    }

    public static Queueable of(CompoundTag tag) {
        return new Queueable(Tasks.TASKS.get(ResourceLocation.parse(tag.getString("Task"))));
    }
}
