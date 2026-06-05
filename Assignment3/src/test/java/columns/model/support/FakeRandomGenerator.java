package columns.model.support;

import columns.model.kernel.RandomGenerator;

/**
 * Returns a fixed, repeating sequence of integers.
 * Eliminates randomness from Figure construction so tests are deterministic.
 */
public class FakeRandomGenerator implements RandomGenerator {

    private final int[] values;
    private int index = 0;

    public FakeRandomGenerator(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Must provide at least one value");
        }
        this.values = values;
    }

    @Override
    public int nextInt() {
        int v = values[index];
        index = (index + 1) % values.length;
        return v;
    }
}
