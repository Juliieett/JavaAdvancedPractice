package columns.model.support;

import columns.model.kernel.ModelListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Records every callback fired by Board so tests can assert on what happened.
 */
public class FakeModelListener implements ModelListener {

    public final List<Integer> levelsChanged = new ArrayList<>();
    public int tripletDetectedCount = 0;
    public int fieldUpdatedCount = 0;
    public final List<Long> scoresUpdated = new ArrayList<>();

    @Override
    public void levelHasChanged(int level) {
        levelsChanged.add(level);
    }

    @Override
    public void tripletDetected(int a, int b, int c, int d, int i, int j) {
        tripletDetectedCount++;
    }

    @Override
    public void fieldWasUpdated(int[][] newField) {
        fieldUpdatedCount++;
    }

    @Override
    public void scoreUpdated(long score) {
        scoresUpdated.add(score);
    }
}
