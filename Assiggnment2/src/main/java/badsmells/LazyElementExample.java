package badsmells;

/*
 * Smell: Lazy Element
 *
 * Problem: StudentNameFormatter was an entire class whose only method just
 * called name.trim() — it added structure without adding value.
 *
 * Refactoring: Removed the class and inlined the .trim() call at the usage
 * site.
 *
 * Result: Less indirection and fewer files to maintain, following the YAGNI
 * and simplicity principles.
 */
public class LazyElementExample {

	public void clientCode() {
		System.out.println("  Nino  ".trim());
	}
}
