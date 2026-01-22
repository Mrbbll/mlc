package com.mlc.mlc.respacksender.Listener;

import com.mlc.mlc.respacksender.packsender;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

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
