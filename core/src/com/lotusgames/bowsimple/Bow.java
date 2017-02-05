package com.lotusgames.bowsimple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sun.javafx.fxml.expression.UnaryExpression;

/**
 * Created by Alexpan98 on 25.12.2016.
 */
public class Bow {
    final int precision = 1000; //Кол-во точек измерения

    private Stick stick;

    private float maxForce;
    private float minForce;
    private int firstPoint;
    private float stringLength;
    private float stringWidth;

    private Vector2[] tipPositions;
    private float[] displacements;
    private float[] forceProjections;


    Bow(float stringLength, float stringWidth)
    {
        this.stick = null;
        this.stringLength = stringLength;
        this.stringWidth = stringWidth;
        tipPositions = new Vector2[precision];
        displacements = new float[precision];
        forceProjections = new float[precision];
    }

    Bow(Stick stick, float stringLength, float stringWidth)
    {
        this(stringLength, stringWidth);
        this.stick = stick;
    }

    Bow(BowType type, int numberOfPieces, float maxRadius, float pieceLength, Material material, float stringLength, float stringWidth)
    {
        this(stringLength, stringWidth);
        float[] radiuses = new float[numberOfPieces];
        for (int i = 0; i < numberOfPieces; i++)
            radiuses[i] = type.radius(i, numberOfPieces, maxRadius);
        this.stick = new Stick(material, pieceLength, radiuses);
    }

    public void calcShapeByX(float xInMeters)
    {
        calcShape(getPerpForceInNewtons(getI(xInMeters / getMaxXInMeters())));
    }

    public void calcShape(float forceAtTheTip)
    {
        stick.calcShape(forceAtTheTip);
    }

    public void buildTables(float startForce)
    {
        buildTableForPositions(startForce);
        buildTableForDisplacements();
        buildTableForForceProjections();
        stick.calcShape(0);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Arrow arrow)
    {
        batch.begin();
        stick.render(batch);
        batch.end();

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderString(shapeRenderer, arrow);
        shapeRenderer.end();
    }

    private void renderString(ShapeRenderer shapeRenderer, Arrow arrow)
    {
        Piece tip = stick.getTip();
        Vector2 pos1 =
                tip.getPosition().
                        add(-tip.getRadius(), 0).
                        add(new Vector2(tip.getRadius(), tip.getLength()).rotateRad(-tip.getAngle()));
        Vector2 pos2 =
                tip.getPosition().
                        scl(1,-1).
                        add(-tip.getRadius(), 0).
                        add(new Vector2(tip.getRadius(), 0).rotateRad(tip.getAngle()));

        float handX = getProperHandX(arrow.getX());

        shapeRenderer.rectLine(pos1.x, pos1.y, handX, 0,stringWidth);
        shapeRenderer.rectLine(pos2.x, pos2.y, handX, 0,stringWidth);
    }

    public boolean isInside(float xInMeters)
    {
        return xInMeters >= getMinXInMeters() && xInMeters <= getMaxXInMeters();
    }

    public float getProperHandX(float handX)
    {
        if (handX < getMinXInMeters())
            return getMinXInMeters();
        else if (handX > getMaxXInMeters())
            return getMaxXInMeters();
        else
            return handX;
    }



    private void buildTableForPositions(float startForce)
    {
        float force = startForce;
        for (stick.calcShape(startForce);
             stick.getTip().getAngle() < Math.PI/2;
             force *= 1.05f)
        {
            stick.calcShape(force);
        }
        maxForce = force;

        force = 0;
        for (int i = 0; i < precision; i++)
        {
            stick.calcShape(force);
            tipPositions[i] = stick.getTip().getPosition();
            force += maxForce / precision;
        }
    }

    private boolean buildTableForDisplacements()
    {
        if (tipPositions[precision - 1].y > stringLength) return false;
        for (int i = 0; i < precision; i++)
        {
            if (tipPositions[i].y < stringLength){
                minForce = maxForce * (float) i / precision;
                firstPoint = i;
                break;
            }
        }

        for (int i = firstPoint; i < precision; i++)
        {
            float strX = (float) Math.sqrt(stringLength*stringLength - tipPositions[i].y*tipPositions[i].y);
            displacements[i] = strX;
        }
        return true;
    }

