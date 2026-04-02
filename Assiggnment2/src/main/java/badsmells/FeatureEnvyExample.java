package badsmells;

/*
 * Smell: Feature Envy
 *
 * Problem: ScholarshipCalculator.qualifies() only used StudentAccount's data
 * and none of its own — the behavior lived far from the data it depended on.
 *
 * Refactoring: Moved the qualification logic into StudentAccount and removed
 * ScholarshipCalculator along with the getters it required.
 *
 * Result: Data and behavior are in the same class, improving cohesion and
 * following the "Tell, Don't Ask" principle.
 */
public class FeatureEnvyExample {

	static class StudentAccount {

		private final int completedCredits;
		private final double gpa;

		StudentAccount(int completedCredits, double gpa) {
			this.completedCredits = completedCredits;
			this.gpa = gpa;
		}

		public boolean qualifiesForScholarship() {
			return completedCredits >= 30 && gpa >= 3.7;
		}
	}

	public void clientCode() {
		StudentAccount account = new StudentAccount(36, 3.9);
		System.out.println(account.qualifiesForScholarship());
	}
}
