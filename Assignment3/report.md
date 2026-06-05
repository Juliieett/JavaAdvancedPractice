# Assignment 3 – Unit Test Report

## 1. Test Framework

I used **JUnit 5** (junit-jupiter 5.10.2) with **Maven** as the build tool. I also configured **JaCoCo 0.8.11** for coverage reporting as part of the bonus track.

To run the tests:
```
mvnd test
```
The coverage report is generated automatically at `target/site/jacoco/index.html` after each test run.

---

## 2. What I Tested

I organised my tests around three main areas: `Figure` behavior, `Board` behavior, and `GameController` event handling. I tried to focus on meaningful behavior rather than just calling methods and checking they didn't crash.

### Figure

I started with `Figure` because it has no dependencies — it just needs a `RandomGenerator`, which I replaced with a fake that returns fixed values. I tested:

- `moveLeft`, `moveRight`, `moveDown` each change the coordinate by exactly 1
- `rotateUp` cycles colors forward: `[1,2,3]` becomes `[2,3,1]`
- `rotateDown` cycles colors backward: `[1,2,3]` becomes `[3,1,2]`
- Doing the same rotation 3 times in a row restores the original colors
- `rotateUp` followed by `rotateDown` also restores the original (they are inverses)
- The `c[0]` slot is always 0 and is never touched by rotation

### Board — Initialization

I verified that `initBoard` actually resets everything it is supposed to: all field cells become 0, and `Score`, `level`, and `figuresMatchedCounter` all go back to zero even if they were non-zero beforehand.

### Board — Movement Boundaries

I tested all three boundary checks the board uses:

- `canMoveLeft` returns false at the left wall (`x == 1`) and when the cell to the left is occupied, but true when the path is clear
- `canMoveRight` returns false at the right wall (`x == WIDTH`) and when blocked, true otherwise
- `figureMayMoveDown` returns false when the figure is at the bottom boundary (`y == DEPTH-2`) and when the cell below is occupied

### Board — Paste and Drop

- `pasteFigure` writes the three figure colors into exactly the right cells (`newField[x][y]`, `[y+1]`, `[y+2]`) and does not touch adjacent columns
- `dropFigure` on an empty board lands the figure at `DEPTH-2` (the lowest valid position)
- `dropFigure` with blocks already at the bottom stops the figure just above them
- `dropFigure` does nothing if the figure is already near the bottom boundary

### Board — Match Detection

I placed cells directly into `newField` to test each match direction. For a single triplet at level 0, the score formula gives `(0+1) * 10 = 10`, and because `checkNeighbours` only fires once per triplet (from the center cell), I was able to assert the exact score:

- Vertical triplet → `noChanges == false`, `Score == 10`
- Horizontal triplet → `noChanges == false`, `Score == 10`
- Diagonal triplet → `noChanges == false`, `Score == 10`
- No triplet (three different colors) → `noChanges` stays `true`, `Score` stays `0`
- At level 2 one triplet adds exactly `(2+1) * 10 = 30`

I also checked that the `ModelListener` is notified when a match is found and that `figuresMatchedCounter` increases.

### Board — Field Packing

- An empty field stays empty after `packField`
- A single block in the middle of a column ends up at the bottom row after packing
- Two blocks with different colors maintain their relative order after compaction

### Board — Collapse and Scoring

- After a collapse, `DScore` is added to `Score`
- `fieldWasUpdated` and `scoreUpdated` are both called on the listener
- `levelHasChanged` is not called when `figuresMatchedCounter` is below the threshold

### Board — Level Progression

- When `figuresMatchedCounter` reaches 33, level increments by 1
- The counter resets to 0 after a level-up
- The listener receives `levelHasChanged` with the new level value
- Level never exceeds `MAX_LEVEL` (7)
- Even when already at `MAX_LEVEL`, the listener still fires (so the view stays consistent)
- Nothing happens when the counter is below the threshold

### Board — Game-Over Detection

- An empty board is not full
- Any cell in row 3 being occupied means the field is full
- Cells only in rows 4–15 do not trigger game-over

### GameController Events

For controller tests I constructed a `GameController` using `FakePlatform`, which has no real screen, no real sleep, and gives me full control over time and key events. I then called `processEvent()` directly (it is package-private, so placing my test in `columns.model` gives access without any code change).

| Event | What I verified |
|---|---|
| `LEFT` – open path | `figure.x` decreases by 1 |
| `LEFT` – at wall | `figure.x` unchanged |
| `LEFT` – cell blocked | `figure.x` unchanged |
| `LEFT` | `isKeyPressed` flag is cleared |
| `RIGHT` – open path | `figure.x` increases by 1 |
| `RIGHT` – at wall or blocked | `figure.x` unchanged |
| `UP` | Colors cycle forward; 3 times restores original |
| `DOWN` | Colors cycle backward; 3 times restores original |
| `DROP` on empty board | Figure lands at `DEPTH-2`; `platform.tc` is set to 0 |
| `DROP` near bottom | `figure.y` does not change |
| `LEVEL_UP` | Level increments; capped at `MAX_LEVEL`; counter reset to 0 |
| `LEVEL_DOWN` | Level decrements; floored at 0; counter reset to 0 |
| `PAUSE` | Loop exits when `isKeyPressed` becomes true; `tc` updated to `currentTime()` |

