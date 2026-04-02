package badsmells;

/*
 * Smell: Duplicated Code
 *
 * Problem: summerInvoice() and winterInvoice() duplicated the same tax and
 * shipping logic; only the discount calculation differed between them.
 *
 * Refactoring: Extracted shared tax, shipping, and total logic into reusable
 * methods so each season method only defines its unique discount.
 *
 * Result: Changing tax or shipping rules requires a single edit, following
 * DRY (Don't Repeat Yourself) and High Cohesion principles.
 */
public class DuplicatedCodeExample {

	private double calculateTax(double subtotal) {
		return subtotal * 0.18;
	}

	private double calculateShipping(double subtotal) {
		return subtotal > 100 ? 0 : 15;
	}

	private double calculateInvoice(double subtotal, double discount) {
		return subtotal + calculateTax(subtotal) + calculateShipping(subtotal) - discount;
	}

	public double summerInvoice(double subtotal) {
		double discount = subtotal > 200 ? subtotal * 0.10 : 0;
		return calculateInvoice(subtotal, discount);
	}

	public double winterInvoice(double subtotal) {
		double discount = subtotal > 200 ? subtotal * 0.20 : 50;
		return calculateInvoice(subtotal, discount);
	}

	public void clientCode() {
		System.out.println(summerInvoice(240));
		System.out.println(winterInvoice(240));
	}
}
