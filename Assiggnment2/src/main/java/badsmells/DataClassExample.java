package badsmells;

/*
 * Smell: Data Class
 *
 * Problem: StudentRecord was a dumb container with public fields and no behavior;
 * three external helper classes did all the work by reaching into its data.
 *
 * Refactoring: Made fields private/final, and moved isHonorsEligible(),
 * tuitionDiscountPercent(), and academicStanding() into StudentRecord itself.
 *
 * Result: The class now owns its behavior ("tell, don't ask"), applying
 * Encapsulation and High Cohesion while eliminating Feature Envy.
 */
public class DataClassExample {

	public static class StudentRecord {

		private final String name;
		private final int credits;
		private final double gpa;

		public StudentRecord(String name, int credits, double gpa) {
			this.name = name;
			this.credits = credits;
			this.gpa = gpa;
		}

		public boolean isHonorsEligible() {
			return credits >= 30 && gpa >= 3.7;
		}

		public double tuitionDiscountPercent() {
			if (gpa >= 3.8) {
				return 0.15;
			}
			if (gpa >= 3.5) {
				return 0.10;
			}
			return 0.0;
		}

		public String academicStanding() {
			if (gpa < 2.0) {
				return name + " is on academic probation";
			}
			if (credits < 15) {
				return name + " is a new student";
			}
			return name + " is in good standing";
		}
	}

	public void clientCode() {
		StudentRecord student = new StudentRecord("Nino", 32, 3.8);

		System.out.println(student.isHonorsEligible());
		System.out.println(student.tuitionDiscountPercent());
		System.out.println(student.academicStanding());
	}
}