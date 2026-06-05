package columns.model;

import columns.model.support.FakeModelListener;
import columns.model.support.FakeRandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Board: boundary movement, paste/drop, match detection,
 * field packing, collapse, scoring, level progression, and game-over detection.
 *
 * Board is package-private; this test is in the same package (columns.model).
 * Tests access Board fields directly because they are package-private or public.
 */
class BoardTest {

    private Board board;
    private FakeModelListener listener;

    /** Create a fresh, empty Board with a FakeModelListener wired in. */
    @BeforeEach
    void setUp() {
        board = new Board();
        listener = new FakeModelListener();
        board.setModelListener(listener);
        board.initFields();
        board.initBoard();
        // Place a default figure in the centre so movement checks have a figure.
        board.figure = makeFigure(1, 2, 3);
    }

    /** Helper: create a Figure with fixed colors c[1]=a, c[2]=b, c[3]=c at default x/y. */
    private Figure makeFigure(int a, int b, int c) {
        // Sequence a-1, b-1, c-1 maps to abs(n)%7+1 = n+1 when n>=0
        return new Figure(new FakeRandomGenerator(a - 1, b - 1, c - 1));
    }

    // =========================================================
    // initBoard
    // =========================================================

    @Nested
    class InitBoard {

        @Test
        void initBoard_clearsAllFieldCells() {
            // Dirty the board first
            board.newField[2][5] = 3;
            board.newField[5][12] = 7;
            board.initBoard();
            for (int col = 0; col <= GameConfig.WIDTH; col++) {
                for (int row = 0; row <= GameConfig.DEPTH; row++) {
                    assertEquals(0, board.newField[col][row],
                        "newField[" + col + "][" + row + "] must be 0 after initBoard");
                }
            }
        }

        @Test
        void initBoard_resetsScoreToZero() {
            board.Score = 9999L;
            board.initBoard();
            assertEquals(0L, board.Score);
        }

        @Test
        void initBoard_resetsLevelToZero() {
            board.level = 5;
            board.initBoard();
            assertEquals(0, board.level);
        }

        @Test
        void initBoard_resetsFiguresMatchedCounterToZero() {
            board.figuresMatchedCounter = 25;
            board.initBoard();
            assertEquals(0, board.figuresMatchedCounter);
        }
    }

    // =========================================================
    // Boundary movement
    // =========================================================

    @Nested
    class BoundaryMovement {

        @Test
        void canMoveLeft_falseAtLeftWall() {
            board.figure.x = 1;
            assertFalse(board.canMoveLeft());
        }

        @Test
        void canMoveLeft_falseWhenCellToLeftIsOccupied() {
            board.figure.x = 3;
            board.figure.y = 5;
            board.newField[2][7] = 1;   // column x-1=2, row y+2=7
            assertFalse(board.canMoveLeft());
        }

        @Test
        void canMoveLeft_trueWhenClearAndNotAtWall() {
            board.figure.x = 3;
            board.figure.y = 5;
            // newField[2][7] is 0 (empty board)
            assertTrue(board.canMoveLeft());
        }

        @Test
        void canMoveRight_falseAtRightWall() {
            board.figure.x = GameConfig.WIDTH;
            assertFalse(board.canMoveRight());
        }

        @Test
        void canMoveRight_falseWhenCellToRightIsOccupied() {
            board.figure.x = 3;
            board.figure.y = 5;
            board.newField[4][7] = 1;   // column x+1=4, row y+2=7
            assertFalse(board.canMoveRight());
        }

        @Test
        void canMoveRight_trueWhenClearAndNotAtWall() {
            board.figure.x = 3;
            board.figure.y = 5;
            assertTrue(board.canMoveRight());
        }

        @Test
        void figureMayMoveDown_falseWhenCellBelowIsOccupied() {
            board.figure.x = 3;
            board.figure.y = 5;
            board.newField[3][8] = 1;   // row y+3=8
            assertFalse(board.figureMayMoveDown());
        }

