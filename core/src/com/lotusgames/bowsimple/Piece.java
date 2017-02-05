package com.lotusgames.bowsimple;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Alexpan98 on 24.12.2016.
 */
public class Piece {
    private Piece next;
    private Piece prev;

    private float length;
    private float radius;
    private Material material;

    private float MomentOfInertia;

    private float angleAddition;
    private float angle;

    private float posX;
    private float posY;

    Piece(Material material, float radius, float length)
    {
        next = null;
        prev = null;
        this.material = material;
        this.length = length;
        this.radius = radius;
        calcI();
    }

    Piece(Piece piece)
    {
        this(piece.material, piece.radius, piece.length);
    }



    public static void calcShape(float forceAtTheTip, Piece tip, Piece root)
    {
        calcAllAngleAdditions(forceAtTheTip, tip);
        calcAllPositions(root);
    }

    private static void calcAllPositions(Piece root) {
        root.angle = 0;
        root.posX = 0;
        root.posY = 0;

        for (Piece cur = root.next; cur != null; cur = cur.next)
        {
            cur.angle = cur.prev.angle + cur.angleAddition;
            cur.posX = cur.prev.posX + cur.prev.length * (float) Math.sin(cur.prev.angle);
            cur.posY = cur.prev.posY + cur.prev.length * (float) Math.cos(cur.prev.angle);
        }
    }

    private static void calcAllAngleAdditions(float perpendicularForceAtTheTip, Piece theTip)
    {
        float F0 = perpendicularForceAtTheTip;
        float a = theTip.getLength() / 2;
        for (Piece cur = theTip; cur != null; cur = cur.prev) {
            cur.calcAngleAddition(F0, a);
            a += cur.getLength();
        }
    }

    private void calcAngleAddition(float perpendicularForceAtTheTip, float distanceFromTheTip)
    {
        //M = E*I/R
        //R - радиус кривизны
        float M = perpendicularForceAtTheTip * distanceFromTheTip;
        angleAddition = M * (length / (getI() * material.getE()));
    }

    public void appendToThis(Piece next) {
        this.next = next;
        next.prev = this;
    }


    public float getRadius() {return radius;}
    public float getPosY() {return posY;}
    public float getPosX() {return posX;}
    public Vector2 getPosition() {return new Vector2(posX, posY);}
    public Piece getNext() {return next;}
    public Piece getPrev() {return prev;}
    public float getAngle() {return angle;}
    public float getLength() {return length;}
    public float getI() {return MomentOfInertia;}
    private void calcI() {MomentOfInertia = (float) Math.PI * radius * radius * radius * radius / 4;}

    public void render(SpriteBatch batch)
    {
        Texture texture = material.getTexture();

        float x = (posX - radius);
        float y  =  posY;
        float r = 2 * radius;
        float l = 1.1f *  length;
        float a = -angle * 180 / (float) Math.PI;
        int w = texture.getWidth();
        int h = texture.getHeight();

        //batch.draw(texture, offsetX + scale*posX, offsetY + scale*posY, scale*radius, scale*length);
        batch.draw(texture, x, y , 0, 0, r, l, 1, 1,  a, 0, 0, w, h, false, false);
        batch.draw(texture, x, -y, 0, 0, r, l, 1, 1, -a, 0, 0, w, h, false, true);
    }
}
