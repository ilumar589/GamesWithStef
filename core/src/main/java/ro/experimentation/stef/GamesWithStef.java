package ro.experimentation.stef;

import com.badlogic.gdx.Game;
import ro.experimentation.stef.screens.CharacterSelectionScreen;
import ro.experimentation.stef.screens.GameplayScreen;
import ro.experimentation.stef.systems.GameAssetManager;

/**
 * Main game class that manages screens and assets.
 * Refactored from a monolithic ApplicationListener to use libGDX's Game class with proper Screen management.
 */
public class GamesWithStef extends Game {
    
    private GameAssetManager assetManager;
    private CharacterSelectionScreen characterSelectionScreen;
    
    @Override
    public void create() {
        // Initialize asset manager and load all assets
        assetManager = new GameAssetManager();
        assetManager.loadAssets();
        
        // Create and show character selection screen
        characterSelectionScreen = new CharacterSelectionScreen(
            this,
            assetManager.getCharacterTextures(),
            assetManager.getBackgroundTexture()
        );
        setScreen(characterSelectionScreen);
    }
    
    /**
     * Starts the game with the selected character.
     * Called by CharacterSelectionScreen when player confirms selection.
     *
     * @param selectedCharacterIndex The index of the selected character
     */
    public void startGameWithCharacter(int selectedCharacterIndex) {
        GameplayScreen gameplayScreen = new GameplayScreen(assetManager, selectedCharacterIndex);
        setScreen(gameplayScreen);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (screen != null) {
            screen.dispose();
        }
    }
}
