package com.mlc.mlc.mlcmain.respacksender.Listener;

import com.mlc.mlc.mlcmain.respacksender.packsender;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import static com.mlc.mlc.Mlc.instance;

public class Joinlistener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerconnect(AsyncPlayerConnectionConfigureEvent event){
        new BukkitRunnable(){
            @Override
            public void run() {
                packsender.send(event.getConnection());
            }
        }.runTaskLater(instance,0);

    }
}
