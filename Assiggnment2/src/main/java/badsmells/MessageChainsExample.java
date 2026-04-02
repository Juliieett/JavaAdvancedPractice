package badsmells;

/*
 * Smell: Message Chains
 *
 * Problem: The client navigated four levels deep (University → Department → Coordinator
 * → Office) just to get a phone number, coupling it to the entire internal structure.
 *
 * Refactoring: Added delegate methods at each level so the client only calls
 * university.getCoordinatorPhone() — a single, direct request.
 *
 * Result: The client is decoupled from the internal object graph, and structural changes
 * only affect delegate methods, not callers. (Law of Demeter, Information Hiding)
 */
public class MessageChainsExample {

	static class University {

		private final Department department = new Department();

		String getCoordinatorPhone() {
			return department.getCoordinatorPhone();
		}
	}

	static class Department {

		private final Coordinator coordinator = new Coordinator();

		String getCoordinatorPhone() {
			return coordinator.getOfficePhone();
		}
	}

	static class Coordinator {

		private final Office office = new Office();

		String getOfficePhone() {
			return office.getPhoneNumber();
		}
	}

	static class Office {

		String getPhoneNumber() {
			return "555-0101";
		}
	}

	public void clientCode() {
		University university = new University();
		System.out.println(university.getCoordinatorPhone());
	}
}
