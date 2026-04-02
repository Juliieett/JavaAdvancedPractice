package badsmells;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Smell: Loops
 *
 * Problem: honorStudents() used a manual for-loop to filter by GPA and collect names,
 * burying the intent under boilerplate like temporary lists and index tracking.
 *
 * Refactoring: Replaced the loop with a stream pipeline — filter, map, collect — so
 * the transformation reads as a single declarative expression.
 *
 * Result: The method's intent is immediately clear, there is less boilerplate, and
 * adding steps like sorting or limiting is trivial. (Abstraction, Clarity of Intent)
 */
public class LoopsExample {

	public List<String> honorStudents(List<Student> students) {
		return students.stream()
			.filter(student -> student.gpa > 3.5)
			.map(student -> student.name)
			.collect(Collectors.toList());
	}

	static class Student {

		String name;
		double gpa;

		Student(String name, double gpa) {
			this.name = name;
			this.gpa = gpa;
		}
	}

	public void clientCode() {
		List<Student> students = Arrays.asList(
			new Student("Nino", 3.9),
			new Student("Giorgi", 3.1),
			new Student("Maka", 3.7)
		);

		System.out.println(honorStudents(students));
	}
}
