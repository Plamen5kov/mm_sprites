package com.plamen.sprites.drawable;

import java.util.ArrayList;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

/**
 * @author maximo guerrero
 */
public class SpriteTile extends Drawable {
    private final View outerView; //used for invalidation only (tell outer view when to redraw)
    private Bitmap sprites; //sprite sheet for all animations. rectangles are used to slip and only show parts of one bitmap
    private Hashtable<String, AnimationSequece> animations; //all animation sequences for this sprite

    private int currentAnimationIndex = 0; //index in allAnimations array
    private int currentSpriteIndex = 0; //current sprite being drawn
    private String currentAnimation = "m2t"; //current animation sequence
    private String[] allAnimations = {"t2c", "c2m", "m2t"}; //animation names

    private boolean runAnimation = false;
    private int spriteOffsetX = 0;
    private int spriteOffsetY = 0;

    public void setRunAnimation(boolean runAnimation) {
        this.runAnimation = runAnimation;
    }

    // Class contains Information about one frame
    private class FrameInfo {
        public Rect rect = new Rect();
    }

    //Class encapsulates all the data for an animations sequence. List for frames, animcation name, if the sequence will loop and collission info
    private class AnimationSequece {
        public ArrayList<FrameInfo> sequence;
        public Rect collisionRect;
        public boolean canLoop = false;
    }

    //takes resource ids for bitmaps and xmlfiles
    public SpriteTile(int BitmapResourceId, int XmlAnimationResourceId, Context context, View view) {
        this.outerView = view;
        loadSprite(BitmapResourceId, XmlAnimationResourceId, context);
    }

    public void loadSprite(int spriteid, int xmlid, Context context) {

        //load picture with sprites
        sprites = BitmapFactory.decodeResource(context.getResources(), spriteid);

        //load splice from xml
        XmlResourceParser spliceInfo = context.getResources().getXml(xmlid);

        animations = new Hashtable<String, AnimationSequece>();

        try {
            int eventType = spliceInfo.getEventType();
            String animationname = "";
            AnimationSequece animationsequence = new AnimationSequece();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                    if (spliceInfo.getName().toLowerCase().equals("animation")) {
                        animationname = spliceInfo.getAttributeValue(null, "name");
                        animationsequence = new AnimationSequece();
                        animationsequence.sequence = new ArrayList<FrameInfo>();
                        animationsequence.canLoop = spliceInfo.getAttributeBooleanValue(null, "canLoop", false);
                    } else if (spliceInfo.getName().toLowerCase().equals("framerect")) {
                        FrameInfo frameinfo = new FrameInfo();
                        Rect frame = new Rect();
                        frame.top = spliceInfo.getAttributeIntValue(null, "top", 0);
                        frame.bottom = spliceInfo.getAttributeIntValue(null, "bottom", 0);
                        frame.left = spliceInfo.getAttributeIntValue(null, "left", 0);
                        frame.right = spliceInfo.getAttributeIntValue(null, "right", 0);
                        frameinfo.rect = frame;
                        animationsequence.sequence.add(frameinfo);

                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (spliceInfo.getName().toLowerCase().equals("animation")) {
                        animations.put(animationname, animationsequence);
                    }
                }
                eventType = spliceInfo.next();
            }
        } catch (Exception e) {
            Log.e("ERROR", " sprite parsing failed " + e.toString());
        }
    }

    //Draw sprite onto screen
    @Override
    public void draw(Canvas canvas) {
        if (runAnimation) {
            FrameInfo frameinfo = animations.get(currentAnimation).sequence.get(currentSpriteIndex);
            Rect rclip = frameinfo.rect;
            Rect dest = new Rect(getSpriteOffsetX(), //left
                    canvas.getHeight() / 5, //top
                    canvas.getWidth(), //right
                    canvas.getHeight() - canvas.getHeight() / 5); //bottom

            //draw
            canvas.drawBitmap(sprites, rclip, dest, null);

            //update
            if (currentSpriteIndex == animations.get(currentAnimation).sequence.size() - 1) {
                currentSpriteIndex = 0;
                this.runAnimation = false;
                currentAnimation = this.getNextAnimationName();

            } else {
                currentSpriteIndex++;
                outerView.invalidate();
            }
        }
    }

    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub
    }

    private String getNextAnimationName() {
        String currentAnimationName = allAnimations[currentAnimationIndex];
        currentAnimationIndex++;
        if (currentAnimationIndex >= allAnimations.length) {
            currentAnimationIndex = 0;
        }
        return currentAnimationName;
    }

    public int getSpriteOffsetX() {
        return spriteOffsetX;
    }

    public int getSpriteOffsetY() {
        return spriteOffsetY;
    }
}
