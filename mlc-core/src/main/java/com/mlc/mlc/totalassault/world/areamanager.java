package com.mlc.mlc.totalassault.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.mlc.mlc.Mlc.instance;


public class areamanager {
    //读取配置中总力战区域，克隆到每个玩家的世界
    public static void pasteSchematic(
            World world,
            String schematicFilename,
            int x,
            int y,
            int z
    ) throws IOException {


        File schematicFile = new File(
                instance.getDataFolder(),
                "totalassault/schematics/"+ schematicFilename+  ".schem"
        );


        if (!schematicFile.isFile()) {
            throw new IOException("场地原理图不存在：" + schematicFile.getAbsolutePath());
        }

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

        if (format == null) {
            throw new IOException("无法识别场地原理图格式：" + schematicFile.getName());
        }

        Clipboard clipboard;

        try (InputStream input = new FileInputStream(schematicFile);
             ClipboardReader reader = format.getReader(input)) {
            clipboard = reader.read();
        }

        com.sk89q.worldedit.world.World editWorld = BukkitAdapter.adapt(world);

        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(editWorld)
                .build()) {

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
        }
    }

}
