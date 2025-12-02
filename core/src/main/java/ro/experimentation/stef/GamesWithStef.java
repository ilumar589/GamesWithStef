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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import java.util.ArrayList;
import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GamesWithStef implements ApplicationListener {

    final static int width = 1920;
    final static int height = 1080;

    // Game state management
    private enum GameState {
        CHARACTER_SELECTION,
        GAMEPLAY
    }
    private GameState gameState = GameState.CHARACTER_SELECTION;

    // Character selection
    private static class CharacterInfo {
        String texturePath;
        String name;
        Texture texture;

        CharacterInfo(String texturePath, String name) {
            this.texturePath = texturePath;
            this.name = name;
        }
    }
    private CharacterInfo[] characters;
    private int selectedCharacterIndex = 0;

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

    // Character selection rendering constants
    final float CHARACTER_SCALE = 0.4f;
    final int HIGHLIGHT_THICKNESS = 5;
    final float HIGHLIGHT_PADDING = 10f;
    final float CHARACTER_Y_OFFSET = 100f;
    final float CHARACTER_NAME_OFFSET = 30f;

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
    final float[][] ENEMY_SPAWN_POSITIONS = {
        {1000f, 400f},  // Enemy 1 position
        {1000f, 0f},    // Enemy 2 position
        {700f, 150f}    // Enemy 3 position
    };
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

    // Cached Rectangle objects for collision detection to reduce allocations
    private Rectangle tempLaserRect;
    private Rectangle tempChar2Rect;
    private Rectangle tempChar3Rect;
    private Rectangle tempChar4Rect;
    private Rectangle tempChar1Rect;

    // Helper class to store laser with velocity
    // Poolable object to reduce allocations
    private static class LaserData {
        Sprite sprite;
        Vector2 velocity;

        LaserData() {
            this.velocity = new Vector2();
        }

        // Reset method for object pooling
        void reset() {
            this.sprite = null;
            this.velocity.set(0, 0);
        }
    }

    // Object pool for LaserData to reduce allocations
    private Pool<LaserData> laserDataPool;

    @Override
    public void create() {
        // Initialize character selection data
        characters = new CharacterInfo[4];
        characters[0] = new CharacterInfo("Brolly_renewed.png", "Broly");
        characters[1] = new CharacterInfo("UltraInstinctGoku.png", "UI Goku");
        characters[2] = new CharacterInfo("UltraInstinctGoku1.png", "UI Goku 2");
        characters[3] = new CharacterInfo("VegitoUltraInstinct1.png", "Vegito UI");

        // Load all character textures
        for (CharacterInfo character : characters) {
            character.texture = new Texture(character.texturePath);
        }

        backgroundTexture = new Texture("dragonballbackground.jpg");

        // Character textures and sprites will be assigned after selection

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

        // Initialize object pool for LaserData to reduce allocations
        laserDataPool = new Pool<LaserData>() {
            @Override
            protected LaserData newObject() {
                return new LaserData();
            }
        };

        // Initialize cached Rectangle objects for collision detection
        tempLaserRect = new Rectangle();
        tempChar2Rect = new Rectangle();
        tempChar3Rect = new Rectangle();
        tempChar4Rect = new Rectangle();
        tempChar1Rect = new Rectangle();

        // end assets initialization

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(width, height);

        // initialize health system
        shapeRenderer = new ShapeRenderer();

        // Don't initialize health and AI until character selection is done
        character1Health = 0;
        character2Health = 0;
        character3Health = 0;
        character4Health = 0;
        character1Alive = false;
        character2Alive = false;
        character3Alive = false;
        character4Alive = false;

        // initialize pause system
        isPaused = false;
        font = new BitmapFont();
        font.getData().setScale(3f);
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();

        // Don't start music until character is selected - music will start when gameplay begins
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        if (gameState == GameState.CHARACTER_SELECTION) {
            handleCharacterSelection();
            renderCharacterSelection();
        } else {
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (dragonBallMusic != null) {
            dragonBallMusic.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        // Dispose character textures from the CharacterInfo array
        // Note: characterTexture1-4 are references to textures in the characters array,
        // so we only dispose the textures once from the characters array
        if (characters != null) {
            for (CharacterInfo character : characters) {
                if (character.texture != null) {
                    character.texture.dispose();
                }
            }
        }
        
        // Clear object pools
        if (laserDataPool != null) {
            laserDataPool.clear();
        }
        // Free all pooled Vector2 objects from libGDX Pools
        Pools.get(Vector2.class).clear();
    }

    private void handleCharacterSelection() {
        // Navigate between characters
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCharacterIndex--;
            if (selectedCharacterIndex < 0) {
                selectedCharacterIndex = characters.length - 1;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCharacterIndex++;
            if (selectedCharacterIndex >= characters.length) {
                selectedCharacterIndex = 0;
            }
        }

        // Confirm selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startGameWithSelectedCharacter();
        }
    }

    private void renderCharacterSelection() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        
        // Draw background
        spriteBatch.draw(backgroundTexture, 0, 0, width, height);

        // Draw title text
        drawTextCentered("SELECT YOUR FIGHTER", height - CHARACTER_Y_OFFSET, 3f);

        // Draw instruction text
        drawTextCentered("Use LEFT/RIGHT arrows - Press ENTER to confirm", CHARACTER_Y_OFFSET, 2f);

        // Calculate layout for characters (horizontal layout)
        float characterY = height / 2 - CHARACTER_Y_OFFSET;

        for (int i = 0; i < characters.length; i++) {
            CharacterInfo character = characters[i];
            float characterX = calculateCharacterX(i, CHARACTER_SCALE);

            // Draw character sprite
            spriteBatch.draw(character.texture, characterX, characterY, 
                           character.texture.getWidth() * CHARACTER_SCALE, 
                           character.texture.getHeight() * CHARACTER_SCALE);

            // Draw character name
            font.getData().setScale(2f);
            glyphLayout.setText(font, character.name);
            float nameX = characterX + (character.texture.getWidth() * CHARACTER_SCALE / 2) - (glyphLayout.width / 2);
            float nameY = characterY - CHARACTER_NAME_OFFSET;
            font.draw(spriteBatch, character.name, nameX, nameY);
            font.getData().setScale(3f);
        }

        spriteBatch.end();

        // Draw selection highlight box around selected character
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        
        CharacterInfo selectedChar = characters[selectedCharacterIndex];
        float selectedX = calculateCharacterX(selectedCharacterIndex, CHARACTER_SCALE);
        float boxWidth = selectedChar.texture.getWidth() * CHARACTER_SCALE;
        float boxHeight = selectedChar.texture.getHeight() * CHARACTER_SCALE;
        
        // Draw thick selection box
        for (int i = 0; i < HIGHLIGHT_THICKNESS; i++) {
            shapeRenderer.rect(selectedX - HIGHLIGHT_PADDING - i, characterY - HIGHLIGHT_PADDING - i, 
                             boxWidth + HIGHLIGHT_PADDING * 2 + i * 2, boxHeight + HIGHLIGHT_PADDING * 2 + i * 2);
        }
        
        shapeRenderer.end();
    }

    private float calculateCharacterX(int index, float scale) {
        float spacing = width / (characters.length + 1);
        return spacing * (index + 1) - (characters[index].texture.getWidth() * scale / 2);
    }

    private void drawTextCentered(String text, float y, float scale) {
        float originalScale = font.getData().scaleX;
        font.getData().setScale(scale);
        glyphLayout.setText(font, text);
        float x = (width - glyphLayout.width) / 2;
        font.draw(spriteBatch, text, x, y);
        font.getData().setScale(originalScale);
    }

    private void startGameWithSelectedCharacter() {
        // Assign selected character as player (character 1)
        characterTexture1 = characters[selectedCharacterIndex].texture;
        characterSprite1 = new Sprite(characterTexture1);
        characterSprite1.setScale(0.5f);
        characterSprite1.setPosition(0, 0);

        // Assign remaining characters as enemies (characters 2, 3, 4)
        int enemyIndex = 0;

        for (int i = 0; i < characters.length; i++) {
            if (i != selectedCharacterIndex) {
                if (enemyIndex == 0) {
                    characterTexture2 = characters[i].texture;
                    characterSprite2 = new Sprite(characterTexture2);
                    characterSprite2.setScale(0.5f);
                    characterSprite2.setPosition(ENEMY_SPAWN_POSITIONS[0][0], ENEMY_SPAWN_POSITIONS[0][1]);
                } else if (enemyIndex == 1) {
                    characterTexture3 = characters[i].texture;
                    characterSprite3 = new Sprite(characterTexture3);
                    characterSprite3.setScale(0.5f);
                    characterSprite3.setPosition(ENEMY_SPAWN_POSITIONS[1][0], ENEMY_SPAWN_POSITIONS[1][1]);
                } else if (enemyIndex == 2) {
                    characterTexture4 = characters[i].texture;
                    characterSprite4 = new Sprite(characterTexture4);
                    characterSprite4.setScale(0.5f);
                    characterSprite4.setPosition(ENEMY_SPAWN_POSITIONS[2][0], ENEMY_SPAWN_POSITIONS[2][1]);
                }
                enemyIndex++;
            }
        }

        // Initialize health
        character1Health = 300f;
        character2Health = MAX_HEALTH;
        character3Health = MAX_HEALTH;
        character4Health = MAX_HEALTH;
        character1Alive = true;
        character2Alive = true;
        character3Alive = true;
        character4Alive = true;

        // Initialize enemy movement
        character2Velocity = new Vector2();
        character3Velocity = new Vector2();
        character4Velocity = new Vector2();
        setRandomDirection(character2Velocity);
        setRandomDirection(character3Velocity);
        setRandomDirection(character4Velocity);
        character2MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character3MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character4MoveTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);

        // Initialize enemy shooting timers
        character2ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character3ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);
        character4ShootTimer = MathUtils.random(MIN_TIMER_DURATION, MAX_TIMER_DURATION);

        // Start music and switch to gameplay
        dragonBallMusic.setLooping(true);
        dragonBallMusic.play();
        gameState = GameState.GAMEPLAY;
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
            // Use cached rectangle to avoid allocation
            tempLaserRect.set(laser.getBoundingRectangle());

            // check collision with character 2
            if (character2Alive) {
                tempChar2Rect.set(characterSprite2.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar2Rect)) {
                    character2Health -= DAMAGE_PER_HIT;
                    if (character2Health <= 0) {
                        character2Alive = false;
                    }
                    iterator.remove();
                    continue;
                }
            }

            // check collision with character 3
            if (character3Alive) {
                tempChar3Rect.set(characterSprite3.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar3Rect)) {
                    character3Health -= DAMAGE_PER_HIT;
                    if (character3Health <= 0) {
                        character3Alive = false;
                    }
                    iterator.remove();
                    continue;
                }
            }

            // check collision with character 4
            if (character4Alive) {
                tempChar4Rect.set(characterSprite4.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar4Rect)) {
                    character4Health -= DAMAGE_PER_HIT;
                    if (character4Health <= 0) {
                        character4Alive = false;
                    }
                    iterator.remove();
                    continue;
                }
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
                // Free the LaserData object back to the pool
                laserDataPool.free(laserData);
                continue;
            }

            // collision detection with enemy characters
            tempLaserRect.set(laserData.sprite.getBoundingRectangle());

            // check collision with character 2
            if (character2Alive) {
                tempChar2Rect.set(characterSprite2.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar2Rect)) {
                    character2Health -= DAMAGE_PER_HIT;
                    if (character2Health <= 0) {
                        character2Alive = false;
                    }
                    playerSpecialIterator.remove();
                    // Free the LaserData object back to the pool
                    laserDataPool.free(laserData);
                    continue;
                }
            }

            // check collision with character 3
            if (character3Alive) {
                tempChar3Rect.set(characterSprite3.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar3Rect)) {
                    character3Health -= DAMAGE_PER_HIT;
                    if (character3Health <= 0) {
                        character3Alive = false;
                    }
                    playerSpecialIterator.remove();
                    // Free the LaserData object back to the pool
                    laserDataPool.free(laserData);
                    continue;
                }
            }

            // check collision with character 4
            if (character4Alive) {
                tempChar4Rect.set(characterSprite4.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar4Rect)) {
                    character4Health -= DAMAGE_PER_HIT;
                    if (character4Health <= 0) {
                        character4Alive = false;
                    }
                    playerSpecialIterator.remove();
                    // Free the LaserData object back to the pool
                    laserDataPool.free(laserData);
                    continue;
                }
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
                // Free the LaserData object back to the pool
                laserDataPool.free(laserData);
                continue;
            }

            // check collision with Broly (character 1)
            if (character1Alive) {
                tempLaserRect.set(laserData.sprite.getBoundingRectangle());
                tempChar1Rect.set(characterSprite1.getBoundingRectangle());
                if (tempLaserRect.overlaps(tempChar1Rect)) {
                    character1Health -= DAMAGE_PER_HIT;
                    if (character1Health <= 0) {
                        character1Alive = false;
                    }
                    enemyIterator.remove();
                    // Free the LaserData object back to the pool
                    laserDataPool.free(laserData);
                    continue;
                }
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

        // Calculate direction from enemy to Broly using pooled Vector2
        Vector2 direction = Pools.obtain(Vector2.class);
        direction.set(brolyX - eyeX, brolyY - eyeY);
        direction.nor(); // Normalize the vector
        direction.scl(LASER_SPEED); // Scale to laser speed

        // Create laser sprite
        Sprite laser = new Sprite(blueLaserTexture);
        laser.setPosition(eyeX, eyeY);

        // Get LaserData from pool and set values
        LaserData laserData = laserDataPool.obtain();
        laserData.sprite = laser;
        laserData.velocity.set(direction);
        
        // Free the temporary direction vector
        Pools.free(direction);

        // Add laser to the list
        enemyLasers.add(laserData);
    }

    private void shootAOEAttack(Sprite enemy) {
        // Calculate eye position for the enemy
        float eyeX = enemy.getX() - 50 + (enemy.getWidth() * 0.8f);
        float eyeY = enemy.getY() - 100 + (enemy.getHeight() * 0.8f);

        // Calculate Broly's center position
        float brolyX = characterSprite1.getX() + (characterSprite1.getWidth() * characterSprite1.getScaleX() / 2);
        float brolyY = characterSprite1.getY() + (characterSprite1.getHeight() * characterSprite1.getScaleY() / 2);

        // Calculate base direction from enemy to Broly using pooled Vector2
        Vector2 baseDirection = Pools.obtain(Vector2.class);
        baseDirection.set(brolyX - eyeX, brolyY - eyeY);
        float baseAngle = baseDirection.angleDeg();
        Pools.free(baseDirection);  // Free immediately after use

        // Create 7 projectiles in a spread pattern
        int numProjectiles = 7;
        float spreadAngle = 40f; // Total spread of 40 degrees
        float angleStep = spreadAngle / (numProjectiles - 1);
        float startAngle = baseAngle - (spreadAngle / 2);

        for (int i = 0; i < numProjectiles; i++) {
            float angle = startAngle + (i * angleStep);
            
            // Get LaserData from pool
            LaserData laserData = laserDataPool.obtain();
            laserData.velocity.x = MathUtils.cosDeg(angle);
            laserData.velocity.y = MathUtils.sinDeg(angle);
            laserData.velocity.scl(LASER_SPEED);

            Sprite laser = new Sprite(yellowLaserTexture);
            laser.setPosition(eyeX, eyeY);
            laserData.sprite = laser;
            
            enemyLasers.add(laserData);
        }
    }

    private void shootBeamAttack(Sprite enemy) {
        // Calculate eye position for the enemy
        float eyeX = enemy.getX() - 50 + (enemy.getWidth() * 0.8f);
        float eyeY = enemy.getY() - 100 + (enemy.getHeight() * 0.8f);

        // Calculate Broly's center position
        float brolyX = characterSprite1.getX() + (characterSprite1.getWidth() * characterSprite1.getScaleX() / 2);
        float brolyY = characterSprite1.getY() + (characterSprite1.getHeight() * characterSprite1.getScaleY() / 2);

        // Calculate direction from enemy to Broly using pooled Vector2
        Vector2 direction = Pools.obtain(Vector2.class);
        direction.set(brolyX - eyeX, brolyY - eyeY);
        direction.nor(); // Normalize the vector
        direction.scl(LASER_SPEED * 0.7f); // Slower beam (70% of normal speed)

        // Create large beam sprite
        Sprite beam = new Sprite(cyanBeamTexture);
        beam.setPosition(eyeX, eyeY);

        // Get LaserData from pool and set values
        LaserData laserData = laserDataPool.obtain();
        laserData.sprite = beam;
        laserData.velocity.set(direction);
        
        // Free the temporary direction vector
        Pools.free(direction);

        // Add beam to the list
        enemyLasers.add(laserData);
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
            
            // Get LaserData from pool
            LaserData laserData = laserDataPool.obtain();
            laserData.velocity.x = MathUtils.cosDeg(angle);
            laserData.velocity.y = MathUtils.sinDeg(angle);
            laserData.velocity.scl(LASER_SPEED);
            
            Sprite laser = new Sprite(magentaLaserTexture);
            laser.setPosition(eyeX, eyeY);
            laserData.sprite = laser;
            
            // Add to player special lasers list with velocity
            playerSpecialLasers.add(laserData);
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
