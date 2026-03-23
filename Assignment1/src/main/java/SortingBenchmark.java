package org.example.sortingbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 8)
@Fork(1)
@State(Scope.Thread)
public class SortingBenchmark {
    @Param({"UNIFORM_RANDOM", "ASCENDING", "DESCENDING", "NEARLY_SORTED"})
    public String distribution;

    @Param({"1000000"})
    public int n;

    @Param({"2000"})
    public int verifyN;

    private int[] base;
    private int[] work;

    @Setup(Level.Trial)
    public void setupTrial() {
        base = generateBase(distribution, n);
        work = new int[n];

        int m = Math.min(n, verifyN);
        int[] sample = Arrays.copyOf(base, m);

        int[] actual = Arrays.copyOf(sample, m);
        IntSorts.bubbleSort(actual);
        SortVerifier.assertCorrect(sample, actual);

        actual = Arrays.copyOf(sample, m);
        IntSorts.quickSort(actual);
        SortVerifier.assertCorrect(sample, actual);

        actual = Arrays.copyOf(sample, m);
        IntSorts.lsdRadixSortBase256(actual);
        SortVerifier.assertCorrect(sample, actual);

        actual = Arrays.copyOf(sample, m);
        Arrays.sort(actual);
        SortVerifier.assertCorrect(sample, actual);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        System.arraycopy(base, 0, work, 0, n);
    }

    @Benchmark
    public void bubbleSort(Blackhole blackhole) {
        IntSorts.bubbleSort(work);
        blackhole.consume(work[0]);
        blackhole.consume(work[n - 1]);
    }

    @Benchmark
    public void quickSort(Blackhole blackhole) {
        IntSorts.quickSort(work);
        blackhole.consume(work[0]);
        blackhole.consume(work[n - 1]);
    }

    @Benchmark
    public void lsdRadixSort(Blackhole blackhole) {
        IntSorts.lsdRadixSortBase256(work);
        blackhole.consume(work[0]);
        blackhole.consume(work[n - 1]);
    }

    @Benchmark
    public void arraysSort(Blackhole blackhole) {
        IntSorts.arraysSort(work);
        blackhole.consume(work[0]);
        blackhole.consume(work[n - 1]);
    }

    private static int[] generateBase(String distribution, int n) {
        if ("UNIFORM_RANDOM".equals(distribution)) {
            int[] a = new int[n];
            ThreadLocalRandom r = ThreadLocalRandom.current();
            for (int i = 0; i < n; i++) {
                a[i] = r.nextInt();
            }
            return a;
        }

        if ("ASCENDING".equals(distribution)) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = i;
            }
            return a;
        }

        if ("DESCENDING".equals(distribution)) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = n - 1 - i;
            }
            return a;
        }

        if ("NEARLY_SORTED".equals(distribution)) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = i;
            }
            int swaps = Math.max(1, (n / 100));
            ThreadLocalRandom r = ThreadLocalRandom.current();
            for (int s = 0; s < swaps; s++) {
                int i = r.nextInt(n);
                int j = r.nextInt(n);
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
            return a;
        }

        throw new IllegalArgumentException("Unknown distribution: " + distribution);
    }
}

