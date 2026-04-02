package badsmells;

/*
 * Smell: Alternative Classes with Different Interfaces
 *
 * Problem: ZoomClassroom and TeamsClassroom did the same job but exposed
 * different method names (beginSession vs openMeeting), preventing polymorphic use.
 *
 * Refactoring: Introduced a shared VirtualClassroom interface with startSession(),
 * and made both classes implement it so the client works through one type.
 *
 * Result: New platforms are added without changing client code, leveraging
 * Polymorphism and the Open/Closed Principle.
 */
public class AlternativeClassesWithDifferentInterfacesExample {

	interface VirtualClassroom {
		void startSession();
	}

	static class ZoomClassroom implements VirtualClassroom {

		@Override
		public void startSession() {
			System.out.println("Zoom session started");
		}
	}

	static class TeamsClassroom implements VirtualClassroom {

		@Override
		public void startSession() {
			System.out.println("Teams meeting started");
		}
	}

	public void clientCode() {
		VirtualClassroom[] classrooms = {
			new ZoomClassroom(),
			new TeamsClassroom()
		};

		for (VirtualClassroom classroom : classrooms) {
			classroom.startSession();
		}
	}
}