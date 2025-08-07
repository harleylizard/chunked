package com.harleylizard.chunked;

import com.harleylizard.chunked.action.Action;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;

public final class Chunked implements ModInitializer, ChunkComponentInitializer {
    private static final String MOD_ID = "chunked";

    public static final ComponentKey<QueuedComponent> QUEUED = ComponentRegistryV3.INSTANCE.getOrCreate(ResourceLocation.fromNamespaceAndPath(MOD_ID, "queued"), QueuedComponent.class);

    @Override
    public void onInitialize() {
        Action.CODEC.listOf();

        ServerWorldEvents.LOAD.register((server, level) -> {

        });
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(QUEUED, QueuedComponent::new);
    }

}
