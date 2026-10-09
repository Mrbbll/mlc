package com.mlc.mlc;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;

/** Registers data-driven content before the JavaPlugin instance is created. */
public final class MlcBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        BootStrapTask.run(context);
    }
}
