package de.hglabor.youtuberideen.veto;

import de.hglabor.youtuberideen.YoutuberIdeen;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityIronGolem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.purpurmc.purpur.controller.FlyingWithSpacebarMoveControllerWASD;

import java.util.Arrays;

public class FlyingIronGolem extends EntityIronGolem {
    public FlyingIronGolem(World w) {
        super(EntityTypes.P, ((CraftWorld) w).getHandle());
        //es wird immer geiler
        try {
            this.bN = (FlyingWithSpacebarMoveControllerWASD) Arrays.stream(Class.forName("org.purpurmc.purpur.controller.FlyingWithSpacebarMoveControllerWASD")
                .getConstructors()).filter((x) -> x.getParameterCount() == 1).findFirst().get()
                .newInstance(this);
        } catch (Exception e) {
            YoutuberIdeen.INSTANCE.getLogger().warning("Du benötigst Purpur als Server.jar");
        }
    }

    public FlyingIronGolem spawnAt(Location location) {
        s.b(this);
        getBukkitEntity().teleport(location);
        return this;
    }
}
