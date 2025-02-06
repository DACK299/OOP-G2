import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class IOManagement {
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }

    public boolean isKeyJustPressed(int key) {
        return Gdx.input.isKeyJustPressed(key);
    }

    public void checkKeyInputs() {
        // Example key checks
        if (isKeyPressed(Input.Keys.LEFT)) {
            // Handle left movement
        }
        if (isKeyPressed(Input.Keys.RIGHT)) {
            // Handle right movement
        }
        // Add more key checks as needed
    }
}
