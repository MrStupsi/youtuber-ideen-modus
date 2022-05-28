package de.hglabor.youtuberideen.wichtiger;

import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.commands.CommandSetBlock;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SchematicReader {
    public static Map<Vector, IBlockData> parseSchematic(InputStream inputStream, boolean ignoreAir) {
        Map<Vector, IBlockData> map = new HashMap<>();
        try {
            Map<Byte, IBlockData> blockPalette = new HashMap<>();
            NBTTagCompound nbt = NBTCompressedStreamTools.a(inputStream);
            int width = nbt.h("Width");
            int height = nbt.h("Height");
            int length = nbt.h("Length");
            byte[] blocks = nbt.m("BlockData");
            NBTTagCompound palette = nbt.p("Palette");
            palette.d().forEach((key) -> {
                try {
                    IBlockData state = ArgumentTile.a().parse(new StringReader(key)).a();
                    if (state == null) return;
                    blockPalette.put(palette.f(key), state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            for (int i = 0; i < blocks.length; i++) {
                IBlockData state = blockPalette.get(blocks[i]);
                if (state == null) continue;
                if (ignoreAir && state.g()) continue;
                int x = i % (width * length) % width;
                int y = i / (width * length);
                int z = i % (width * length) / width;
                map.put(new Vector(x, y, z), state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
