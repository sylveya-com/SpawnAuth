package me.lokspel.spawnauth.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class VoidWorldGenerator extends ChunkGenerator {

    private static final byte LEGACY_BARRIER_ID = (byte) 166;

    private final int spawnX;
    private final int spawnZ;
    private final int platformY;
    private final int platformRadius;

    public VoidWorldGenerator(
            int spawnX,
            int platformY,
            int spawnZ,
            int platformRadius
    ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
        this.platformY = platformY;
        this.platformRadius = Math.max(0, platformRadius);
    }

    @Override
    public void generateSurface(
            @NotNull WorldInfo worldInfo,
            @NotNull Random random,
            int chunkX,
            int chunkZ,
            @NotNull ChunkData chunkData
    ) {
        if (platformY < chunkData.getMinHeight()
                || platformY >= chunkData.getMaxHeight()) {
            return;
        }

        buildPlatform(chunkData, chunkX, chunkZ);
    }

    // CraftBukkit 1.16.x calls ChunkGenerator#generateChunkData through
    // CustomChunkGenerator.buildBase; without this override it throws
    // UnsupportedOperationException and hangs the chunk pipeline.
    // Modern servers use generateSurface instead.
    @Deprecated
    public ChunkData generateChunkData(
            World world,
            Random random,
            int chunkX,
            int chunkZ,
            BiomeGrid biomes
    ) {
        if (platformY < 0 || platformY > world.getMaxHeight()) {
            return null;
        }

        ChunkData chunkData = createChunkData(world);
        buildPlatform(chunkData, chunkX, chunkZ);
        return chunkData;
    }

    private void buildPlatform(ChunkData chunkData, int chunkX, int chunkZ) {
        // Intersect the platform square (centered on the spawn point) with the current chunk.
        int minX = Math.max(spawnX - platformRadius, chunkX << 4);
        int maxX = Math.min(spawnX + platformRadius, (chunkX << 4) + 15);
        int minZ = Math.max(spawnZ - platformRadius, chunkZ << 4);
        int maxZ = Math.min(spawnZ + platformRadius, (chunkZ << 4) + 15);

        if (minX > maxX || minZ > maxZ) {
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                chunkData.setBlock(
                        x & 15,
                        platformY,
                        z & 15,
                        Material.BARRIER
                );
            }
        }
    }

    @Deprecated
    public byte[][] generateBlockSections(
            World world,
            Random random,
            int chunkX,
            int chunkZ,
            BiomeGrid biomes
    ) {
        if (platformY < 0 || platformY > 255) {
            return null;
        }

        byte[][] sections = new byte[16][];

        int minX = Math.max(spawnX - platformRadius, chunkX << 4);
        int maxX = Math.min(spawnX + platformRadius, (chunkX << 4) + 15);
        int minZ = Math.max(spawnZ - platformRadius, chunkZ << 4);
        int maxZ = Math.min(spawnZ + platformRadius, (chunkZ << 4) + 15);

        if (minX > maxX || minZ > maxZ) {
            return sections;
        }

        int sectionY = platformY >> 4;
        int localY = platformY & 15;

        sections[sectionY] = new byte[4096];

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int localX = x & 15;
                int localZ = z & 15;

                int index = (localY << 8) | (localZ << 4) | localX;

                sections[sectionY][index] = LEGACY_BARRIER_ID;
            }
        }

        return sections;
    }

    @Deprecated
    public short[][] generateExtBlockSections(
            World world,
            Random random,
            int chunkX,
            int chunkZ,
            BiomeGrid biomes
    ) {
        return null;
    }
}