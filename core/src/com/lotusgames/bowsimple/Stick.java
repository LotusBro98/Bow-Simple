package com.lotusgames.bowsimple;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Alexpan98 on 24.12.2016.
 */
public class Stick {
    private Piece root;
    private Piece tip;

    Stick(Material material, float length, float[] radiuses)
    {
        root = new Piece(material, radiuses[0], length);
        Piece cur = root;
        for (int i = 1; i < radiuses.length; i++)
        {
            cur.appendToThis(new Piece(material, radiuses[i], length));
            cur = cur.getNext();
        }
        tip = cur;

        calcShape(0);
    }

    public void calcShape(float forceAtTheTip)
    {
        Piece.calcShape(forceAtTheTip, tip, root);
    }

    public void render(SpriteBatch batch)
    {
        for (Piece cur = root; cur != null; cur = cur.getNext())
            cur.render(batch);
    }

    public Piece getTip() {return tip;}
    public Piece getRoot() {return root;}
}
