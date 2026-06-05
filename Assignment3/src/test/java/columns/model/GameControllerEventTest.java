package columns.model;

import columns.model.support.FakeModelListener;
import columns.model.support.FakePlatform;
import columns.model.support.FakeRandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameController.processEvent(GameEvent).
 *
 * processEvent is package-private; this test is in columns.model so it can
 * call it directly without any visibility change to production code.
 *
 * Each test:
 *   1. Constructs GameController with a FakePlatform (no real time, no real screen).
 *   2. Replaces board.listener with a FakeModelListener so View callbacks do not crash.
 *   3. Places a deterministic Figure at a known position.
 *   4. Calls processEvent() and asserts on board/figure state.
 */
class GameControllerEventTest {

    private GameController controller;
    private FakePlatform platform;
    private FakeModelListener listener;

    @BeforeEach
    void setUp() {
        platform = new FakePlatform();
        controller = new GameController(platform);

        // Re-wire the listener so View callbacks go to FakeModelListener instead of the
        // default GameController (which delegates to the real View/Screen).
        // GameController itself implements ModelListener and is already wired; we add
        // a second listener by wrapping — but the simplest approach is to keep GameController
        // as listener (it delegates to View → NoOpScreen which is safe) and just read
        // board state directly after each event.
        listener = new FakeModelListener();

        // Re-initialize the board with a clean state and a fake listener for Board callbacks.
        controller.board.initBoard();
        controller.board.setModelListener(listener);

        // Place a figure at a safe mid-board position for most tests.
        placeFigureAt(4, 5, 1, 2, 3);
    }

    /** Set board.figure to a figure with given position and colors. */
    private void placeFigureAt(int x, int y, int c1, int c2, int c3) {
        Figure f = new Figure(new FakeRandomGenerator(c1 - 1, c2 - 1, c3 - 1));
        f.x = x;
        f.y = y;
        controller.board.figure = f;
    }

    // =========================================================
    // LEFT event
    // =========================================================

    @Nested
    class LeftEvent {

        @Test
        void left_movesFigureLeftWhenClearAndNotAtWall() {
            placeFigureAt(4, 5, 1, 2, 3);
            int xBefore = controller.board.figure.x;
            controller.processEvent(GameEvent.LEFT);
            assertEquals(xBefore - 1, controller.board.figure.x);
        }

        @Test
        void left_doesNotMoveFigureWhenAtLeftWall() {
            placeFigureAt(1, 5, 1, 2, 3);
            controller.processEvent(GameEvent.LEFT);
            assertEquals(1, controller.board.figure.x);
        }

        @Test
        void left_doesNotMoveFigureWhenCellToLeftIsOccupied() {
            placeFigureAt(4, 5, 1, 2, 3);
            controller.board.newField[3][7] = 1;   // blocks the cell at x-1, y+2
            int xBefore = controller.board.figure.x;
            controller.processEvent(GameEvent.LEFT);
            assertEquals(xBefore, controller.board.figure.x);
        }

        @Test
        void left_clearsKeyPressedFlag() {
            platform.setKeyPressed(true);
            controller.processEvent(GameEvent.LEFT);
            assertFalse(platform.isKeyPressed());
        }
    }

    // =========================================================
    // RIGHT event
    // =========================================================

    @Nested
    class RightEvent {

        @Test
        void right_movesFigureRightWhenClearAndNotAtWall() {
            placeFigureAt(3, 5, 1, 2, 3);
            int xBefore = controller.board.figure.x;
            controller.processEvent(GameEvent.RIGHT);
            assertEquals(xBefore + 1, controller.board.figure.x);
        }

        @Test
        void right_doesNotMoveFigureWhenAtRightWall() {
            placeFigureAt(GameConfig.WIDTH, 5, 1, 2, 3);
            controller.processEvent(GameEvent.RIGHT);
            assertEquals(GameConfig.WIDTH, controller.board.figure.x);
        }

        @Test
        void right_doesNotMoveFigureWhenCellToRightIsOccupied() {
            placeFigureAt(3, 5, 1, 2, 3);
            controller.board.newField[4][7] = 1;   // blocks the cell at x+1, y+2
            int xBefore = controller.board.figure.x;
            controller.processEvent(GameEvent.RIGHT);
            assertEquals(xBefore, controller.board.figure.x);
        }
    }

    // =========================================================
    // UP (rotate up) event
    // =========================================================

    @Nested
    class RotateUpEvent {

        @Test
        void up_rotatesColorsUpward() {
            placeFigureAt(4, 5, 1, 2, 3);
            controller.processEvent(GameEvent.UP);
            assertAll(
                () -> assertEquals(2, controller.board.figure.c[1], "c[1] after UP"),
                () -> assertEquals(3, controller.board.figure.c[2], "c[2] after UP"),
                () -> assertEquals(1, controller.board.figure.c[3], "c[3] after UP")
            );
        }

