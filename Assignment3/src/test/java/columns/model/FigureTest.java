package columns.model;

import columns.model.support.FakeRandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Figure movement and rotation.
 *
 * Figure is package-private; this test lives in the same package (columns.model)
 * so it can access Figure directly without any visibility change.
 *
 * FakeRandomGenerator supplies fixed color values:
 *   c[1]=1, c[2]=2, c[3]=3  (values 1..7 are produced as abs(n)%7+1)
 * The sequence 0,1,2 maps to: abs(0)%7+1=1, abs(1)%7+1=2, abs(2)%7+1=3.
 */
class FigureTest {

    private Figure figure;

    @BeforeEach
    void setUp() {
        // Sequence 0,1,2 → c[1]=1, c[2]=2, c[3]=3
        figure = new Figure(new FakeRandomGenerator(0, 1, 2));
    }

    // ---- movement ----

    @Test
    void moveRight_incrementsX() {
        int before = figure.x;
        figure.moveRight();
        assertEquals(before + 1, figure.x);
    }

    @Test
    void moveLeft_decrementsX() {
        int before = figure.x;
        figure.moveLeft();
        assertEquals(before - 1, figure.x);
    }

    @Test
    void moveDown_incrementsY() {
        int before = figure.y;
        figure.moveDown();
        assertEquals(before + 1, figure.y);
    }

    @Test
    void initialPosition_isMiddleColumnRow1() {
        assertEquals(GameConfig.WIDTH / 2 + 1, figure.x);
        assertEquals(1, figure.y);
    }

    // ---- rotation ----

    @Test
    void rotateUp_cyclesColorsForward() {
        // Before: c[1]=1, c[2]=2, c[3]=3
        figure.rotateUp();
        // After:  c[1]=2, c[2]=3, c[3]=1
        assertAll(
            () -> assertEquals(2, figure.c[1], "c[1] after rotate up"),
            () -> assertEquals(3, figure.c[2], "c[2] after rotate up"),
            () -> assertEquals(1, figure.c[3], "c[3] after rotate up")
        );
    }

    @Test
    void rotateDown_cyclesColorsBackward() {
        // Before: c[1]=1, c[2]=2, c[3]=3
        figure.rotateDown();
        // After:  c[1]=3, c[2]=1, c[3]=2
        assertAll(
            () -> assertEquals(3, figure.c[1], "c[1] after rotate down"),
            () -> assertEquals(1, figure.c[2], "c[2] after rotate down"),
            () -> assertEquals(2, figure.c[3], "c[3] after rotate down")
        );
    }

    @Test
    void threeRotateUps_restoreOriginalColors() {
        int c1 = figure.c[1], c2 = figure.c[2], c3 = figure.c[3];
        figure.rotateUp();
        figure.rotateUp();
        figure.rotateUp();
        assertAll(
            () -> assertEquals(c1, figure.c[1]),
            () -> assertEquals(c2, figure.c[2]),
            () -> assertEquals(c3, figure.c[3])
        );
    }

    @Test
    void threeRotateDowns_restoreOriginalColors() {
        int c1 = figure.c[1], c2 = figure.c[2], c3 = figure.c[3];
        figure.rotateDown();
        figure.rotateDown();
        figure.rotateDown();
        assertAll(
            () -> assertEquals(c1, figure.c[1]),
            () -> assertEquals(c2, figure.c[2]),
            () -> assertEquals(c3, figure.c[3])
        );
    }

    @Test
    void rotateUpThenDown_restoresOriginalColors() {
        int c1 = figure.c[1], c2 = figure.c[2], c3 = figure.c[3];
        figure.rotateUp();
        figure.rotateDown();
        assertAll(
            () -> assertEquals(c1, figure.c[1]),
            () -> assertEquals(c2, figure.c[2]),
            () -> assertEquals(c3, figure.c[3])
        );
    }

    @Test
    void colorSlot0_isAlwaysZero() {
        assertEquals(0, figure.c[0], "c[0] is reserved and must stay 0");
        figure.rotateUp();
        assertEquals(0, figure.c[0]);
        figure.rotateDown();
        assertEquals(0, figure.c[0]);
    }

    @Test
    void colorsAreInRange_1to7() {
        assertTrue(figure.c[1] >= 1 && figure.c[1] <= 7, "c[1] in range");
        assertTrue(figure.c[2] >= 1 && figure.c[2] <= 7, "c[2] in range");
        assertTrue(figure.c[3] >= 1 && figure.c[3] <= 7, "c[3] in range");
    }
}
