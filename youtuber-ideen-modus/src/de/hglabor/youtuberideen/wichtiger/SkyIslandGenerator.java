package de.hglabor.youtuberideen.wichtiger;

import de.hglabor.youtuberideen.Shuffler;
import de.hglabor.youtuberideen.YoutuberIdeen;
import de.hglabor.youtuberideen.game.mechanic.LootTables;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class SkyIslandGenerator {
    public interface Workload {
        void execute();
    }

    public static World world;
    private static File schematicFolder = new File(new File(new File("plugins"), "youtuberideen"), "schematics");
    private static HashMap<String, Map<Vector, IBlockData>> schematics = new HashMap<>();
    public static List<Workload> placeableBlocks = new ArrayList<>();
    private static List<Location> islandLocations = new ArrayList<>();
    private static long MAX_MS_PER_TICK = 10L;
    private static long ISLAND_DISTANCE = 100;
    public static List<Vector> spawnLocations = new ArrayList<>();
    public static int MIN_Y_ISLAND = 100;

    public SkyIslandGenerator() {
        schematicFolder.mkdirs();
        loadIslandSchematics();
        world = new WorldCreator("world_pvp").generator(new VoidGenerator()).createWorld();
        world.getWorldBorder().setSize(800 * 2.0);
        world.getWorldBorder().setSize(80 * 2.0, 1200);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        populate();
        AtomicBoolean placedIslands = new AtomicBoolean(false);
        Bukkit.getScheduler().runTaskTimer(YoutuberIdeen.INSTANCE, () -> {
            Random r = new Random();
            long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
            while (!placeableBlocks.isEmpty() && System.currentTimeMillis() < stopTime) {
                Workload workload = placeableBlocks.size() >= 1 ? placeableBlocks.remove(0) : null;
                if (workload == null) continue;
                workload.execute();
                if (workload instanceof PlaceableBlock) {
                    PlaceableBlock pb = (PlaceableBlock) workload;
                    if (pb.state.getBukkitMaterial() == Material.BEDROCK) {
                        spawnLocations.add(pb.loc);
                    } else if (pb.state.getBukkitMaterial() == Material.CHEST) {
                        Block block = world.getBlockAt(pb.loc.toLocation(world));
                        Chest chest = (Chest) block.getState();
                        for (int i = 0; i < 27; i++) {
                            if (r.nextInt(100) + 1 >= 20) {
                                int probability = r.nextInt(100) + 1;
                                Stream<LootTables.LootItem> items = LootTables.normal.stream().filter((x) -> x.probability + 10 >= probability);
                                Optional<LootTables.LootItem> opt = items.sorted(new Shuffler<>()).findFirst();
                                if (!opt.isPresent()) continue;
                                LootTables.LootItem lootItem = opt.get();
                                ItemStack item = lootItem.itemStack;
                                item.setAmount(lootItem.maxAmount == lootItem.minAmount ? lootItem.minAmount : r.nextInt(lootItem.maxAmount - lootItem.minAmount) + lootItem.minAmount);
                                chest.getInventory().setItem(i, item);
                            }
                        }
                    }
                }
                if (!placedIslands.get() && placeableBlocks.isEmpty()) {
                    placedIslands.set(false);
                    YoutuberIdeen.INSTANCE.getLogger().info("Alle Inseln wurden platziert");
                }
            }
            System.out.println(placeableBlocks.size());
            System.gc();
        }, 0, 1);
    }

    private void populate() {
        int size = (int) (world.getWorldBorder().getSize() / 2);
        Random r = new Random();
        int j = (r.nextInt() % ((75 * size) / 100 - (50 * size) / 100)) + (50 * size) / 100;
        for (int i = 0; i < j; i++) {
            Location pasteLoc = getSafeArenaSpawn();
            Map<Vector, IBlockData> schematic = r.nextBoolean() ? null : (Map<Vector, IBlockData>) schematics.values().toArray()[r.nextInt(schematics.size())];
            if (schematic == null) continue;
            if (!world.getWorldBorder().isInside(pasteLoc)) continue;
            islandLocations.add(pasteLoc);
            schematic.forEach((loc, state) -> {
                Location location = pasteLoc.clone().add(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                placeableBlocks.add(new PlaceableBlock(world, location.toVector(), state, 2));
            });
        }
    }

    private void loadIslandSchematics() {
        for (File f : schematicFolder.listFiles((dir, name) -> name.endsWith(".schem"))) {
            try {
                schematics.put(f.getName(), SchematicReader.parseSchematic(new FileInputStream(f), true));
                YoutuberIdeen.INSTANCE.getLogger().info(f.getName() + " wurde geladen");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Location getSafeArenaSpawn() {
        int size = (int) world.getWorldBorder().getSize();
        Random r = new Random();
        int x = r.nextInt(size) - size / 2;
        int z = r.nextInt(size) - size / 2;
        Location location = new Location(world, x, r.nextInt(MIN_Y_ISLAND, 200), z);
        Stream<Location> locs = islandLocations.stream().filter((loc) -> loc.distanceSquared(location) < ISLAND_DISTANCE);
        return locs.findAny().isPresent() ? getSafeArenaSpawn() : location;
    }

    public static class PlaceableBlock implements Workload {
        public World world;
        public Vector loc;
        public IBlockData state;
        int method;

        public PlaceableBlock(World world, Vector loc, IBlockData state, int method) {
            this.world = world;
            this.loc = loc;
            this.state = state;
            this.method = method;
        }

        private void setBlockInNativeChunk(World world, boolean applyPhysics) {
            WorldServer nmsWorld = ((CraftWorld) world).getHandle();
            Chunk chunk = nmsWorld.d(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
            BlockPosition bp = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            chunk.a(bp, state, applyPhysics);
        }

        private void setBlockInNativeWorld(World world, boolean applyPhysics) {
            WorldServer nmsWorld = ((CraftWorld) world).getHandle();
            BlockPosition bp = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            nmsWorld.a(bp, state, applyPhysics ? 3 : 2);
        }

        @Override
        public void execute() {
            switch (method) {
                case 1: setBlockInNativeChunk(world, false);
                case 2: setBlockInNativeWorld(world, false);
            }
        }
    }
}