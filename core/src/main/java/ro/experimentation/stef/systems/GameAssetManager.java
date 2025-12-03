package ro.experimentation.stef.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import ro.experimentation.stef.config.GameConfig;

/**
 * Manages all game assets including textures, sprites, and sounds.
 * Centralizes resource loading and disposal.
 */
public class GameAssetManager {
    // Character textures
    private Texture[] characterTextures;
    
    // Background and UI
    private Texture backgroundTexture;
    
    // Music
    private Music gameMusic;
    
    // Projectile textures
    private Texture redLaserTexture;
    private Texture blueLaserTexture;
    private Texture yellowLaserTexture;
    private Texture cyanBeamTexture;
    private Texture greenLaserTexture;
    private Texture magentaLaserTexture;
    private Texture orangeLaserTexture;
    
    /**
     * Loads all game assets.
     */
    public void loadAssets() {
        // Load character textures
        characterTextures = new Texture[GameConfig.CHARACTER_TEXTURES.length];
        for (int i = 0; i < GameConfig.CHARACTER_TEXTURES.length; i++) {
            characterTextures[i] = new Texture(GameConfig.CHARACTER_TEXTURES[i]);
        }
        
        // Load background
        backgroundTexture = new Texture(GameConfig.BACKGROUND_TEXTURE);
        
        // Load music
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal(GameConfig.MUSIC_FILE));
        gameMusic.setLooping(true);
        
        // Create laser textures programmatically
        redLaserTexture = createColoredTexture(40, 5, Color.RED);
        blueLaserTexture = createColoredTexture(40, 5, Color.BLUE);
        yellowLaserTexture = createColoredTexture(40, 5, Color.YELLOW);
        cyanBeamTexture = createColoredTexture(150, 25, Color.CYAN);
        greenLaserTexture = createColoredTexture(50, 8, Color.GREEN);
        magentaLaserTexture = createColoredTexture(35, 7, Color.MAGENTA);
        orangeLaserTexture = createColoredTexture(200, 40, Color.ORANGE);
    }
    
    /**
     * Creates a colored texture programmatically.
     *
     * @param width Width of the texture
     * @param height Height of the texture
     * @param color Color of the texture
     * @return The created texture
     */
    private Texture createColoredTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    /**
     * Disposes all loaded assets.
     */
    public void dispose() {
        if (characterTextures != null) {
            for (Texture texture : characterTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        
        if (gameMusic != null) {
            gameMusic.dispose();
        }
        
        if (redLaserTexture != null) {
            redLaserTexture.dispose();
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
    }
    
    // Getters
    public Texture[] getCharacterTextures() {
        return characterTextures;
    }
    
    public Texture getCharacterTexture(int index) {
        return characterTextures[index];
    }
    
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
    
    public Music getGameMusic() {
        return gameMusic;
    }
    
    public Texture getRedLaserTexture() {
        return redLaserTexture;
    }
    
    public Texture getBlueLaserTexture() {
        return blueLaserTexture;
    }
    
    public Texture getYellowLaserTexture() {
        return yellowLaserTexture;
    }
    
    public Texture getCyanBeamTexture() {
        return cyanBeamTexture;
    }
    
    public Texture getGreenLaserTexture() {
        return greenLaserTexture;
    }
    
    public Texture getMagentaLaserTexture() {
        return magentaLaserTexture;
    }
    
    public Texture getOrangeLaserTexture() {
        return orangeLaserTexture;
    }
}
