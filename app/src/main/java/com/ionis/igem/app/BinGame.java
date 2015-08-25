package com.ionis.igem.app;

import android.hardware.SensorManager;
import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ionis.igem.app.game.bins.Bin;
import com.ionis.igem.app.game.bins.Item;
import com.ionis.igem.app.game.managers.ResMan;
import com.ionis.igem.app.game.model.BaseGame;
import com.ionis.igem.app.game.model.HUDElement;
import com.ionis.igem.app.game.model.res.FontAsset;
import com.ionis.igem.app.game.model.res.GFXAsset;
import com.ionis.igem.app.ui.GameActivity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.*;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by PLN on 21/08/2015.
 */
public class BinGame extends BaseGame {

    private ArrayList<Item> items = new ArrayList<>();

    private static final String TAG = "BinGame";

    private int gameScore = 0;
    private int gameLives = 3;

    private HUDElement HUDScore;
    private HUDElement HUDLives;

    public BinGame(GameActivity pActivity) {
        super(pActivity);
        activity = pActivity;
    }

    @Override
    public List<GFXAsset> getGraphicalAssets() {
        if (graphicalAssets.isEmpty()) {
            /* Bins */
            graphicalAssets.add(new GFXAsset(ResMan.BIN1, 696, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.BIN2, 696, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.BIN3, 696, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.BIN4, 696, 1024, 0, 0));

            /* Items */
            graphicalAssets.add(new GFXAsset(ResMan.FACE_BOX_TILED, 696, 1024, 0, 0, 2, 1));

            graphicalAssets.add(new GFXAsset(ResMan.ITEM_TUBE, 99, 512, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.ITEM_CONE_BLUE, 59, 512, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.ITEM_PEN, 390, 2048, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.ITEM_CONE_WHITE, 235, 2048, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.ITEM_CONE_YELLOW, 235, 2048, 0, 0));

            /* HUD */
            graphicalAssets.add(new GFXAsset(ResMan.HUD_LIVES, 1479, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.HUD_SCORE, 1885, 1024, 0, 0));
        }

        return graphicalAssets;
    }

    @Override
    public List<FontAsset> getFontAssets() {
        if (fontAssets.isEmpty()) {
            fontAssets.add(new FontAsset(ResMan.F_HUD_BIN, ResMan.F_HUD_BIN_SIZE, ResMan.F_HUD_BIN_COLOR, ResMan.F_HUD_BIN_ANTI));
        }
        return fontAssets;
    }

    @Override
    public Vector2 getPhysicsVector() {
        return new Vector2(0, SensorManager.GRAVITY_EARTH);
    }

    @Override
    public List<HUDElement> getHudElements() {
        if (elements.isEmpty()) {
            final ITiledTextureRegion textureScore = activity.getTexture(ResMan.HUD_SCORE);
            final ITiledTextureRegion textureLives = activity.getTexture(ResMan.HUD_LIVES);

            final float scale = 0.120f;

            Vector2 posS = new Vector2(5, 0); //activity.spritePosition(textureScore, 0.1f, 0.05f, HUDElement.SCALE_DEFAULT);
            Vector2 posL = new Vector2(155, 0); //activity.spritePosition(textureLives, 0.6f, 0.05f, HUDElement.SCALE_DEFAULT);

            Vector2 offS = new Vector2(120, 45);
            Vector2 offL = new Vector2(170, 45);

            IFont fontRoboto = activity.getFont(FontAsset.name(ResMan.F_HUD_BIN, ResMan.F_HUD_BIN_SIZE, ResMan.F_HUD_BIN_COLOR, ResMan.F_HUD_BIN_ANTI));
            final VertexBufferObjectManager vbom = activity.getVBOM();

            HUDScore = new HUDElement()
                    .buildSprite(posS, textureScore, vbom, scale)
                    .buildText("", "31337".length(), posS.add(offS), fontRoboto, vbom);
            HUDLives = new HUDElement()
                    .buildSprite(posL, textureLives, vbom, scale)
                    .buildText("", "999".length(), posL.add(offL), fontRoboto, vbom);

            elements.add(HUDScore);
            elements.add(HUDLives);
        }

        return elements;
    }

