package columns.model.support;

import columns.model.kernel.Screen;

/**
 * A Screen that discards all drawing calls.
 * Allows View and GameController to operate without a real graphics context.
 */
public class NoOpScreen implements Screen {

    @Override public void setColor(int color) {}
    @Override public void fillRect(int x, int y, int width, int height) {}
    @Override public void drawRect(int x, int y, int width, int height) {}
    @Override public void drawString(String string, int x, int y) {}
    @Override public void clearRect(int x, int y, int width, int height) {}
    @Override public int Black() { return 0; }
    @Override public int White() { return 8; }
}
