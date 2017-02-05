package com.lotusgames.bowsimple;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	BitmapFont font;

	Bow bow;
	Arrow arrow;
	
	@Override
	public void create () {
		bow = new Bow(BowType.NEWBIE_BOW,
				100,
				0.112f,
				0.01f,
				Material.WOOD,
				0.95f,
				0.007f);
		bow.buildTables(1);

		arrow = new Arrow(0.1f, 1.5f);


		font = new BitmapFont(Gdx.files.internal("font.fnt"));

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.zoom = 1/450f;
		camera.position.set(0,0,0);
		camera.update();
		width = bow.getMaxXInMeters();
		height = bow.getStick().getTip().getPosition().y;

		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	float width;
	float height;
	Bow.Values values = new Bow.Values() {
		public float getX(Bow bow, int i) {return bow.getX(i);}
		public float getY(Bow bow, int i) {return bow.getProjForce(i);}
		public BitmapFont getFont() {return font;}
		public float getMaxX(Bow bow) {return bow.getMaxXInMeters();}
		public float getMaxY(Bow bow) {return bow.getMaxProjForceInNewtons();}
		public String getXGlyph() {return " m";}
		public String getYGlyph() {return " H";}
	};

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		bow.calcShapeByX(arrow.getX());
		if (!Gdx.input.isButtonPressed(Input.Keys.LEFT))
			arrow.tickTime(bow,Gdx.graphics.getDeltaTime());
		else {
			arrow.setX(bow.getProperHandX(camera.unproject(new Vector3(Gdx.input.getX(), 0, 0)).x));
			arrow.setVelocity(0);
		}

		bow.render(batch, shapeRenderer, arrow);
		arrow.render(batch, camera, font);

		//bow.drawLabels(batch, camera, values);
		bow.drawGraph(shapeRenderer, batch, camera, width, height, values);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
