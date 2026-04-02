package badsmells;

/*
 * Smell: Insider Trading
 *
 * Problem: AuditService directly accessed BankAccount's package-visible fields
 * (balance, secretFlag), tightly coupling the two classes' internals.
 *
 * Refactoring: Made fields private and added freezeIfNegative() so BankAccount
 * owns its own freeze logic; AuditService now calls that method instead.
 *
 * Result: The classes communicate through methods, not raw fields, achieving
 * information hiding and low coupling.
 */
public class InsiderTradingExample {

	static class BankAccount {

		private double balance;
		private String status;

		BankAccount(double balance) {
			this.balance = balance;
			this.status = "ACTIVE";
		}

		public void freezeIfNegative() {
			if (balance < 0) {
				status = "FROZEN";
			}
		}

		public String getStatus() {
			return status;
		}
	}

	static class AuditService {

		public void audit(BankAccount account) {
			account.freezeIfNegative();
		}
	}

	public void clientCode() {
		BankAccount account = new BankAccount(-50);

		AuditService auditService = new AuditService();
		auditService.audit(account);

		System.out.println(account.getStatus());
	}
}
