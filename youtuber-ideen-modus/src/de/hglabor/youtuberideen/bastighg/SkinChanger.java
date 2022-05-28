package de.hglabor.youtuberideen.bastighg;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.hglabor.youtuberideen.YoutuberIdeen;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.EnumGamemode;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

public class SkinChanger {
    private static Map<UUID, JSONObject> skins = Map.of();

    public static Listener playerJoinEvent = new Listener() {
        @EventHandler
        public void event(PlayerJoinEvent e) {
            if (skins.containsKey(e.getPlayer().getUniqueId())) {
                JSONObject textureObject = skins.get(e.getPlayer().getUniqueId());
                changeSkin(e.getPlayer(), (String) textureObject.get("value"), (String) textureObject.get("signature"));
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(
                        YoutuberIdeen.INSTANCE,
                        () -> downloadAndChangeSkin(e.getPlayer(), e.getPlayer().getName())
                );
            }
        }
    };

    //jajajja blabla async future dings das chillt mal
    private static void downloadAndChangeSkin(Player player, String name) {
        try {
            BufferedImage image = ImageIO.read(new URL("https://mineskin.eu/download/" + name));
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(image, image);
            JSONObject uploadedSkin = mineskinUpload(image);
            JSONObject textureObject = (JSONObject) ((JSONObject) uploadedSkin.get("data")).get("texture");
            skins.put(player.getUniqueId(), textureObject);
            changeSkin(player, (String) textureObject.get("value"), (String) textureObject.get("signature"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void changeSkin(Player p, String value, String signature) {
        Location loc = p.getLocation();
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        GameProfile profile = ep.fq();
        WorldServer world = ((CraftWorld) p.getWorld()).getHandle();
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", new Property("textures", value, signature));
        ep.b.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, ep));
        ep.b.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, ep));
        ep.b.a(
            new PacketPlayOutRespawn(
                Holder.a(world.q_()),
                world.aa(),
                world.D(),
                ep.d.b(),
                ep.d.b(),
            false,
            false,
            true
            )
        );
        p.updateInventory();
        p.teleport(loc);
    }

    //danke an https://www.spigotmc.org/threads/small-util-class-for-uploading-skins-to-mineskin.406061/
    private static JSONObject mineskinUpload(BufferedImage skinImage) {
        try {
            return MineskinUtil.mineskinUpload(skinImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}