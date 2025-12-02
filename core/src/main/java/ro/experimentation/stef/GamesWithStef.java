package ro.experimentation.stef;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
    Texture blueLaserTexture;
    Texture yellowLaserTexture;  // for AOE attacks
    Texture cyanBeamTexture;     // for large beam attacks
    Texture greenLaserTexture;   // for player special ability A
    Texture magentaLaserTexture; // for player special ability S
    Texture orangeLaserTexture;  // for player special ability D
    ArrayList<Sprite> lasers;
    ArrayList<LaserData> enemyLasers;
    ArrayList<LaserData> playerSpecialLasers;  // for player special abilities that need velocity
    float laserCooldown = 0f;
    final float LASER_COOLDOWN_TIME = 0.3f;
    final float LASER_SPEED = 700f;
    
    // player special abilities cooldowns
    float abilityACooldown = 0f;
    float abilitySCooldown = 0f;
    float abilityDCooldown = 0f;
    final float ABILITY_COOLDOWN_TIME = 2.0f;

    // control classes
    SpriteBatch spriteBatch;
    FillViewport viewport;

    // pause system
    boolean isPaused;
    BitmapFont font;
    GlyphLayout glyphLayout;

    // health system
    ShapeRenderer shapeRenderer;
    final float MAX_HEALTH = 100f;
    final float DAMAGE_PER_HIT = 10f;
    final float HEALTH_BAR_WIDTH = 120f;
    final float HEALTH_BAR_HEIGHT = 12f;

    float character1Health;
    float character2Health;
    float character3Health;
    float character4Health;

    boolean character1Alive;
    boolean character2Alive;
    boolean character3Alive;
    boolean character4Alive;

    // enemy AI movement
    final float ENEMY_SPEED = 125f;
    final float MIN_TIMER_DURATION = 1f;
    final float MAX_TIMER_DURATION = 3f;
    Vector2 character2Velocity;
    Vector2 character3Velocity;
    Vector2 character4Velocity;

    float character2MoveTimer;
    float character3MoveTimer;
    float character4MoveTimer;

    // enemy shooting
    float character2ShootTimer;
    float character3ShootTimer;
    float character4ShootTimer;

    // Helper class to store laser with velocity
    private static class LaserData {
        Sprite sprite;
        Vector2 velocity;

        LaserData(Sprite sprite, Vector2 velocity) {
            this.sprite = sprite;
            this.velocity = velocity;
        }
    }

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

        // create blue laser texture for enemies
        Pixmap blueLaserPixmap = new Pixmap(40, 5, Pixmap.Format.RGBA8888);
        blueLaserPixmap.setColor(Color.BLUE);
        blueLaserPixmap.fill();
        blueLaserTexture = new Texture(blueLaserPixmap);
        blueLaserPixmap.dispose();

        // create yellow laser texture for AOE attacks
        Pixmap yellowLaserPixmap = new Pixmap(40, 5, Pixmap.Format.RGBA8888);
        yellowLaserPixmap.setColor(Color.YELLOW);
        yellowLaserPixmap.fill();
        yellowLaserTexture = new Texture(yellowLaserPixmap);
        yellowLaserPixmap.dispose();

        // create cyan beam texture for large beam attacks
        Pixmap cyanBeamPixmap = new Pixmap(150, 25, Pixmap.Format.RGBA8888);
        cyanBeamPixmap.setColor(Color.CYAN);
        cyanBeamPixmap.fill();
        cyanBeamTexture = new Texture(cyanBeamPixmap);
        cyanBeamPixmap.dispose();

        // create green laser texture for player ability A (rapid fire)
        Pixmap greenLaserPixmap = new Pixmap(50, 8, Pixmap.Format.RGBA8888);
        greenLaserPixmap.setColor(Color.GREEN);
        greenLaserPixmap.fill();
        greenLaserTexture = new Texture(greenLaserPixmap);
        greenLaserPixmap.dispose();

        // create magenta laser texture for player ability S (circular burst)
        Pixmap magentaLaserPixmap = new Pixmap(35, 7, Pixmap.Format.RGBA8888);
        magentaLaserPixmap.setColor(Color.MAGENTA);
        magentaLaserPixmap.fill();
        magentaLaserTexture = new Texture(magentaLaserPixmap);
        magentaLaserPixmap.dispose();

        // create orange beam texture for player ability D (mega beam)
        Pixmap orangeBeamPixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        orangeBeamPixmap.setColor(Color.ORANGE);
        orangeBeamPixmap.fill();
        orangeLaserTexture = new Texture(orangeBeamPixmap);
        orangeBeamPixmap.dispose();

        lasers = new ArrayList<>();
        enemyLasers = new ArrayList<>();
        playerSpecialLasers = new ArrayList<>();

        // end assets initialization

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(width, height);

        // initialize health system
        shapeRenderer = new ShapeRenderer();
        character1Health = 300f;
        character2Health = MAX_HEALTH;
        character3Health = MAX_HEALTH;
        character4Health = MAX_HEALTH;
        character1Alive = true;
        character2Alive = true;
        character3Alive = true;
        character4Alive = true;

        // initialize enemy movement
        character2Velocity = new Vector2();
        character3Velocity = new Vector2();
        character4Velocity = new Vector2();
        setRandomDirection(character2Velocity);
        setRandomDirection(character3Velocity);
        setRandomDirection(character4Velocity);
        character2MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character3MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character4MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);

        // initialize enemy shooting timers
        character2ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character3ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character4ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);

        // initialize pause system
        isPaused = false;
        font = new BitmapFont();
        font.getData().setScale(3f);
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();

        // init
        dragonBallMusic.setLooping(true);
        dragonBallMusic.play();
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Check for pause toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
            if (isPaused) {
                dragonBallMusic.pause();
            } else {
                dragonBallMusic.play();
            }
        }

        // Only process input and logic if not paused
        if (!isPaused) {
            input();
            logic();
        }
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
        if (blueLaserTexture != null) {
            blueLaserTexture.dispose();
        }
        if (yellowLaserTexture != null) {
            yellowLaserTexture.dispose();
        }
        if (cyanBeamTexture != null) {
            cyanBeamTexture.dispose();
        }
        if (greenLaserTexture != null) {
            greenLaserTexture.dispose();
        }
        if (magentaLaserTexture != null) {
            magentaLaserTexture.dispose();
        }
        if (orangeLaserTexture != null) {
            orangeLaserTexture.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }


    private void input() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }



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
        
        // special abilities cooldowns
        abilityACooldown -= delta;
        abilitySCooldown -= delta;
        abilityDCooldown -= delta;
        
        // Ability A: Rapid Fire - shoots 3 fast green lasers in quick succession
        if (Gdx.input.isKeyJustPressed(Input.Keys.A) && abilityACooldown <= 0) {
            shootRapidFire();
            abilityACooldown = ABILITY_COOLDOWN_TIME;
        }
        
        // Ability S: Circular Burst - shoots 12 magenta lasers in all directions
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) && abilitySCooldown <= 0) {
            shootCircularBurst();
            abilitySCooldown = ABILITY_COOLDOWN_TIME;
        }
        
        // Ability D: Mega Beam - shoots a massive orange beam
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) && abilityDCooldown <= 0) {
            shootMegaBeam();
            abilityDCooldown = ABILITY_COOLDOWN_TIME;
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
                continue;
            }

            // collision detection with enemy characters
            Rectangle laserRect = laser.getBoundingRectangle();

            // check collision with character 2
            if (character2Alive && laserRect.overlaps(characterSprite2.getBoundingRectangle())) {
                character2Health -= DAMAGE_PER_HIT;
                if (character2Health <= 0) {
                    character2Alive = false;
                }
                iterator.remove();
                continue;
            }

            // check collision with character 3
            if (character3Alive && laserRect.overlaps(characterSprite3.getBoundingRectangle())) {
                character3Health -= DAMAGE_PER_HIT;
                if (character3Health <= 0) {
                    character3Alive = false;
                }
                iterator.remove();
                continue;
            }

            // check collision with character 4
            if (character4Alive && laserRect.overlaps(characterSprite4.getBoundingRectangle())) {
                character4Health -= DAMAGE_PER_HIT;
                if (character4Health <= 0) {
                    character4Alive = false;
                }
                iterator.remove();
                continue;
            }
        }

        // update player special laser positions and check collisions with enemies
        Iterator<LaserData> playerSpecialIterator = playerSpecialLasers.iterator();
        while (playerSpecialIterator.hasNext()) {
            LaserData laserData = playerSpecialIterator.next();
            laserData.sprite.translate(laserData.velocity.x * delta, laserData.velocity.y * delta);

            // remove lasers that are off-screen
            if (laserData.sprite.getX() > width || laserData.sprite.getX() < -100 ||
                laserData.sprite.getY() > height || laserData.sprite.getY() < -100) {
                playerSpecialIterator.remove();
                continue;
            }

            // collision detection with enemy characters
            Rectangle laserRect = laserData.sprite.getBoundingRectangle();

            // check collision with character 2
            if (character2Alive && laserRect.overlaps(characterSprite2.getBoundingRectangle())) {
                character2Health -= DAMAGE_PER_HIT;
                if (character2Health <= 0) {
                    character2Alive = false;
                }
                playerSpecialIterator.remove();
                continue;
            }

            // check collision with character 3
            if (character3Alive && laserRect.overlaps(characterSprite3.getBoundingRectangle())) {
                character3Health -= DAMAGE_PER_HIT;
                if (character3Health <= 0) {
                    character3Alive = false;
                }
                playerSpecialIterator.remove();
                continue;
            }

            // check collision with character 4
            if (character4Alive && laserRect.overlaps(characterSprite4.getBoundingRectangle())) {
                character4Health -= DAMAGE_PER_HIT;
                if (character4Health <= 0) {
                    character4Alive = false;
                }
                playerSpecialIterator.remove();
                continue;
            }
        }

        // update enemy laser positions and check collisions with Broly
        Iterator<LaserData> enemyIterator = enemyLasers.iterator();
        while (enemyIterator.hasNext()) {
            LaserData laserData = enemyIterator.next();
            laserData.sprite.translate(laserData.velocity.x * delta, laserData.velocity.y * delta);

            // remove lasers that are off-screen
            if (laserData.sprite.getX() > width || laserData.sprite.getX() < -100 ||
                laserData.sprite.getY() > height || laserData.sprite.getY() < -100) {
                enemyIterator.remove();
                continue;
            }

            // check collision with Broly (character 1)
            if (character1Alive && laserData.sprite.getBoundingRectangle().overlaps(characterSprite1.getBoundingRectangle())) {
                character1Health -= DAMAGE_PER_HIT;
                if (character1Health <= 0) {
                    character1Alive = false;
                }
                enemyIterator.remove();
            }
        }

        // update enemy movement
        if (character2Alive) {
            updateEnemyMovement(characterSprite2, character2Velocity, delta);
            character2MoveTimer -= delta;
            if (character2MoveTimer <= 0) {
                setRandomDirection(character2Velocity);
                character2MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
            }
        }

        if (character3Alive) {
            updateEnemyMovement(characterSprite3, character3Velocity, delta);
            character3MoveTimer -= delta;
            if (character3MoveTimer <= 0) {
                setRandomDirection(character3Velocity);
                character3MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
            }
        }

        if (character4Alive) {
            updateEnemyMovement(characterSprite4, character4Velocity, delta);
            character4MoveTimer -= delta;
            if (character4MoveTimer <= 0) {
                setRandomDirection(character4Velocity);
                character4MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
            }
        }

        // enemy shooting
        if (character1Alive) {
            if (character2Alive) {
                character2ShootTimer -= delta;
                if (character2ShootTimer <= 0) {
                    shootAOEAttack(characterSprite2);  // Character 2 uses AOE attack
                    character2ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
                }
            }

            if (character3Alive) {
                character3ShootTimer -= delta;
                if (character3ShootTimer <= 0) {
                    shootBeamAttack(characterSprite3);  // Character 3 uses beam attack
                    character3ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
                }
            }

            if (character4Alive) {
                character4ShootTimer -= delta;
                if (character4ShootTimer <= 0) {
                    shootEnemyLaser(characterSprite4);  // Character 4 uses normal laser
                    character4ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
                }
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, width, height);

        // only draw Broly if alive
        if (character1Alive) {
            characterSprite1.draw(spriteBatch);
        }

        // only draw alive characters
        if (character2Alive) {
            characterSprite2.draw(spriteBatch);
        }
        if (character3Alive) {
            characterSprite3.draw(spriteBatch);
        }
        if (character4Alive) {
            characterSprite4.draw(spriteBatch);
        }

        // draw player lasers
        for (Sprite laser : lasers) {
            laser.draw(spriteBatch);
        }

        // draw player special lasers
        for (LaserData laserData : playerSpecialLasers) {
            laserData.sprite.draw(spriteBatch);
        }

        // draw enemy lasers
        for (LaserData laserData : enemyLasers) {
            laserData.sprite.draw(spriteBatch);
        }

        spriteBatch.end();

        // draw health bars
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw Broly's health bar
        if (character1Alive) {
            drawHealthBar(characterSprite1, character1Health);
        }

        if (character2Alive) {
            drawHealthBar(characterSprite2, character2Health);
        }
        if (character3Alive) {
            drawHealthBar(characterSprite3, character3Health);
        }
        if (character4Alive) {
            drawHealthBar(characterSprite4, character4Health);
        }

        shapeRenderer.end();

        // Draw pause text if paused
        if (isPaused) {
            spriteBatch.begin();
            String pauseText = "PAUSED";
            glyphLayout.setText(font, pauseText);
            float textX = (width - glyphLayout.width) / 2;
            float textY = height / 2 + glyphLayout.height / 2;
            font.draw(spriteBatch, pauseText, textX, textY);
            spriteBatch.end();
        }
    }

    private void shootLaser() {
        // Calculate eye position based on character sprite position and scale
        // The character is scaled at 0.5f, so we need to account for that
        float eyeX = characterSprite1.getX() - 50 + (characterSprite1.getWidth() * 0.8f);
        float eyeY = characterSprite1.getY() - 100 + (characterSprite1.getHeight() * 0.8f);

        Sprite laser = new Sprite(laserTexture);
        laser.setPosition(eyeX, eyeY);
        lasers.add(laser);
    }

    private void drawHealthBar(Sprite character, float currentHealth) {
        // Calculate health bar position
        // Position above the character's head, centered horizontally
        float barX = character.getX() + (character.getWidth() * character.getScaleX() / 2) - (HEALTH_BAR_WIDTH / 2);
        float barY = character.getY() + (character.getHeight() * character.getScaleY()) + 10;

        // Draw background (red) for max health
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Draw foreground (green) for current health
        if (MAX_HEALTH > 0) {
            float healthPercentage = currentHealth / MAX_HEALTH;
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(barX, barY, HEALTH_BAR_WIDTH * healthPercentage, HEALTH_BAR_HEIGHT);
        }
    }

    private void setRandomDirection(Vector2 velocity) {
        // Generate random direction
        float angle = MathUtils.random(0f, 360f);
        velocity.x = MathUtils.cosDeg(angle) * ENEMY_SPEED;
        velocity.y = MathUtils.sinDeg(angle) * ENEMY_SPEED;
    }

    private void updateEnemyMovement(Sprite character, Vector2 velocity, float delta) {
        // Update position
        character.translate(velocity.x * delta, velocity.y * delta);

        // Keep character within screen bounds
        float x = character.getX();
        float y = character.getY();
        float charWidth = character.getWidth() * character.getScaleX();
        float charHeight = character.getHeight() * character.getScaleY();

        // Bounce off edges or choose new direction
        if (x < 0) {
            character.setX(0);
            velocity.x = Math.abs(velocity.x);
        } else if (x + charWidth > width) {
            character.setX(width - charWidth);
            velocity.x = -Math.abs(velocity.x);
        }

        if (y < 0) {
            character.setY(0);
            velocity.y = Math.abs(velocity.y);
        } else if (y + charHeight > height) {
            character.setY(height - charHeight);
            velocity.y = -Math.abs(velocity.y);
        }
    }

    private void shootEnemyLaser(Sprite enemy) {
        // Calculate eye position for the enemy
        float eyeX = enemy.getX() - 50 + (enemy.getWidth() * 0.8f);
        float eyeY = enemy.getY() - 100 + (enemy.getHeight() * 0.8f);

        // Calculate Broly's center position
        float brolyX = characterSprite1.getX() + (characterSprite1.getWidth() * characterSprite1.getScaleX() / 2);
        float brolyY = characterSprite1.getY() + (characterSprite1.getHeight() * characterSprite1.getScaleY() / 2);

        // Calculate direction from enemy to Broly using Vector2
        Vector2 direction = new Vector2(brolyX - eyeX, brolyY - eyeY);
        direction.nor(); // Normalize the vector
        direction.scl(LASER_SPEED); // Scale to laser speed

        // Create laser sprite
        Sprite laser = new Sprite(blueLaserTexture);
        laser.setPosition(eyeX, eyeY);

        // Add laser to the list with velocity
        enemyLasers.add(new LaserData(laser, direction));
    }

    private void shootAOEAttack(Sprite enemy) {
        // Calculate eye position for the enemy
        float eyeX = enemy.getX() - 50 + (enemy.getWidth() * 0.8f);
        float eyeY = enemy.getY() - 100 + (enemy.getHeight() * 0.8f);

        // Calculate Broly's center position
        float brolyX = characterSprite1.getX() + (characterSprite1.getWidth() * characterSprite1.getScaleX() / 2);
        float brolyY = characterSprite1.getY() + (characterSprite1.getHeight() * characterSprite1.getScaleY() / 2);

        // Calculate base direction from enemy to Broly
        Vector2 baseDirection = new Vector2(brolyX - eyeX, brolyY - eyeY);
        float baseAngle = baseDirection.angleDeg();

        // Create 7 projectiles in a spread pattern
        int numProjectiles = 7;
        float spreadAngle = 40f; // Total spread of 40 degrees
        float angleStep = spreadAngle / (numProjectiles - 1);
        float startAngle = baseAngle - (spreadAngle / 2);

        for (int i = 0; i < numProjectiles; i++) {
            float angle = startAngle + (i * angleStep);
            Vector2 direction = new Vector2();
            direction.x = MathUtils.cosDeg(angle);
            direction.y = MathUtils.sinDeg(angle);
            direction.scl(LASER_SPEED);

            Sprite laser = new Sprite(yellowLaserTexture);
            laser.setPosition(eyeX, eyeY);
            enemyLasers.add(new LaserData(laser, direction));
        }
    }

    private void shootBeamAttack(Sprite enemy) {
        // Calculate eye position for the enemy
        float eyeX = enemy.getX() - 50 + (enemy.getWidth() * 0.8f);
        float eyeY = enemy.getY() - 100 + (enemy.getHeight() * 0.8f);

        // Calculate Broly's center position
        float brolyX = characterSprite1.getX() + (characterSprite1.getWidth() * characterSprite1.getScaleX() / 2);
        float brolyY = characterSprite1.getY() + (characterSprite1.getHeight() * characterSprite1.getScaleY() / 2);

        // Calculate direction from enemy to Broly using Vector2
        Vector2 direction = new Vector2(brolyX - eyeX, brolyY - eyeY);
        direction.nor(); // Normalize the vector
        direction.scl(LASER_SPEED * 0.7f); // Slower beam (70% of normal speed)

        // Create large beam sprite
        Sprite beam = new Sprite(cyanBeamTexture);
        beam.setPosition(eyeX, eyeY);

        // Add beam to the list with velocity
        enemyLasers.add(new LaserData(beam, direction));
    }

    // Player Special Ability A: Rapid Fire - shoots 3 green lasers in a tight spread
    private void shootRapidFire() {
        float eyeX = characterSprite1.getX() - 50 + (characterSprite1.getWidth() * 0.8f);
        float eyeY = characterSprite1.getY() - 100 + (characterSprite1.getHeight() * 0.8f);
        
        // Shoot 3 lasers with slight vertical spread
        for (int i = -1; i <= 1; i++) {
            Sprite laser = new Sprite(greenLaserTexture);
            laser.setPosition(eyeX, eyeY + (i * 15)); // Vertical offset
            lasers.add(laser);
        }
    }

    // Player Special Ability S: Circular Burst - shoots 12 magenta lasers in all directions
    private void shootCircularBurst() {
        float eyeX = characterSprite1.getX() - 50 + (characterSprite1.getWidth() * 0.8f);
        float eyeY = characterSprite1.getY() - 100 + (characterSprite1.getHeight() * 0.8f);
        
        int numProjectiles = 12;
        float angleStep = 360f / numProjectiles;
        
        for (int i = 0; i < numProjectiles; i++) {
            float angle = i * angleStep;
            Vector2 direction = new Vector2();
            direction.x = MathUtils.cosDeg(angle);
            direction.y = MathUtils.sinDeg(angle);
            direction.scl(LASER_SPEED);
            
            Sprite laser = new Sprite(magentaLaserTexture);
            laser.setPosition(eyeX, eyeY);
            
            // Add to player special lasers list with velocity
            playerSpecialLasers.add(new LaserData(laser, direction));
        }
    }

    // Player Special Ability D: Mega Beam - shoots a massive orange beam
    private void shootMegaBeam() {
        float eyeX = characterSprite1.getX() - 50 + (characterSprite1.getWidth() * 0.8f);
        float eyeY = characterSprite1.getY() - 100 + (characterSprite1.getHeight() * 0.8f);
        
        Sprite megaBeam = new Sprite(orangeLaserTexture);
        megaBeam.setPosition(eyeX, eyeY);
        lasers.add(megaBeam);
    }
}
