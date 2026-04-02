package badsmells;

/*
 * Smell: Shotgun Surgery
 *
 * Problem: The course title was duplicated in Course, Invoice, and Certificate,
 * so renaming it required finding and updating all three places.
 *
 * Refactoring: Made Course the single owner of the title; Invoice and Certificate
 * now receive a Course object and read the title via getTitle().
 *
 * Result: The title exists in exactly one place, eliminating inconsistencies
 * when it changes (Low Coupling, Single Source of Truth).
 */
public class ShotgunSurgeryExample {

	static class Course {

		private final String title;

		Course(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public String label() {
			return "Course: " + title;
		}
	}

	static class Invoice {

		private final Course course;

		Invoice(Course course) {
			this.course = course;
		}

		public String description() {
			return "Invoice for " + course.getTitle();
		}
	}

	static class Certificate {

		private final Course course;

		Certificate(Course course) {
			this.course = course;
		}

		public String text() {
			return "Completed " + course.getTitle();
		}
	}

	public void clientCode() {
		Course course = new Course("Refactoring");
		Invoice invoice = new Invoice(course);
		Certificate certificate = new Certificate(course);

		System.out.println(course.label());
		System.out.println(invoice.description());
		System.out.println(certificate.text());
	}
}
