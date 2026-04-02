package badsmells;

/*
 * Smell: Long Parameter List
 *
 * Problem: registerStudent() took 12 separate parameters, making calls error-prone
 * and unreadable — it was easy to swap arguments like phone and email.
 *
 * Refactoring: Grouped related parameters into four objects (StudentContact, Address,
 * Guardian, Enrollment) so the method takes four clear arguments instead of twelve.
 *
 * Result: The signature is short and self-documenting, wrong-order mistakes are prevented,
 * and new fields only require changes in one object. (Encapsulation, Information Hiding)
 */
public class LongParameterListExample {

	static class StudentContact {
		final String firstName;
		final String lastName;
		final String email;
		final String phone;

		StudentContact(String firstName, String lastName, String email, String phone) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.phone = phone;
		}
	}

	static class Address {
		final String city;
		final String street;
		final String zipCode;

		Address(String city, String street, String zipCode) {
			this.city = city;
			this.street = street;
			this.zipCode = zipCode;
		}
	}

	static class Guardian {
		final String name;
		final String phone;

		Guardian(String name, String phone) {
			this.name = name;
			this.phone = phone;
		}
	}

	static class Enrollment {
		final String program;
		final int startYear;
		final boolean scholarship;

		Enrollment(String program, int startYear, boolean scholarship) {
			this.program = program;
			this.startYear = startYear;
			this.scholarship = scholarship;
		}
	}

	public String registerStudent(StudentContact student, Address address,
								  Guardian guardian, Enrollment enrollment) {
		return student.firstName + " " + student.lastName + " -> " + enrollment.program
			+ " (" + enrollment.startYear + "), guardian=" + guardian.name
			+ ", scholarship=" + enrollment.scholarship + ", address=" + address.city
			+ ", " + address.street + ", " + address.zipCode + ", contact="
			+ student.email + "/" + student.phone + ", guardianPhone=" + guardian.phone;
	}

	public void clientCode() {
		StudentContact student = new StudentContact("Nino", "Beridze", "nino@example.com", "+995-555-000-001");
		Address address = new Address("Tbilisi", "Rustaveli Ave 10", "0108");
		Guardian guardian = new Guardian("Maka Beridze", "+995-555-000-999");
		Enrollment enrollment = new Enrollment("Computer Science", 2026, true);

		String summary = registerStudent(student, address, guardian, enrollment);
		System.out.println(summary);
	}
}
