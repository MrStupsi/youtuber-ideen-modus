package de.hglabor.youtuberideen.veto;

import de.hglabor.youtuberideen.YoutuberIdeen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

//Buuuutz digga
public class GolemManager {
    public static Listener joinEvent = new Listener() {
        @EventHandler
        public void event(PlayerJoinEvent e) {
            //Purpur hahahahah YOOOO
            e.getPlayer().addAttachment(YoutuberIdeen.INSTANCE, "allow.ride.iron_golem", true);
        }
    };
}
