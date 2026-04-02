package badsmells;

/*
 * Smell: Global Data
 *
 * Problem: currentSemester and tuitionRate were public static fields that any
 * class could read or modify freely, making changes impossible to trace.
 *
 * Refactoring: Wrapped the fields in a UniversityConfig class with private state
 * and controlled methods; dependents receive it via constructor injection.
 *
 * Result: Only UniversityConfig can modify its own data, enforcing encapsulation
 * and information hiding while making the code easier to test.
 */
public class GlobalDataExample {

	static class UniversityConfig {
		private String currentSemester;
		private double tuitionRate;

		public UniversityConfig(String currentSemester, double tuitionRate) {
			this.currentSemester = currentSemester;
			this.tuitionRate = tuitionRate;
		}

		public String getCurrentSemester() {
			return currentSemester;
		}

		public double getTuitionRate() {
			return tuitionRate;
		}

		public void setSemester(String semester) {
			this.currentSemester = semester;
		}

		public void increaseTuitionRate(double delta) {
			this.tuitionRate += delta;
		}
	}

	static class BillingService {
		private final UniversityConfig config;

		BillingService(UniversityConfig config) {
			this.config = config;
		}

		public double calculateInvoice(int credits) {
			return credits * config.getTuitionRate();
		}
	}

	static class SemesterAdministration {
		private final UniversityConfig config;

		SemesterAdministration(UniversityConfig config) {
			this.config = config;
		}

		public void openFallSemester() {
			config.setSemester("FALL");
		}

		public void approveRateIncrease() {
			config.increaseTuitionRate(100);
		}
	}

	public void clientCode() {
		UniversityConfig config = new UniversityConfig("SPRING", 1250.0);
		BillingService billingService = new BillingService(config);
		SemesterAdministration administration = new SemesterAdministration(config);

		System.out.println(config.getCurrentSemester());
		System.out.println(billingService.calculateInvoice(3));
		administration.openFallSemester();
		administration.approveRateIncrease();
		System.out.println(config.getCurrentSemester());
		System.out.println(billingService.calculateInvoice(3));
	}
}
