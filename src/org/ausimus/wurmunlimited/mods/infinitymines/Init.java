package org.ausimus.wurmunlimited.mods.infinitymines;

import javassist.*;
import javassist.bytecode.Descriptor;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;


public class Init implements WurmServerMod, PreInitable {
    @Override
    public void preInit() {
        try {
            CtClass[] parametersWT = {
                    HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.Creature"),
                    HookManager.getInstance().getClassPool().get("com.wurmonline.mesh.MeshIO"),
                    CtPrimitiveType.intType,
                    CtPrimitiveType.intType
            };
            CtClass ctClassWT = HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.TileRockBehaviour");
            CtMethod WideTunnels = ctClassWT.getMethod("hasValidNearbyEntrance", Descriptor.ofMethod(CtPrimitiveType.booleanType, parametersWT));
            /*
            Nasty setBody I like, "shouldn't" break anything.
            I have yet to figure out how to distinguish the start tile, so limited to infinite until then non configurable.
            */
            WideTunnels.setBody("{\n" +
                    "        int holeX = -1;\n" +
                    "        int holeY = -1;\n" +
                    "        int holeXX = -1;\n" +
                    "        int holeYY = -1;\n" +
                    "\n" +
                    "        int xxx;\n" +
                    "        int yyy;\n" +
                    "        int tileThree;\n" +
                    "        byte type;\n" +
                    "        for(xxx = -1; xxx <= 1; ++xxx) {\n" +
                    "            for(yyy = -1; yyy <= 1; ++yyy) {\n" +
                    "                if(xxx != 0 && yyy != 0) {\n" +
                    "                    tileThree = $2.getTile($3 + xxx, $4 + yyy);\n" +
                    "                    type = com.wurmonline.mesh.Tiles.decodeType(tileThree);\n" +
                    "                    if(type == com.wurmonline.mesh.Tiles.Tile.TILE_HOLE.id) {\n" +
                    "                        if($1 != null) {\n" +
                    "                            $1.getCommunicator().sendNormalServerMessage(\"Cannot have cave entrances meeting diagonally.\");\n" +
                    "                        }\n" +
                    "\n" +
                    "                        return false;\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        for(xxx = -1; xxx <= 1; ++xxx) {\n" +
                    "            for(yyy = -1; yyy <= 1; ++yyy) {\n" +
                    "                if(xxx != 0 || yyy != 0) {\n" +
                    "                    tileThree = $2.getTile($3 + xxx, $4 + yyy);\n" +
                    "                    type = com.wurmonline.mesh.Tiles.decodeType(tileThree);\n" +
                    "                    if(com.wurmonline.mesh.Tiles.isMineDoor(type)) {\n" +
                    "                        if($1 != null) {\n" +
                    "                            $1.getCommunicator().sendNormalServerMessage(\"Cannot make a tunnel next to a mine door.\");\n" +
                    "                        }\n" +
                    "\n" +
                    "                        return false;\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        for(xxx = -1; xxx <= 1; ++xxx) {\n" +
                    "            for(yyy = -1; yyy <= 1; ++yyy) {\n" +
                    "                if((xxx != 0 || yyy != 0) && (xxx == 0 || yyy == 0)) {\n" +
                    "                    tileThree = $2.getTile($3 + xxx, $4 + yyy);\n" +
                    "                        if(holeX != -1) {\n" +
                    "                            if($1 != null) {\n" +
                    "                                $1.getCommunicator().sendNormalServerMessage(\"Can only make two or three tile wide cave entrances .\");\n" +
                    "                            }\n" +
                    "\n" +
                    "                            return false;\n" +
                    "\n" +
                    "                        holeX = $3 + xxx;\n" +
                    "                        holeY = $4 + yyy;\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        if(holeX == -1) {\n" +
                    "            return true;\n" +
                    "        } else {\n" +
                    "            for(xxx = -1; xxx <= 1; ++xxx) {\n" +
                    "                for(yyy = -1; yyy <= 1; ++yyy) {\n" +
                    "                    if(xxx != 0 || yyy != 0) {\n" +
                    "                        tileThree = $2.getTile(holeX + xxx, holeY + yyy);\n" +
                    "                            if(holeXX != -1) {\n" +
                    "                                if($1 != null) {\n" +
                    "                                    $1.getCommunicator().sendNormalServerMessage(\"Can only make two or three tile wide cave entrances .\");\n" +
                    "\n" +
                    "                                return false;\n" +
                    "                            }\n" +
                    "\n" +
                    "                            holeXX = holeX + xxx;\n" +
                    "                            holeYY = holeY + yyy;\n" +
                    "                                if($1 != null) {\n" +
                    "                                    $1.getCommunicator().sendNormalServerMessage(\"Can only make two or three tile wide cave entrances .\");\n" +
                    "\n" +
                    "                                return false;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "\n" +
                    "            if(holeXX == -1) {\n" +
                    "                return true;\n" +
                    "            } else {\n" +
                    "                for(xxx = -1; xxx <= 1; ++xxx) {\n" +
                    "                    for(yyy = -1; yyy <= 1; ++yyy) {\n" +
                    "                        if(xxx != 0 || yyy != 0) {\n" +
                    "                            tileThree = $2.getTile(holeXX + xxx, holeYY + yyy);\n" +
                    "                                if($1 != null) {\n" +
                    "                                    $1.getCommunicator().sendNormalServerMessage(\"Can only make two or three tile wide cave entrances .\");\n" +
                    "\n" +
                    "                                return false;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                }\n" +
                    "\n" +
                    "                return true;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }");
        } catch (CannotCompileException | NotFoundException ex) {
            throw new HookException(ex);
        }
    }
}
