package ro.experimentation.stef.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Enemy;
import ro.experimentation.stef.entities.Player;
import ro.experimentation.stef.systems.CollisionManager;
import ro.experimentation.stef.systems.EnemyAI;
import ro.experimentation.stef.systems.GameAssetManager;
import ro.experimentation.stef.systems.InputHandler;
import ro.experimentation.stef.ui.UIRenderer;
import ro.experimentation.stef.weapons.Projectile;
import ro.experimentation.stef.weapons.ProjectileFactory;

/**
 * Main gameplay screen where the game action happens.
 * Manages the player, enemies, projectiles, and game logic.
 */
public class GameplayScreen implements Screen {
    private final GameAssetManager assetManager;
    private final SpriteBatch spriteBatch;
    private final FillViewport viewport;
    private final UIRenderer uiRenderer;
    private final InputHandler inputHandler;
    private final CollisionManager collisionManager;
    private final EnemyAI enemyAI;
    private final ProjectileFactory projectileFactory;
    
    private Player player;
    private Array<Enemy> enemies;
    private Array<Projectile> playerProjectiles;
    private Array<Projectile> enemyProjectiles;
    
    private boolean isPaused;
    
    /**
     * Creates a new gameplay screen.
     *
     * @param assetManager The asset manager
     * @param selectedCharacterIndex The index of the selected player character
     */
    public GameplayScreen(GameAssetManager assetManager, int selectedCharacterIndex) {
        this.assetManager = assetManager;
        this.spriteBatch = new SpriteBatch();
        this.viewport = new FillViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        this.uiRenderer = new UIRenderer();
        this.inputHandler = new InputHandler();
        this.collisionManager = new CollisionManager();
        
        // Initialize projectile factory
        this.projectileFactory = new ProjectileFactory(
            assetManager.getRedLaserTexture(),
            assetManager.getBlueLaserTexture(),
            assetManager.getYellowLaserTexture(),
            assetManager.getCyanBeamTexture(),
            assetManager.getGreenLaserTexture(),
            assetManager.getMagentaLaserTexture(),
            assetManager.getOrangeLaserTexture()
        );
        
        this.enemyAI = new EnemyAI(projectileFactory);
        
        // Initialize game entities
        initializeEntities(selectedCharacterIndex);
        
        this.isPaused = false;
    }
    
    /**
     * Initializes the player and enemies.
     *
     * @param selectedCharacterIndex The selected player character
     */
    private void initializeEntities(int selectedCharacterIndex) {
        // Create player
        Texture playerTexture = assetManager.getCharacterTexture(selectedCharacterIndex);
        player = new Player(playerTexture);
        player.setPosition(0, 0);
        
        // Create enemies (use other characters as enemies)
        enemies = new Array<>(true, 3);
        playerProjectiles = new Array<>(true, 100);
        enemyProjectiles = new Array<>(true, 100);
        
        int enemyIndex = 0;
        for (int i = 0; i < GameConfig.CHARACTER_TEXTURES.length; i++) {
            if (i != selectedCharacterIndex && enemyIndex < 3) {
                Texture enemyTexture = assetManager.getCharacterTexture(i);
                Enemy enemy = new Enemy(enemyTexture);
                enemy.setPosition(
                    GameConfig.ENEMY_SPAWN_POSITIONS[enemyIndex][0],
                    GameConfig.ENEMY_SPAWN_POSITIONS[enemyIndex][1]
                );
                enemyAI.initializeEnemy(enemy);
                enemies.add(enemy);
                enemyIndex++;
            }
        }
    }
    
    @Override
    public void show() {
        // Start music when screen is shown
        assetManager.getGameMusic().play();
    }
    
    @Override
    public void render(float delta) {
        // Check for pause toggle
        if (inputHandler.shouldTogglePause()) {
            isPaused = !isPaused;
            if (isPaused) {
                assetManager.getGameMusic().pause();
            } else {
                assetManager.getGameMusic().play();
            }
        }
        
        // Check for exit
        if (inputHandler.shouldExit()) {
            Gdx.app.exit();
        }
        
        // Only process input and logic if not paused
        if (!isPaused) {
            handleInput(delta);
            updateLogic(delta);
        }
        
        render();
    }
    
    /**
     * Handles player input.
     *
     * @param delta Time elapsed since last frame
     */
    private void handleInput(float delta) {
        inputHandler.update(delta);
        
        // Handle movement
        inputHandler.handleMovement(player, delta);
        
        // Handle shooting
        if (inputHandler.shouldShoot()) {
            playerProjectiles.add(projectileFactory.createPlayerLaser(player));
        }
        
        // Handle abilities
        if (inputHandler.shouldTriggerAbilityA(player)) {
            player.useAbilityA();
            projectileFactory.createRapidFire(player, playerProjectiles);
        }
        
        if (inputHandler.shouldTriggerAbilityS(player)) {
            player.useAbilityS();
            projectileFactory.createCircularBurst(player, playerProjectiles);
        }
        
        if (inputHandler.shouldTriggerAbilityD(player)) {
            player.useAbilityD();
            playerProjectiles.add(projectileFactory.createMegaBeam(player));
        }
    }
    
    /**
     * Updates game logic.
     *
     * @param delta Time elapsed since last frame
     */
    private void updateLogic(float delta) {
        // Update player
        player.update(delta);
        
        // Update enemies
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta);
            enemyAI.updateMovement(enemy, delta);
            enemyAI.updateShooting(enemy, i, player, enemyProjectiles, delta);
        }
        
        // Update projectiles
        for (Projectile projectile : playerProjectiles) {
            projectile.update(delta);
        }
        for (Projectile projectile : enemyProjectiles) {
            projectile.update(delta);
        }
        
        // Check collisions
        collisionManager.checkPlayerProjectileCollisions(playerProjectiles, enemies);
        collisionManager.checkEnemyProjectileCollisions(enemyProjectiles, player);
    }
    
    /**
     * Renders the game.
     */
    private void render() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        
        spriteBatch.begin();
        
        // Draw background
        spriteBatch.draw(assetManager.getBackgroundTexture(), 0, 0, 
                        GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        // Draw player
        player.render(spriteBatch);
        
        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.render(spriteBatch);
        }
        
        // Draw projectiles
        for (Projectile projectile : playerProjectiles) {
            projectile.render(spriteBatch);
        }
        for (Projectile projectile : enemyProjectiles) {
            projectile.render(spriteBatch);
        }
        
        spriteBatch.end();
        
        // Draw health bars
        uiRenderer.setShapeProjectionMatrix(viewport.getCamera().combined);
        uiRenderer.beginShapes();
        
        uiRenderer.drawHealthBar(player);
        for (Enemy enemy : enemies) {
            uiRenderer.drawHealthBar(enemy);
        }
        
        uiRenderer.endShapes();
        
        // Draw pause text if paused
        if (isPaused) {
            spriteBatch.begin();
            uiRenderer.drawCenteredText(spriteBatch, "PAUSED", 
                                       GameConfig.SCREEN_HEIGHT / 2, 
                                       GameConfig.DEFAULT_FONT_SCALE);
            spriteBatch.end();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, true);
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
        // Stop music when screen is hidden
        assetManager.getGameMusic().pause();
    }
    
    @Override
    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (uiRenderer != null) {
            uiRenderer.dispose();
        }
        if (projectileFactory != null) {
            projectileFactory.dispose();
        }
    }
}
