package de.hglabor.youtuberideen.wichtiger;

import de.hglabor.youtuberideen.sasukey.LavaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int xChunk, int zChunk, ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = worldInfo.getMinHeight(); y <= LavaManager.defaultLavaLevel; y++) {
                    chunkData.setBlock(x, y, z, Material.LAVA);
                }
            }
        }
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int xChunk, int yChunk, BiomeGrid biome) {
        ChunkData data = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome.setBiome(x, z, Biome.PLAINS);
            }
        }
        return data;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.0, 128.0, 0.0);
    }
}