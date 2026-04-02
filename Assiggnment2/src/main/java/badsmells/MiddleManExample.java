package badsmells;

/*
 * Smell: Middle Man
 *
 * Problem: StudentPortal existed only to forward findGrade() calls to TranscriptService
 * without adding any logic, validation, or value of its own.
 *
 * Refactoring: Removed the unnecessary StudentPortal class and let the client call
 * TranscriptService directly.
 *
 * Result: One fewer class to maintain, and the client reaches the real service without
 * a pointless detour. (Meaningful Abstraction, Simplicity)
 */
public class MiddleManExample {

	static class TranscriptService {

		public String findGrade(String studentId) {
			return "A";
		}
	}

	public void clientCode() {
		TranscriptService transcriptService = new TranscriptService();
		System.out.println(transcriptService.findGrade("s-1001"));
	}
}
