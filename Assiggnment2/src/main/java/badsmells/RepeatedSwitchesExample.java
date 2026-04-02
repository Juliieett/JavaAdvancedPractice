package badsmells;

/*
 * Smell: Repeated Switches
 *
 * Problem: The same switch on studentType was duplicated in tuitionDiscount() and
 * dormPriority(), so adding a new type required updating every switch in sync.
 *
 * Refactoring: Replaced both switches with a StudentType enum whose constants
 * each carry their own discount and priority values.
 *
 * Result: Adding a new type means adding one enum constant in one place; the
 * compiler enforces completeness (Open/Closed Principle, Polymorphism).
 */
public class RepeatedSwitchesExample {

	enum StudentType {
		STUDENT(0.05, "NORMAL"),
		ATHLETE(0.15, "HIGH"),
		EMPLOYEE_CHILD(0.25, "LOW");

		private final double tuitionDiscount;
		private final String dormPriority;

		StudentType(double tuitionDiscount, String dormPriority) {
			this.tuitionDiscount = tuitionDiscount;
			this.dormPriority = dormPriority;
		}

		public double tuitionDiscount() {
			return tuitionDiscount;
		}

		public String dormPriority() {
			return dormPriority;
		}
	}

	public void clientCode() {
		StudentType type = StudentType.ATHLETE;
		System.out.println(type.tuitionDiscount());
		System.out.println(type.dormPriority());
	}
}
