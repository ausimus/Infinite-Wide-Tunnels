package org.ausimus.wurmunlimited.mods.infinitymines;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.*;
import com.wurmonline.server.behaviours.*;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.Zones;
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
        if (subject.isMineDoor() || subject.getTemplateId() == 315 || subject.getTemplateId() == 176) {
            int[] slist = Terraforming.getCaveOpeningCoords(tilex, tiley);
            if (isWideEntrance(slist[0], slist[1])) {
                return Collections.singletonList(actionEntryWT);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean action(Action act, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short action, float counter) {
        boolean done = true;
        if(action == WTID) {
            int[] spell = Terraforming.getCaveOpeningCoords(tilex, tiley);
            if (isWideEntrance(spell[0], spell[1])) {
                done = buildMineDoor(performer, source, act, spell[0], spell[1], onSurface, counter);
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
    private static boolean buildMineDoor(Creature performer, Item source, Action act, int tilex, int tiley, boolean onSurface, float counter) {
        boolean done = true;
        if(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_HOLE.id) {
            if(!onSurface) {
                performer.getCommunicator().sendNormalServerMessage("You need to do this from the outside.");
                return true;
            }

            if(performer.getPower() < 5) {
                if(Zones.isInPvPZone(tilex, tiley)) {
                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to build this in the PvP zone.");
                    return true;
                }

                if(getCaveDoorDifference(Server.surfaceMesh.getTile(tilex, tiley), tilex, tiley) > 90) {
                    performer.getCommunicator().sendNormalServerMessage("That hole is too big to be covered.");
                    return true;
                }

                if(isTileModBlocked(performer, tilex, tiley, true)) {
                    return true;
                }

                if(isAltarBlocking(performer, tilex, tiley)) {
                    performer.getCommunicator().sendSafeServerMessage("You cannot build here, since this is holy ground.");
                    return true;
                }

                if(!Methods.isActionAllowed(performer, (short) 363)) {
                    return true;
                }
            }

            if(source.isMineDoor() || source.isWand()) {
                done = false;
                boolean insta = performer.getPower() > 0;
                Skills skills;
                Skill mining;
                if(counter == 1.0F && !insta) {
                    skills = performer.getSkills();

                    try {
                        mining = skills.getSkill(1008);
                        if(source.getTemplateId() != 592 && mining.getRealKnowledge() <= 21.0D) {
                            performer.getCommunicator().sendNormalServerMessage("You do not know how to do that effectively.");
                            return true;
                        }
                    } catch (NoSuchSkillException var13) {
                        performer.getCommunicator().sendNormalServerMessage("You do not know how to do that effectively.");
                        return true;
                    }

                    performer.getCommunicator().sendNormalServerMessage("You start to fit the door in the entrance.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to fit a door in the entrance.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[363].getVerbString(), true, 150);
                    performer.getStatus().modifyStamina(-1000.0F);
                }

                if(act.currentSecond() % 5 == 0) {
                    performer.getStatus().modifyStamina(-5000.0F);
                }

                if(act.mayPlaySound()) {
                    String skills1 = Server.rand.nextInt(2) == 0?"sound.work.carpentry.mallet1":"sound.work.carpentry.mallet2";
                    if(source.isStone()) {
                        skills1 = "sound.work.masonry";
                    }

                    if(source.isMetal()) {
                        skills1 = "sound.work.smithing.metal";
                    }

                    SoundPlayer.playSound(skills1, performer, 1.0F);
                }

                if(counter > 15.0F || insta) {
                    skills = performer.getSkills();
                    try {
                        mining = skills.getSkill(1008);
                        mining.skillCheck(20.0D, (double)source.getQualityLevel(), false, 15.0F);
                    } catch (NoSuchSkillException ignored) {
                    }

                    if(MineDoorPermission.getPermission(tilex, tiley) != null) {
                        MineDoorPermission.deleteMineDoor(tilex, tiley);
                    }

                    Village vill = Villages.getVillage(tilex, tiley, true);
                    new MineDoorPermission(tilex, tiley, performer.getWurmId(), vill, false, false, "", 0);
                    Server.setWorldResource(tilex, tiley, Math.max(1, (int)source.getCurrentQualityLevel() * 100));
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), getNewTileTypeForMineDoor(source.getTemplateId()), (byte) 0);
                    TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
                    TileEvent.log(tilex, tiley, -1, performer.getWurmId(), act.getNumber());
                    Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                    if(source.isMineDoor()) {
                        Items.destroyItem(source.getWurmId());
                    }

                    return true;
                }
            }
        }

        return done;
    }
}