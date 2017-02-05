package com.lotusgames.bowsimple;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Alexpan98 on 24.12.2016.
 */
public enum Material {
    WOOD(1E+10f, new Texture("wood.png")); //10 ГПа

    private float ElasticModulus;
    private Texture texture;

    Material(float ElasticModulus, Texture texture) {
        this.ElasticModulus = ElasticModulus;
        this.texture = texture;
    }

    float getElasticModulus() {return ElasticModulus;}
    float getE() {return ElasticModulus;}

    Texture getTexture() {return texture;}
}