    private void buildTableForForceProjections()
    {
        for (int i = firstPoint; i < precision; i++)
        {
            float force = (float) i * maxForce / precision;
            float alpha = stick.getTip().getAngle();
            float beta = (float) Math.atan2(displacements[i], tipPositions[i].y);
            float T = force / (float) Math.sin(alpha + beta);
            float F = T * (float) Math.sin(beta);
            forceProjections[i] = F;
        }
    }

    public abstract static class Values {
        abstract float getX(Bow bow, int i);
        abstract float getY(Bow bow, int i);
        abstract BitmapFont getFont();
        abstract float getMaxX(Bow bow);
        abstract float getMaxY(Bow bow);
        abstract String getXGlyph();
        abstract String getYGlyph();

        public String getXValue(Bow bow, int i) {return String.valueOf(getX(bow, i) * getMaxX(bow)) + getXGlyph();}
        public String getYValue(Bow bow, int i) {return String.valueOf(getY(bow, i) * getMaxY(bow)) + getYGlyph();}

    }

    //0..1
    public float getPerpForce(int i) {return getPerpForceInNewtons(i) / getMaxPerpForceInNewtons();}
    public float getProjForce(int i) {return getProjForceInNewtons(i) / getMaxProjForceInNewtons();}
    public float getDisplacement(int i) {return getDisplacementInMeters(i) / getMaxDisplacementInMeters();}
    public float getX(int i) {return getXInMeters(i) / getMaxXInMeters();
    }

    public void drawGraph(ShapeRenderer renderer, SpriteBatch batch, OrthographicCamera camera, float width, float height, Values values)
    {
        float scale = camera.zoom;
        renderer.setColor(Color.BLACK);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rectLine(0, 0, 0, height, scale * 2f);
        renderer.rectLine(0, 0, width, 0, scale * 2f);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = firstPoint; i < precision; i++)
        {
            float x = values.getX(this, i) * width;
            float y = values.getY(this, i) * height;
            renderer.circle(x, y, scale * 2f,6);
        }
        renderer.end();

        drawLabels(batch, camera, values);
    }

    public void drawLabels(SpriteBatch batch, OrthographicCamera camera, Values values)
    {
        float scale = camera.zoom;
        float handX = getProperHandX(camera.unproject(new Vector3(Gdx.input.getX(),0,0)).x);
        int i = getI(handX / getMaxXInMeters());

        camera.zoom = 1;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        values.getFont().draw(batch, values.getXValue(this, i), handX/scale, -0.04f/scale);
        values.getFont().draw(batch, values.getYValue(this, i), handX/scale, 0.1f/scale);
        batch.end();
        camera.zoom = scale;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    ///MEGA IMBA
    ///i: 0..(precision-1)
    ///x: 0..1
    public int getI(float x)
    {
        if (x < getX(firstPoint)) return firstPoint;
        int i;
        for (i = 0; i < precision; i++)
        {
            if (getX(i) > x) break;
        }
        if (i < precision)
            return i;
        else
            return precision - 1;
    }

    public float getFinalProjectedForce(float xInMeters)
    {
        if (isInside(xInMeters))
            return 2*getProjForceInNewtons(getI(getProperHandX(xInMeters) / getMaxXInMeters()));
        else
            return 0;
    }

    public Stick getStick() {return stick;}
    public float getXInMeters(int i) {return displacements[i] + tipPositions[i].x;}
    public float getMaxXInMeters() {return getXInMeters(precision - 1);}
    public float getMinXInMeters() {return getXInMeters(firstPoint);}
    public float getDisplacementInMeters(int i) {return displacements[i];}
    public float getPerpForceInNewtons(int i) {return (float) i / precision * maxForce;}
    public float getProjForceInNewtons(int i) {return forceProjections[i];}
    public float getMaxDisplacementInMeters() {return getDisplacementInMeters(precision - 1);}
    public float getMaxPerpForceInNewtons() {return maxForce;}
    public float getMaxProjForceInNewtons() {return getProjForceInNewtons(precision - 1);}
}
