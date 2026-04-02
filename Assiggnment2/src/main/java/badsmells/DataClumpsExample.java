package badsmells;

/*
 * Smell: Data Clumps
 *
 * Problem: (name, email, phone) was passed as three separate parameters to every
 * method, bloating signatures and forcing clients to juggle loose variables.
 *
 * Refactoring: Created a ContactInfo class that bundles the three fields and
 * owns the methods (buildLabel, buildEmailGreeting, buildSmsMessage, isReachable).
 *
 * Result: Data travels as one object, and adding new fields requires changing only
 * ContactInfo — applying Encapsulation and Abstraction.
 */
public class DataClumpsExample {

	static class ContactInfo {

		private final String name;
		private final String email;
		private final String phone;

		public ContactInfo(String name, String email, String phone) {
			this.name = name;
			this.email = email;
			this.phone = phone;
		}

		public String buildLabel() {
			return name + " <" + email + ">, phone: " + phone;
		}

		public String buildEmailGreeting() {
			return "To: " + email + ", hello " + name;
		}

		public String buildSmsMessage() {
			return "SMS to " + phone + ": Hi " + name;
		}

		public boolean isReachable() {
			return email != null && !email.trim().isEmpty()
				&& phone != null && !phone.trim().isEmpty();
		}
	}

	public void clientCode() {
		ContactInfo student = new ContactInfo("Nino", "nino@example.com", "+995-555-000-001");
		ContactInfo advisor = new ContactInfo("Giorgi", "giorgi@example.com", "+995-555-000-002");
		ContactInfo accountant = new ContactInfo("Maka", "maka@example.com", "+995-555-000-003");

		ContactInfo[] contacts = { student, advisor, accountant };

		for (ContactInfo contact : contacts) {
			System.out.println(contact.buildLabel());
			System.out.println(contact.buildEmailGreeting());
			System.out.println(contact.buildSmsMessage());
			System.out.println(contact.isReachable());
		}
	}
}