        @Test
        void up_threeTimesRestoresOriginalColors() {
            placeFigureAt(4, 5, 1, 2, 3);
            controller.processEvent(GameEvent.UP);
            controller.processEvent(GameEvent.UP);
            controller.processEvent(GameEvent.UP);
            assertAll(
                () -> assertEquals(1, controller.board.figure.c[1]),
                () -> assertEquals(2, controller.board.figure.c[2]),
                () -> assertEquals(3, controller.board.figure.c[3])
            );
        }
    }

    // =========================================================
    // DOWN (rotate down) event
    // =========================================================

    @Nested
    class RotateDownEvent {

        @Test
        void down_rotatesColorsDownward() {
            placeFigureAt(4, 5, 1, 2, 3);
            controller.processEvent(GameEvent.DOWN);
            assertAll(
                () -> assertEquals(3, controller.board.figure.c[1], "c[1] after DOWN"),
                () -> assertEquals(1, controller.board.figure.c[2], "c[2] after DOWN"),
                () -> assertEquals(2, controller.board.figure.c[3], "c[3] after DOWN")
            );
        }

        @Test
        void down_threeTimesRestoresOriginalColors() {
            placeFigureAt(4, 5, 1, 2, 3);
            controller.processEvent(GameEvent.DOWN);
            controller.processEvent(GameEvent.DOWN);
            controller.processEvent(GameEvent.DOWN);
            assertAll(
                () -> assertEquals(1, controller.board.figure.c[1]),
                () -> assertEquals(2, controller.board.figure.c[2]),
                () -> assertEquals(3, controller.board.figure.c[3])
            );
        }
    }

    // =========================================================
    // DROP event
    // =========================================================

    @Nested
    class DropEvent {

        @Test
        void drop_movesFigureToLandingRow_onEmptyBoard() {
            placeFigureAt(3, 1, 1, 2, 3);
            controller.processEvent(GameEvent.DROP);
            // On empty board the figure should land at DEPTH-2
            assertEquals(GameConfig.DEPTH - 2, controller.board.figure.y,
                "figure must land at bottom of empty board");
        }

        @Test
        void drop_setsTcToZero() {
            platform.setTc(12345L);
            placeFigureAt(3, 1, 1, 2, 3);
            controller.processEvent(GameEvent.DROP);
            assertEquals(0L, platform.getTc(), "platform.tc must be 0 after drop");
        }

        @Test
        void drop_doesNotMoveFigureAlreadyNearBottom() {
            placeFigureAt(3, GameConfig.DEPTH - 2, 1, 2, 3);
            int yBefore = controller.board.figure.y;
            controller.processEvent(GameEvent.DROP);
            assertEquals(yBefore, controller.board.figure.y,
                "figure already near bottom must not move further");
        }
    }

    // =========================================================
    // LEVEL_UP event
    // =========================================================

    @Nested
    class LevelUpEvent {

        @Test
        void levelUp_incrementsLevelWhenBelowMax() {
            controller.board.level = 2;
            controller.processEvent(GameEvent.LEVEL_UP);
            assertEquals(3, controller.board.level);
        }

        @Test
        void levelUp_doesNotExceedMaxLevel() {
            controller.board.level = GameConfig.MAX_LEVEL;
            controller.processEvent(GameEvent.LEVEL_UP);
            assertEquals(GameConfig.MAX_LEVEL, controller.board.level);
        }

        @Test
        void levelUp_resetsFiguresMatchedCounter() {
            controller.board.figuresMatchedCounter = 10;
            controller.processEvent(GameEvent.LEVEL_UP);
            assertEquals(0, controller.board.figuresMatchedCounter);
        }
    }

    // =========================================================
    // LEVEL_DOWN event
    // =========================================================

    @Nested
    class LevelDownEvent {

        @Test
        void levelDown_decrementsLevelWhenAboveZero() {
            controller.board.level = 3;
            controller.processEvent(GameEvent.LEVEL_DOWN);
            assertEquals(2, controller.board.level);
        }

        @Test
        void levelDown_doesNotGoBelowZero() {
            controller.board.level = 0;
            controller.processEvent(GameEvent.LEVEL_DOWN);
            assertEquals(0, controller.board.level);
        }

        @Test
        void levelDown_resetsFiguresMatchedCounter() {
            controller.board.figuresMatchedCounter = 20;
            controller.processEvent(GameEvent.LEVEL_DOWN);
            assertEquals(0, controller.board.figuresMatchedCounter);
        }
    }

    // =========================================================
    // PAUSE event (bonus)
    // =========================================================

    @Nested
    class PauseEvent {

        @Test
        void pause_resumesWhenKeyIsPressed() {
            // FakePlatform.isKeyPressed() returns true after 1 call → breaks the pause loop
            platform.setKeyPressedAfterCalls(1);
            platform.setCurrentTime(5000L);
            // Should return without blocking
            assertDoesNotThrow(() -> controller.processEvent(GameEvent.PAUSE));
        }

        @Test
        void pause_updatesTcToCurrentTimeAfterResume() {
            platform.setKeyPressedAfterCalls(1);
            platform.setCurrentTime(7777L);
            controller.processEvent(GameEvent.PAUSE);
            assertEquals(7777L, platform.getTc(), "tc must be updated to currentTime after pause");
        }
    }
}
