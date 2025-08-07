package com.harleylizard.chunked;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public final class Queueable implements Comparable<Queueable> {

    public void run(ServerLevel level, long time) {
        var seconds = (int) (level.getDayTime() - time) / 20;
        if (seconds == 0) {
            return;
        }


    }

    @Override
    public int compareTo(@NotNull Queueable o) {
        return 0;
    }

}
