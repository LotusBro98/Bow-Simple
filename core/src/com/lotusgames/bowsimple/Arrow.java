package com.lotusgames.bowsimple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Alexpan98 on 25.12.2016.
 */
public class Arrow {
    Texture texture = new Texture("arrow.png");

    float mass;
    float velocity;
    float x;
    float width;
    float height;

    Arrow(float mass, float width)
    {
        this.mass = mass;
        this.width = width;
        this.height = (float) texture.getHeight()/ (float) texture.getWidth() * width;
        this.x = 0;
        this.velocity = 0;
    }

    public void tickTime(Bow bow, float deltaTime)
    {
        deltaTime *= 0.01f;
        float a = -1f / mass * bow.getFinalProjectedForce(x);
        velocity += a * deltaTime;
        x += velocity * deltaTime + a * deltaTime * deltaTime / 2;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera, BitmapFont font){
        final float g = 9.81f;
        float scale = camera.zoom;

        batch.begin();
        batch.draw(texture, x - width, -0.5f * height, width, height);
        batch.end();

        camera.zoom = 1;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float drawX = x < 0 ? -width : x - width;
        if (x < 0) {
            drawX = - width;
            font.draw(batch, String.valueOf(velocity*velocity/g) + " m", drawX/scale, -0.04f/scale);
        } else {
            drawX = x - width;
        }
        font.draw(batch, String.valueOf(Math.abs(velocity)) + " m/s", drawX/scale,0.1f/scale);
        batch.end();
        camera.zoom = scale;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public float getX() {return x;}
    public void setX(float x) {this.x = x;}
    public void setVelocity(float v) {this.velocity = v;}
}