        @Test
        void figureMayMoveDown_falseWhenFigureIsAtNearBottomBoundary() {
            board.figure.y = GameConfig.DEPTH - 2;
            assertFalse(board.figureMayMoveDown());
        }

        @Test
        void figureMayMoveDown_trueWhenClearAndNotNearBottom() {
            board.figure.x = 3;
            board.figure.y = 1;
            assertTrue(board.figureMayMoveDown());
        }
    }

    // =========================================================
    // Paste and drop
    // =========================================================

    @Nested
    class PasteAndDrop {

        @Test
        void pasteFigure_writesFigureColorsToField() {
            board.figure = makeFigure(2, 4, 6);
            board.figure.x = 3;
            board.figure.y = 4;
            board.pasteFigure(board.figure);
            assertAll(
                () -> assertEquals(2, board.newField[3][4], "c[1] at y"),
                () -> assertEquals(4, board.newField[3][5], "c[2] at y+1"),
                () -> assertEquals(6, board.newField[3][6], "c[3] at y+2")
            );
        }

        @Test
        void pasteFigure_doesNotWriteToOtherColumns() {
            board.figure = makeFigure(2, 4, 6);
            board.figure.x = 3;
            board.figure.y = 4;
            board.pasteFigure(board.figure);
            assertEquals(0, board.newField[2][4], "adjacent column should be untouched");
            assertEquals(0, board.newField[4][4], "adjacent column should be untouched");
        }

        @Test
        void dropFigure_landsFigureAboveBottomOfEmptyColumn() {
            board.figure.x = 3;
            board.figure.y = 1;
            board.dropFigure(board.figure);
            // In an empty board, lowest empty row is DEPTH=15; figure.y = 15-2 = 13
            assertEquals(GameConfig.DEPTH - 2, board.figure.y);
        }

        @Test
        void dropFigure_landsFigureAboveExistingStack() {
            board.figure.x = 3;
            board.figure.y = 1;
            // Put blocks at the bottom rows of column 3
            board.newField[3][GameConfig.DEPTH]     = 1;
            board.newField[3][GameConfig.DEPTH - 1] = 1;
            board.dropFigure(board.figure);
            // Lowest free row is DEPTH-2; figure.y = (DEPTH-2)-2 = DEPTH-4
            assertEquals(GameConfig.DEPTH - 4, board.figure.y);
        }

        @Test
        void dropFigure_doesNothingWhenFigureAlreadyNearBottom() {
            board.figure.x = 3;
            board.figure.y = GameConfig.DEPTH - 2;   // already at boundary
            board.dropFigure(board.figure);
            assertEquals(GameConfig.DEPTH - 2, board.figure.y, "y must not change");
        }
    }

    // =========================================================
    // Match detection
    // =========================================================

    @Nested
    class MatchDetection {

        @Test
        void findMatches_noChangeWhenAllCellsDifferent() {
            board.newField[3][5] = 1;
            board.newField[3][6] = 2;
            board.newField[3][7] = 3;
            board.findMatches();
            assertTrue(board.noChanges, "noChanges must remain true when no triplet exists");
            assertEquals(0, board.Score);
        }

        @Test
        void findMatches_detectsVerticalTriplet() {
            // Three identical colors stacked in column 3, rows 5-7.
            // Only the center cell (row 6) has both a matching cell above and below,
            // so checkNeighbours fires exactly once: score = (level=0 + 1) * 10 = 10.
            board.newField[3][5] = 2;
            board.newField[3][6] = 2;
            board.newField[3][7] = 2;
            board.findMatches();
            assertFalse(board.noChanges, "noChanges must be false when vertical triplet found");
            assertEquals(10L, board.Score, "one triplet at level 0 must add exactly 10 to score");
        }

        @Test
        void findMatches_detectsHorizontalTriplet() {
            // Three identical colors in a row across columns 2-4.
            // Center cell (col 3) triggers the horizontal neighbour check exactly once.
            int row = 8;
            board.newField[2][row] = 3;
            board.newField[3][row] = 3;
            board.newField[4][row] = 3;
            board.findMatches();
            assertFalse(board.noChanges, "noChanges must be false when horizontal triplet found");
            assertEquals(10L, board.Score, "one horizontal triplet at level 0 must add exactly 10");
        }

