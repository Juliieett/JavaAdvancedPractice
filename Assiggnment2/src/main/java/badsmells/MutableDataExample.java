package badsmells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Smell: Mutable Data
 *
 * Problem: getEnrolledStudents() returned the raw internal list, so any caller could
 * add, remove, or clear students behind the class's back.
 *
 * Refactoring: The getter now returns Collections.unmodifiableList(), and an unenroll()
 * method was added so all modifications go through the class.
 *
 * Result: The class fully controls its own state, preventing accidental corruption, and
 * future rules (e.g., logging) can be added in one place. (Encapsulation, Information Hiding)
 */
public class MutableDataExample {

	private final List<String> enrolledStudents = new ArrayList<>();

	public List<String> getEnrolledStudents() {
		return Collections.unmodifiableList(enrolledStudents);
	}

	public void enroll(String studentId) {
		enrolledStudents.add(studentId);
	}

	public void unenroll(String studentId) {
		enrolledStudents.remove(studentId);
	}

	public void clientCode() {
		enroll("s-1001");
		enroll("s-1002");
		System.out.println(getEnrolledStudents().size());

		unenroll("s-1001");
		System.out.println(getEnrolledStudents().size());
	}
}
