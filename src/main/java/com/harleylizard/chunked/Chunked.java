package com.harleylizard.chunked;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.harleylizard.chunked.action.Action;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class Chunked implements ModInitializer, ChunkComponentInitializer {
    private static final String MOD_ID = "chunked";

    public static final ComponentKey<QueuedComponent> QUEUED = ComponentRegistryV3.INSTANCE.getOrCreate(ResourceLocation.fromNamespaceAndPath(MOD_ID, "queued"), QueuedComponent.class);

    @Override
    public void onInitialize() {
        Action.CODEC.listOf();

        var logger = LoggerFactory.getLogger("chunked");
        ServerWorldEvents.LOAD.register((server, level) -> {
            try {
                var gson = new GsonBuilder().create();

                Tasks.TASKS.clear();
                for (var entry : server.getResourceManager().listResources("task", path -> path.toString().endsWith(".json")).entrySet()) {
                    var key = entry.getKey().withPath(path -> path.substring(0, path.indexOf(".json")).substring(path.lastIndexOf("/") + 1));

                    try (var reader = entry.getValue().openAsReader()) {
                        var result = Task.CODEC.parse(JsonOps.INSTANCE, gson.fromJson(reader, JsonElement.class));

                        if (result.isError()) {
                            logger.error(result.error().orElseThrow().message());

                            continue;
                        }

                        Tasks.TASKS.put(key, result.getOrThrow());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> chunk.getComponent(QUEUED).time = level.dayTime());

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) -> chunk.getComponent(QUEUED).all());
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(QUEUED, QueuedComponent::new);
    }

}