        @Test
        void findMatches_detectsDiagonalTriplet() {
            // Down-right diagonal: (col=2,row=6), (col=3,row=7), (col=4,row=8).
            // Center cell (col 3, row 7) triggers the diagonal neighbour check exactly once.
            board.newField[2][6] = 5;
            board.newField[3][7] = 5;
            board.newField[4][8] = 5;
            board.findMatches();
            assertFalse(board.noChanges, "noChanges must be false when diagonal triplet found");
            assertEquals(10L, board.Score, "one diagonal triplet at level 0 must add exactly 10");
        }

        @Test
        void findMatches_scoreScalesWithLevel() {
            // At level 2: score = (2 + 1) * 10 = 30 for one triplet.
            board.level = 2;
            board.newField[3][5] = 2;
            board.newField[3][6] = 2;
            board.newField[3][7] = 2;
            board.findMatches();
            assertEquals(30L, board.Score, "one triplet at level 2 must add exactly 30 to score");
        }

        @Test
        void findMatches_notifiesListenerOnTriplet() {
            board.newField[3][5] = 4;
            board.newField[3][6] = 4;
            board.newField[3][7] = 4;
            board.findMatches();
            assertTrue(listener.tripletDetectedCount > 0, "listener must be notified of triplet");
        }

        @Test
        void findMatches_noListenerNotificationWhenNoMatch() {
            board.newField[3][5] = 1;
            board.newField[3][6] = 2;
            board.newField[3][7] = 3;
            board.findMatches();
            assertEquals(0, listener.tripletDetectedCount);
        }

        @Test
        void findMatches_incrementsFiguresMatchedCounter() {
            board.newField[3][5] = 2;
            board.newField[3][6] = 2;
            board.newField[3][7] = 2;
            board.findMatches();
            assertTrue(board.figuresMatchedCounter > 0);
        }
    }

    // =========================================================
    // Field packing (collapse support)
    // =========================================================

    @Nested
    class FieldPacking {

        @Test
        void packField_emptyFieldRemainsEmpty() {
            board.copyFieldsNew2Old();
            board.packField();
            for (int col = 1; col <= GameConfig.WIDTH; col++) {
                for (int row = 1; row <= GameConfig.DEPTH; row++) {
                    assertEquals(0, board.newField[col][row],
                        "cell [" + col + "][" + row + "] must stay 0");
                }
            }
        }

        @Test
        void packField_compactsNonZeroCellsToBottom() {
            // Place a single block in the middle of column 2
            board.newField[2][5] = 7;
            board.copyFieldsNew2Old();
            board.packField();
            // The block must now be at the bottom row
            assertEquals(7, board.newField[2][GameConfig.DEPTH],
                "non-zero cell must fall to bottom row");
            // All rows above it in column 2 must be 0
            for (int row = 1; row < GameConfig.DEPTH; row++) {
                assertEquals(0, board.newField[2][row],
                    "row " + row + " of column 2 must be 0 after pack");
            }
        }

        @Test
        void packField_preservesOrderOfMultipleCellsInColumn() {
            // Stack two different colors in column 4
            board.newField[4][3] = 1;   // top block
            board.newField[4][7] = 2;   // lower block
            board.copyFieldsNew2Old();
            board.packField();
            // After pack: color 2 should be at bottom, color 1 just above it
            assertEquals(2, board.newField[4][GameConfig.DEPTH],
                "lower block (color 2) must end up at bottom");
            assertEquals(1, board.newField[4][GameConfig.DEPTH - 1],
                "upper block (color 1) must end up just above the lower block");
        }
    }

    // =========================================================
    // Collapse
    // =========================================================

    @Nested
    class CollapseTests {

        @Test
        void collapse_addsDScoreToTotalScore() {
            board.DScore = 15;
            board.Score = 0;
            board.copyFieldsNew2Old();   // make oldField == newField (empty)
            board.collapse();
            assertEquals(15, board.Score, "DScore must be added to Score during collapse");
        }

