package ro.experimentation.stef;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import java.util.ArrayList;
import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GamesWithStef implements ApplicationListener {

    final static int width = 1920;
    final static int height = 1080;

    // assets
    Texture backgroundTexture;

    Texture characterTexture1;
    Sprite characterSprite1;

    Texture characterTexture2;
    Sprite characterSprite2;

    Texture characterTexture3;
    Sprite characterSprite3;

    Texture characterTexture4;
    Sprite characterSprite4;

    Music   dragonBallMusic;

    // laser assets
    Texture laserTexture;
    ArrayList<Sprite> lasers;
    float laserCooldown = 0f;
    final float LASER_COOLDOWN_TIME = 0.3f;
    final float LASER_SPEED = 700f;

    // control classes
    SpriteBatch spriteBatch;
    FillViewport viewport;

    @Override
    public void create() {
        backgroundTexture = new Texture("dragonballbackground.jpg");

        characterTexture1 = new Texture("Brolly_renewed.png");
        characterSprite1 = new Sprite(characterTexture1);
        characterSprite1.setScale(0.5f);
        characterSprite1.setPosition(0, 0);

        characterTexture2 = new Texture("UltraInstinctGoku.png");
        characterSprite2 = new Sprite(characterTexture2);
        characterSprite2.setScale(0.5f);
        characterSprite2.setPosition(1000, 400);

        characterTexture3 = new Texture("UltraInstinctGoku1.png");
        characterSprite3 = new Sprite(characterTexture3);
        characterSprite3.setScale(0.5f);
        characterSprite3.setPosition(1000, 0);

        characterTexture4 = new Texture("VegitoUltraInstinct1.png");
        characterSprite4 = new Sprite(characterTexture4);
        characterSprite4.setScale(0.5f);
        characterSprite4.setPosition(700, 150);

        dragonBallMusic = Gdx.audio.newMusic(Gdx.files.internal("01.Chozetsu_Dynamic!_(TV_Size).mp3"));

        // create laser texture programmatically
        Pixmap laserPixmap = new Pixmap(40, 5, Pixmap.Format.RGBA8888);
        laserPixmap.setColor(Color.RED);
        laserPixmap.fill();
        laserTexture = new Texture(laserPixmap);
        laserPixmap.dispose();
        
        lasers = new ArrayList<>();

        // end assets initialization

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(width, height);

        // init
        dragonBallMusic.play();
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        if (laserTexture != null) {
            laserTexture.dispose();
        }
    }


    private void input() {
        float speed = 200f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterSprite1.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterSprite1.translateX(-speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            characterSprite1.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterSprite1.translateY(-speed * delta);
        }

        // laser shooting
        laserCooldown -= delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && laserCooldown <= 0) {
            shootLaser();
            laserCooldown = LASER_COOLDOWN_TIME;
        }
    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        
        // update laser positions
        Iterator<Sprite> iterator = lasers.iterator();
        while (iterator.hasNext()) {
            Sprite laser = iterator.next();
            laser.translateX(LASER_SPEED * delta);
            
            // remove lasers that are off-screen
            if (laser.getX() > width) {
                iterator.remove();
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, width, height);
        characterSprite1.draw(spriteBatch);
        characterSprite2.draw(spriteBatch);
        characterSprite3.draw(spriteBatch);
        characterSprite4.draw(spriteBatch);
        
        // draw lasers
        for (Sprite laser : lasers) {
            laser.draw(spriteBatch);
        }
        
        spriteBatch.end();
    }

    private void shootLaser() {
        // Calculate eye position based on character sprite position and scale
        // The character is scaled at 0.5f, so we need to account for that
        float eyeX = characterSprite1.getX() + (characterSprite1.getWidth() * 0.8f);
        float eyeY = characterSprite1.getY() + (characterSprite1.getHeight() * 0.8f);
        
        Sprite laser = new Sprite(laserTexture);
        laser.setPosition(eyeX, eyeY);
        lasers.add(laser);
    }
}
