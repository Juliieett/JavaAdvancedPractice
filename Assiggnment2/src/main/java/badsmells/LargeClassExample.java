package badsmells;

import java.util.ArrayList;
import java.util.List;

/*
 * Smell: Large Class
 *
 * Problem: A single class handled enrollment, finance, campus services, and the
 * website — 10 fields and 10 methods with no relation to each other.
 *
 * Refactoring: Split into four focused classes (Enrollment, Finance,
 * CampusServices, Website), each owning only its own fields and methods.
 *
 * Result: Each class has a single responsibility and high cohesion, so changes
 * in one area cannot break another (Single Responsibility Principle).
 */
public class LargeClassExample {

	static class Enrollment {
		private final List<String> students = new ArrayList<>();
		private final List<String> teachers = new ArrayList<>();
		private final List<String> courses = new ArrayList<>();

		public void enrollStudent(String student) {
			students.add(student);
		}

		public void hireTeacher(String teacher) {
			teachers.add(teacher);
		}

		public void addCourse(String course) {
			courses.add(course);
		}
	}

	static class Finance {
		private double budget;
		private String payrollDay;

		public void chargeTuition(double amount) {
			budget += amount;
		}

		public void paySalary(double amount) {
			budget -= amount;
		}

		public void setPayrollDay(String day) {
			payrollDay = day;
		}
	}

	static class CampusServices {
		private int openTickets;
		private String cafeteriaMenu;
		private String busSchedule;

		public void openHelpDeskTicket() {
			openTickets++;
		}

		public void publishCafeteriaMenu(String menu) {
			cafeteriaMenu = menu;
		}

		public void publishBusSchedule(String schedule) {
			busSchedule = schedule;
		}
	}

	static class Website {
		private String theme;

		public void updateTheme(String theme) {
			this.theme = theme;
		}
	}

	public void clientCode() {
		Enrollment enrollment = new Enrollment();
		Finance finance = new Finance();
		CampusServices campus = new CampusServices();
		Website website = new Website();

		enrollment.enrollStudent("Nino");
		enrollment.hireTeacher("Ms. Kapanadze");
		enrollment.addCourse("Refactoring");
		finance.chargeTuition(2400);
		finance.paySalary(1200);
		finance.setPayrollDay("Friday");
		campus.openHelpDeskTicket();
		campus.publishBusSchedule("Route A at 08:00");
		campus.publishCafeteriaMenu("Soup and salad");
		website.updateTheme("blue");
	}
}