        @Test
        void collapse_firesFieldWasUpdated() {
            board.copyFieldsNew2Old();
            board.collapse();
            assertEquals(1, listener.fieldUpdatedCount, "fieldWasUpdated must be called once");
        }

        @Test
        void collapse_firesScoreUpdated() {
            board.DScore = 5;
            board.copyFieldsNew2Old();
            board.collapse();
            assertFalse(listener.scoresUpdated.isEmpty(), "scoreUpdated must be called");
            assertEquals(5L, listener.scoresUpdated.get(0));
        }

        @Test
        void collapse_doesNotFireLevelChangedWhenCounterBelowThreshold() {
            board.figuresMatchedCounter = 0;
            board.copyFieldsNew2Old();
            board.collapse();
            assertTrue(listener.levelsChanged.isEmpty(),
                "levelHasChanged must not fire below threshold");
        }
    }

    // =========================================================
    // Level progression
    // =========================================================

    @Nested
    class LevelProgression {

        @Test
        void changeLevelIfNeeded_incrementsLevelWhenThresholdReached() {
            board.level = 0;
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
            board.changeLevelIfNeeded();
            assertEquals(1, board.level);
        }

        @Test
        void changeLevelIfNeeded_resetsCounterAfterLevelUp() {
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
            board.changeLevelIfNeeded();
            assertEquals(0, board.figuresMatchedCounter);
        }

        @Test
        void changeLevelIfNeeded_notifiesListenerWithNewLevel() {
            board.level = 2;
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
            board.changeLevelIfNeeded();
            assertFalse(listener.levelsChanged.isEmpty());
            assertEquals(3, listener.levelsChanged.get(0));
        }

        @Test
        void changeLevelIfNeeded_doesNotExceedMaxLevel() {
            board.level = GameConfig.MAX_LEVEL;
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
            board.changeLevelIfNeeded();
            assertEquals(GameConfig.MAX_LEVEL, board.level,
                "level must not exceed MAX_LEVEL");
        }

        @Test
        void changeLevelIfNeeded_doesNothingBelowThreshold() {
            board.level = 0;
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD - 1;
            board.changeLevelIfNeeded();
            assertEquals(0, board.level);
            assertTrue(listener.levelsChanged.isEmpty());
        }

        @Test
        void changeLevelIfNeeded_firesLevelChangedEvenAtMaxLevel() {
            // When already at MAX_LEVEL the level stays capped, but levelHasChanged
            // is still fired so the view stays in sync.
            board.level = GameConfig.MAX_LEVEL;
            board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
            board.changeLevelIfNeeded();
            assertFalse(listener.levelsChanged.isEmpty(),
                "levelHasChanged must fire even when level is already at MAX_LEVEL");
            assertEquals(GameConfig.MAX_LEVEL, listener.levelsChanged.get(0));
        }
    }

    // =========================================================
    // Game-over detection
    // =========================================================

    @Nested
    class GameOverDetection {

        @Test
        void isFieldFull_falseWhenBoardIsEmpty() {
            assertFalse(board.isFieldFull());
        }

        @Test
        void isFieldFull_trueWhenRow3HasOccupiedCell() {
            board.newField[1][3] = 2;
            assertTrue(board.isFieldFull());
        }

        @Test
        void isFieldFull_trueWhenAnyColumnRow3IsOccupied() {
            board.newField[GameConfig.WIDTH][3] = 1;
            assertTrue(board.isFieldFull());
        }

        @Test
        void isFieldFull_falseWhenOccupiedCellsAreOnlyBelowRow3() {
            // Rows 4..DEPTH may be occupied; that alone is not game over
            for (int col = 1; col <= GameConfig.WIDTH; col++) {
                for (int row = 4; row <= GameConfig.DEPTH; row++) {
                    board.newField[col][row] = 1;
                }
            }
            assertFalse(board.isFieldFull());
        }
    }
}
