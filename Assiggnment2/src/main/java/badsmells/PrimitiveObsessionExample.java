package badsmells;

/*
 * Smell: Primitive Obsession
 *
 * Problem: Domain concepts (status, country) were plain Strings, allowing typos
 * and invalid values that the compiler could not catch.
 *
 * Refactoring: Replaced raw Strings with StudentStatus and CountryCode enums,
 * and grouped student data into a DormApplicant class with its own eligibility logic.
 *
 * Result: Invalid values are now impossible at compile time, and validation lives
 * inside the types themselves (Abstraction, Encapsulation).
 */
public class PrimitiveObsessionExample {

	enum StudentStatus {
		ACTIVE, BLOCKED
	}

	enum CountryCode {
		GE, US
	}

	static class DormApplicant {
		private final int age;
		private final StudentStatus status;
		private final double unpaidBalance;
		private final CountryCode country;

		DormApplicant(int age, StudentStatus status, double unpaidBalance, CountryCode country) {
			this.age = age;
			this.status = status;
			this.unpaidBalance = unpaidBalance;
			this.country = country;
		}

		public boolean canRentDormRoom() {
			return age >= 18
				&& status == StudentStatus.ACTIVE
				&& unpaidBalance < 100
				&& country == CountryCode.GE;
		}
	}

	public void clientCode() {
		DormApplicant nino = new DormApplicant(19, StudentStatus.ACTIVE, 0.0, CountryCode.GE);
		DormApplicant john = new DormApplicant(17, StudentStatus.BLOCKED, 120.0, CountryCode.US);

		System.out.println(nino.canRentDormRoom());
		System.out.println(john.canRentDormRoom());
	}
}
