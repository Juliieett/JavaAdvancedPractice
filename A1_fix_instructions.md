The submission is already strong. The remaining improvement is benchmark practicality.

Action items:

- Split Bubble Sort into its own reduced-size benchmark path instead of benchmarking it at the full `1,000,000`-element scope.
- Keep the scalable algorithms at `1,000,000` elements and document the split scope explicitly in the report if you change it.

Recommended order:

1. Add a separate Bubble Sort benchmark size.
2. Re-run the benchmark.
3. Update the report so it matches the tracked benchmark configuration.
