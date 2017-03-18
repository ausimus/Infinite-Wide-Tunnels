package org.ausimus.wurmunlimited.mods.infinitymines;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.*;
import com.wurmonline.server.behaviours.*;
import com.wurmonline.server.items.*;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import com.wurmonline.server.creatures.Creature;

import java.util.Collections;
import java.util.List;

import static com.wurmonline.server.behaviours.Terraforming.*;

public class AttachWideDoor implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer {

    private static short WTID;
    private static ActionEntry actionEntryWT;

    AttachWideDoor() {
        WTID = (short) ModActions.getNextActionId();
        actionEntryWT = ActionEntry.createEntry(WTID, "Build Mine Door", "", new int[]{
        });
        ModActions.registerAction(actionEntryWT);
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return this;
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return this;
    }

    @Override
    public short getActionId() {
        return WTID;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, boolean border, int heightOffset) {
        int templateId = subject.getTemplateId();
        if (subject.isMineDoor() || templateId == 315 || templateId == 176) {
            int[] slist = Terraforming.getCaveOpeningCoords(tilex, tiley);
            if (slist[0] != -1 && slist[1] != -1 && isWideEntrance(slist[0], slist[1])) {
                return Collections.singletonList(actionEntryWT);
            }
        }
        return null;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short action, float counter) {
        boolean done = true;
        int[] slist = Terraforming.getCaveOpeningCoords(tilex, tiley);
        if(action == WTID) {
            if (slist[0] != -1 && slist[1] != -1 && isWideEntrance(slist[0], slist[1])) {
                done = buildMineDoor(performer, source, act, slist[0], slist[1], onSurface, counter);
            }
        }
        return done;
    }
    private static boolean isWideEntrance(int tilex, int tiley) {
        MeshTile mTileCurrent = new MeshTile(Server.surfaceMesh, tilex, tiley);
        if(mTileCurrent.isHole()) {
            MeshTile mTileNorth = mTileCurrent.getNorthMeshTile();
            if(mTileNorth.isHole()) {
                return true;
            }

            MeshTile mTileWest = mTileCurrent.getWestMeshTile();
            if(mTileWest.isHole()) {
                return true;
            }

            MeshTile mTileSouth = mTileCurrent.getSouthMeshTile();
            if(mTileSouth.isHole()) {
                return true;
            }

            MeshTile mTileEast = mTileCurrent.getEastMeshTile();
            if(mTileEast.isHole()) {
                return true;
            }
        }
        return false;
    }
}