**Total: 76 tests, 0 failures, 0 errors.**

---

## 3. Test Doubles

I wrote all four test doubles by hand — no mocking library.

**`FakeRandomGenerator`**  
Returns a fixed sequence of integers that I supply in the constructor. This makes `Figure` construction fully deterministic. For example, passing `(0, 1, 2)` gives `c[1]=1, c[2]=2, c[3]=3` because the formula is `abs(n) % 7 + 1`.

**`FakeModelListener`**  
Records every callback from `Board` into simple lists and counters (`levelsChanged`, `tripletDetectedCount`, `fieldUpdatedCount`, `scoresUpdated`). Tests assert on these values directly.

**`NoOpScreen`**  
Implements all `Screen` methods as empty stubs. This lets `View` and `GameController` make drawing calls without needing a real graphics context. `Black()` returns 0 and `White()` returns 8 so color comparisons still work.

**`FakePlatform`**  
Implements `Platform` with full test control. `delay()` does nothing so tests run instantly. `currentTime()` returns a fixed value I can set. `isKeyPressed()` has a mode where it returns true after a given number of calls — I used this to break the `PAUSE` loop after one iteration without changing any production code.

---

## 4. What Was Hard to Test

**`runGameLoop`** — The main game loop is the hardest part to test. It calls `platform.delay(50)` inside a busy-wait and only terminates when `isFieldFull()` returns `true`. Driving it with fakes would require simulating many game ticks. Instead, I tested all the behaviors it orchestrates (figure movement, matching, collapsing, scoring) separately through `Board` and `Figure` directly.

**`checkTimeAndMoveDownIfNeeded`** — This method compares `currentTime() - getTc()` against a time threshold. I could have tested it by setting up `FakePlatform` so the difference is just above the threshold, but the individual behaviors it triggers (`moveDown`, `drawFigure`) are already tested through other paths, so I left it out rather than writing a test that depends heavily on implementation timing details.

**`View`** — `View` only translates board coordinates into screen draw calls. There is no logic to verify, just math. I did not write direct tests for it. It ends up at ~49% coverage because the controller event tests indirectly call `drawFigure`, `hideFigure`, `showLevel`, and `showScore` through `NoOpScreen`.

---

## 5. Design Problems the Tests Revealed

**`Board` has no encapsulation.** Almost every field is `public`: `newField`, `figure`, `Score`, `level`, `DScore`, `figuresMatchedCounter`, `noChanges`. Having them accessible made tests easy to write, but it also means any part of the code can modify them freely with no protection. There are no invariants enforced.

**The game loop and timing are not separable.** `processUserEventsIfAny` and `checkTimeAndMoveDownIfNeeded` both embed timing logic inline using `platform.currentTime()`. This tightly couples the event loop to time, making it difficult to test event handling independently of the clock. A cleaner design would put the tick decision in one place.

**`Board` requires a three-step setup.** Before any `Board` method is safe to call, the test must call `initFields()`, then `initBoard()`, then `setModelListener()`. If any step is missed, a `NullPointerException` follows. A constructor that takes a `ModelListener` and initialises everything in one step would prevent this.

**`checkNeighbours` has a confusing signature.** The parameters are `(int a, int b, int c, int d, int i, int j)` where `j` is the column and `i` is the row of the current cell, while `(a, b)` and `(c, d)` are the neighbours. The reversed `i`/`j` naming required careful reading to get the diagonal test inputs right.

**`DScore` formula is unpredictable.** The formula `(((level+1) * (DEPTH*2 - f.y - zz) * 2) % 5) * 5` can produce 0 for large drops because of the `% 5`. This makes it impossible to predict the drop bonus without working through the formula, so my collapse test only verifies that `DScore` is added to `Score` rather than checking a specific value.

---

## 6. Production Code Changes

**None.** I did not modify any production source file.

All the classes I needed to test (`Figure`, `Board`, `GameConfig`, `GameController`, `GameEvent`) are in the `columns.model` package. By placing my test classes in the same package, I got direct access to all package-private types and to `GameController.processEvent()` without making anything public. The `FakePlatform` returning a `NoOpScreen` satisfies the `GameController` constructor without any changes to `View` or `GameController`.

---

## 7. Coverage (Bonus)

JaCoCo is configured in `pom.xml` and runs automatically with every `mvnd test`. The HTML report is at `target/site/jacoco/index.html`.

| Class | Instructions | Lines | Methods |
|---|---|---|---|
| `Figure` | 100% | 100% | 100% |
| `Board` | 100% | 100% | 100% |
| `GameEvent` | 100% | 100% | 100% |
| `GameController` | 45% | 47% | 18% |
| `View` | 49% | 52% | 73% |
| `Columns` (applet) | 0% | 0% | 0% |
| `AppletScreen` | 0% | 0% | 0% |

`Figure` and `Board` hit 100% because those are the core model classes and all their methods have direct tests. `GameController` and `View` are partially covered because the game loop and rendering paths are not unit-tested (explained in section 4). `Columns` and `AppletScreen` are the applet layer, which the assignment says not to test.
