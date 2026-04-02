package badsmells;

/*
 * Smell: Temporary Field
 *
 * Problem: A single class held both examRoom and onlineMeetingLink, but only one
 * was ever valid at a time — the other sat null or stale.
 *
 * Refactoring: Split into OnsiteExam (examRoom only) and OnlineExam (meetingLink
 * only), so each class carries only the fields it actually uses.
 *
 * Result: Every field is always meaningful; no null checks or stale data are
 * possible (Encapsulation, High Cohesion).
 */
public class TemporaryFieldExample {

	static class OnsiteExam {
		private final String examRoom;

		OnsiteExam(String examRoom) {
			this.examRoom = examRoom;
		}

		public String prepare() {
			return "Use room " + examRoom;
		}
	}

	static class OnlineExam {
		private final String meetingLink;

		OnlineExam(String meetingLink) {
			this.meetingLink = meetingLink;
		}

		public String prepare() {
			return "Join " + meetingLink;
		}
	}

	public void clientCode() {
		OnsiteExam onsite = new OnsiteExam("B-204");
		OnlineExam online = new OnlineExam("https://meet.example/exam");

		System.out.println(onsite.prepare());
		System.out.println(online.prepare());
		System.out.println("room=" + onsite.examRoom + ", link=" + online.meetingLink);
	}
}