    @Override
    public ContactListener getContactListener() {

        return new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                Bin bin;
                Item item;
                if (contact.isTouching()) {
                    if (Bin.isOne(x1)) {
                        bin = (Bin) x1.getBody().getUserData();
                        item = (Item) x2.getBody().getUserData();
                    } else if (Bin.isOne(x2)) {
                        bin = (Bin) x2.getBody().getUserData();
                        item = (Item) x1.getBody().getUserData();
                    } else {
                        /* Two items are touching. */
                        return;
                    }

                    final boolean validMove = bin.accepts(item);
                    Log.v(TAG, "beginContact - Item " + item + " went in bin " + bin + (validMove ? " :)" : " :("));

                    recycleItem(item);
                    animateBin(bin, validMove);
                    if (validMove) {
                        if (++gameScore >= 100) {
                            activity.onWin();
                        }

                        Log.v(TAG, "beginContact - Increasing score to " + gameScore + ".");
                        setScore(gameScore);
                    } else {
                        if (--gameLives == 0) {
                            activity.onLose();
                        }

                        Log.v(TAG, "beginContact - Decreasing lives to " + gameLives + ".");
                        setLives(gameLives);
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        };
    }

    @Override
    public Scene prepareScene() {
        Scene scene = activity.getScene();

        final Background backgroundColor = new Background(0.96862f, 0.77647f, 0.37647f);
        scene.setBackground(backgroundColor);

        createBins();
        createItems();

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }

    private void createItems() {
        final ITiledTextureRegion smileyTextureRegion = activity.getTexture(ResMan.FACE_BOX_TILED);
//        createItem(Item.Type.random());
        createItem(activity.spritePosition(smileyTextureRegion, 0.2f, 0.5f), Item.Type.random());
    }

    @Override
    public void resetGame() {
        resetGamePoints();

        final Scene gameScene = activity.getScene();
        for (final Item item : items) {
            deleteItem(item);
        }
        items.clear();

        gameScene.clearChildScene();
        activity.resetMenuPause();

        createItems();
    }

    private void resetGamePoints() {
        gameScore = 0;
        gameLives = 3;
        setScore(gameScore);
        setLives(gameLives);
    }

    private void deleteItem(final Item item) {
        item.setVisible(false);
        activity.getScene().getChildByIndex(GameActivity.LAYER_BACKGROUND).detachChild(item);
        activity.markForDeletion(item);
    }

    private void setScore(int score) {
        String padding = "";
        if (score < 10) {
            padding += " ";
        }
        setScore(padding + score);
    }

    private void setScore(CharSequence text) {
        HUDScore.getText().setText(text);
    }

    private void setLives(int value) {
        setLives("" + value);
    }

    private void setLives(CharSequence text) {
        HUDLives.getText().setText(text);
    }


    private void createItem(Item.Type type) {
        float posRatioX = 0.1f + new Random().nextFloat() * 0.9f;
        float posRatioY = new Random().nextFloat() * 0.2f;
        Vector2 itemPos = activity.spritePosition(32, 32, posRatioX, posRatioY);
        Log.d(TAG, "createItem - New item created at " + itemPos.x + ", " + itemPos.y);
        createItem(itemPos, type);
    }

    private void createItem(Vector2 pos, Item.Type type) {
        createItem(pos.x, pos.y, type);
    }

    private void createItem(float posX, float posY, Item.Type type) {
        ITiledTextureRegion textureRegion = activity.getTexture(ResMan.FACE_BOX_TILED);
        switch (type) {
            case PETRI_DISH:
                break;
            case SUBSTRATE_BOX:
                break;
            case SOLVENT:
                break;
            case PAPER:
                break;
            case MICROSCOPE_SLIDE:
                break;
            case PEN:
                textureRegion = activity.getTexture(ResMan.ITEM_PEN);
                break;
            case TUBE:
                textureRegion = activity.getTexture(ResMan.ITEM_TUBE);
                break;
            case CONE_BLUE:
                textureRegion = activity.getTexture(ResMan.ITEM_CONE_BLUE);
                break;
            case CONE_YELLOW:
                textureRegion = activity.getTexture(ResMan.ITEM_CONE_YELLOW);
                break;
            case CONE_WHITE:
                textureRegion = activity.getTexture(ResMan.ITEM_CONE_WHITE);
                break;
        }
        createItem(posX, posY, textureRegion, type);

    }

    private void createItem(float posX, float posY, ITiledTextureRegion textureRegion, Item.Type type) {
        Item item = new Item(type, textureRegion, posX, posY, activity.getVBOM(), activity.getPhysicsWorld());
        items.add(item);
        final Scene gameScene = activity.getScene();
        gameScene.getChildByIndex(GameActivity.LAYER_BACKGROUND).attachChild(item);
        gameScene.registerTouchArea(item);
    }

    private void createBin(Bin.Type type, ITiledTextureRegion textureRegion, float posX, float posY) {
        Bin bin = new Bin(type, posX, posY, textureRegion, activity.getVBOM(), activity.getPhysicsWorld());
        activity.getScene().getChildByIndex(GameActivity.LAYER_FOREGROUND).attachChild(bin);
    }

    private void createBins() {
        final float binY = 0.85f;
        final ITiledTextureRegion bin1TextureRegion = activity.getTexture(ResMan.BIN1);
        final ITiledTextureRegion bin2TextureRegion = activity.getTexture(ResMan.BIN2);
        final ITiledTextureRegion bin3TextureRegion = activity.getTexture(ResMan.BIN3);
        final ITiledTextureRegion bin4TextureRegion = activity.getTexture(ResMan.BIN4);

        Vector2 bin1Pos = activity.spritePosition(bin1TextureRegion, 0.30f, binY, Bin.SCALE_DEFAULT);
        Vector2 bin2Pos = activity.spritePosition(bin2TextureRegion, 0.50f, binY, Bin.SCALE_DEFAULT);
        Vector2 bin3Pos = activity.spritePosition(bin3TextureRegion, 0.70f, binY, Bin.SCALE_DEFAULT);
        Vector2 bin4Pos = activity.spritePosition(bin4TextureRegion, 0.90f, binY, Bin.SCALE_DEFAULT);

        createBin(Bin.Type.GLASS, bin1TextureRegion, bin1Pos.x, bin1Pos.y);
        createBin(Bin.Type.LIQUIDS, bin2TextureRegion, bin2Pos.x, bin2Pos.y);
        createBin(Bin.Type.NORMAL, bin3TextureRegion, bin3Pos.x, bin3Pos.y);
        createBin(Bin.Type.BIO, bin4TextureRegion, bin4Pos.x, bin4Pos.y);
    }

    private void createFloor() {

    }

    private void animateBin(final Bin bin, boolean validMove) {
        final IEntityModifier.IEntityModifierListener logListener = new IEntityModifier.IEntityModifierListener() {
            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String text = "Animation started.";
                        Log.v(TAG, "run - " + text);
                    }
                });
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String text = "Animation finished.";
                        Log.v(TAG, "run - " + text);
                    }
                });
            }
        };
        Color initialColor = bin.getDefaultColor();
        Color toColor = validMove ? Color.GREEN : Color.RED;

        final float pDuration = 0.25f;

        final SequenceEntityModifier entityModifier = new SequenceEntityModifier(
                new ColorModifier(pDuration, initialColor, toColor),
                new ColorModifier(pDuration, toColor, initialColor),
                new DelayModifier(pDuration * 2)
        );

        bin.registerEntityModifier(new LoopEntityModifier(entityModifier, 1, logListener));
    }

    private void recycleItem(Item item) {
        //TODO: Really recycle something
        deleteItem(item);
        items.remove(item);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                createItem(Item.Type.random());
            }
        });
    }

}
