## Summary

This is a strong Assignment 1 submission with real JMH usage, all required input distributions, explicit correctness verification, a compliant radix implementation, and a PDF report.

## Strengths

- `Assignment1/pom.xml` contains real JMH dependencies and shade-plugin packaging.
- `Assignment1/src/main/java/SortingBenchmark.java` benchmarks Bubble Sort, Quick Sort, LSD Radix Sort, and `Arrays.sort()` across all four required distributions.
- `Assignment1/src/main/java/IntSorts.java` includes Bubble Sort with early exit, in-place Quick Sort with a documented median-of-three pivot strategy, and a base-`256`, four-pass signed-int LSD radix sort.
- `Assignment1/src/main/java/SortingBenchmark.java` explicitly verifies correctness in `@Setup(Level.Trial)` using `SortVerifier.assertCorrect(...)`.
- `Assignment1/report.pdf` is present.

## Findings

- None.

## Requirement Checklist

- Java source code: Present
- Real JMH benchmark source: Present
- Build descriptor operational as submitted: Present
- PDF analytical report: Present
- Bubble Sort with early exit: Present
- In-place Quick Sort with identifiable pivot strategy: Present
- LSD Radix Sort, base `256`, `4` passes, negative support: Present
- `Arrays.sort(int[])` benchmark/reference: Present
- Uniform random dataset: Present
- Sorted dataset: Present
- Reverse-sorted dataset: Present
- Nearly sorted dataset with about `1%` swaps: Present
- Correctness check against `Arrays.sort()`: Present
- Explicit sortedness check: Present

## Verdict

Meets requirements.
