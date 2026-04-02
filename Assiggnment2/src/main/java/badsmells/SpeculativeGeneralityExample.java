package badsmells;

/*
 * Smell: Speculative Generality
 *
 * Problem: send() accepted four parameters (message, futureTemplate, encrypted,
 * urgent), but only message was ever used — the rest were dead weight.
 *
 * Refactoring: Removed the three unused parameters so send() takes only the
 * message it actually needs.
 *
 * Result: The interface is minimal and honest; unused complexity is gone, and
 * real features can be added when truly needed (YAGNI, Simplicity).
 */
public class SpeculativeGeneralityExample {

	interface NotificationChannel {

		void send(String message);
	}

	static class EmailChannel implements NotificationChannel {

		@Override
		public void send(String message) {
			System.out.println(message);
		}
	}

	public void clientCode() {
		NotificationChannel channel = new EmailChannel();
		channel.send("Exam starts at 10:00");
	}
}
