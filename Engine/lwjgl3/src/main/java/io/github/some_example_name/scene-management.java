import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SceneManagement {
    private String background;
    private Texture backgroundTexture;
    private boolean inTransition;

    public SceneManagement() {
        inTransition = false;
    }

    public void render(SpriteBatch batch) {
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0);
        }
    }

    public void load_scene(String backgroundPath) {
        this.background = backgroundPath;
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        backgroundTexture = new Texture(backgroundPath);
    }

    public void update() {
        // Update scene logic
        if (inTransition) {
            // Handle transition effects
        }
    }

    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
