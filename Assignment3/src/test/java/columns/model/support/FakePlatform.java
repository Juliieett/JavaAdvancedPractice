package columns.model.support;

import columns.model.GameEvent;
import columns.model.kernel.Platform;
import columns.model.kernel.RandomGenerator;
import columns.model.kernel.Screen;

/**
 * Controllable Platform stub for controller tests.
 * - delay() is a no-op so tests run instantly.
 * - currentTime() returns a fixed, configurable value.
 * - isKeyPressed() / getEvent() return values set by the test.
 * - keyPressedAfterCalls: if set > 0, isKeyPressed() returns true after that many calls
 *   (used to break the PAUSE loop after one iteration).
 */
public class FakePlatform implements Platform {

    private long currentTime = 99_999L;
    private long tc = 0L;
    private boolean keyPressed = false;
    private GameEvent event = GameEvent.NONE;
    private final Screen screen = new NoOpScreen();
    private RandomGenerator randomGenerator = new FakeRandomGenerator(1, 2, 3);

    /** If > 0, isKeyPressed() starts returning true after this many calls. */
    private int keyPressedAfterCalls = -1;
    private int isKeyPressedCallCount = 0;

    public void setCurrentTime(long t) { this.currentTime = t; }
    public void setEvent(GameEvent event) { this.event = event; }
    public void setRandomGenerator(RandomGenerator rg) { this.randomGenerator = rg; }
    /** Makes isKeyPressed() return true after n calls (to break the PAUSE loop). */
    public void setKeyPressedAfterCalls(int n) { this.keyPressedAfterCalls = n; }

    @Override public void delay(long t) { /* no-op */ }
    @Override public long currentTime() { return currentTime; }
    @Override public boolean isKeyPressed() {
        isKeyPressedCallCount++;
        if (keyPressedAfterCalls > 0 && isKeyPressedCallCount >= keyPressedAfterCalls) {
            return true;
        }
        return keyPressed;
    }
    @Override public void setKeyPressed(boolean kp) { this.keyPressed = kp; }
    @Override public Screen getScreen() { return screen; }
    @Override public long getTc() { return tc; }
    @Override public void setTc(long time) { this.tc = time; }
    @Override public int getKeyPressed() { return 0; }
    @Override public GameEvent getEvent() { return event; }
    @Override public RandomGenerator getRandomGenerator() { return randomGenerator; }
}
