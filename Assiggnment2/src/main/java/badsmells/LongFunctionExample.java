package badsmells;

/*
 * Smell: Long Function
 *
 * Problem: processOrder() mixed discount, shipping, tax, approval, and
 * formatting logic in one 50-line method that was hard to read or test.
 *
 * Refactoring: Extracted five focused methods (calculateDiscount, calculateShipping,
 * calculateTax, determineApprovalStatus, buildSummary) so processOrder() reads
 * like a high-level plan.
 *
 * Result: Each method does one thing and can be tested independently, following
 * the Single Responsibility Principle and improving cohesion.
 */
public class LongFunctionExample {

	public String processOrder(String customerType, int quantity, double price, boolean expressDelivery) {
		double subtotal = quantity * price;
		double discount = calculateDiscount(customerType, subtotal);
		double shipping = calculateShipping(expressDelivery, quantity);
		double tax = calculateTax(subtotal, discount);
		double total = subtotal - discount + shipping + tax;
		String status = determineApprovalStatus(total);

		return buildSummary(customerType, quantity, price, subtotal, discount, shipping, tax, total, status);
	}

	private double calculateDiscount(String customerType, double subtotal) {
		if ("STUDENT".equals(customerType)) {
			return subtotal * 0.05;
		} else if ("VIP".equals(customerType)) {
			return subtotal * 0.12;
		} else if ("EMPLOYEE".equals(customerType)) {
			return subtotal * 0.20;
		}
		return 0;
	}

	private double calculateShipping(boolean expressDelivery, int quantity) {
		double shipping = expressDelivery ? 25 : 10;
		double bulkExtra = expressDelivery ? 10 : 5;
		if (quantity > 10) {
			shipping += bulkExtra;
		}
		return shipping;
	}

	private double calculateTax(double subtotal, double discount) {
		return (subtotal - discount) * 0.18;
	}

	private String determineApprovalStatus(double total) {
		if (total > 500) {
			return "MANAGER_APPROVAL";
		} else if (total > 200) {
			return "FINANCE_REVIEW";
		}
		return "AUTO_APPROVED";
	}

	private String buildSummary(String customerType, int quantity, double price,
								double subtotal, double discount, double shipping,
								double tax, double total, String status) {
		StringBuilder summary = new StringBuilder();
		summary.append("customerType=").append(customerType).append('\n');
		summary.append("quantity=").append(quantity).append('\n');
		summary.append("price=").append(price).append('\n');
		summary.append("subtotal=").append(subtotal).append('\n');
		summary.append("discount=").append(discount).append('\n');
		summary.append("shipping=").append(shipping).append('\n');
		summary.append("tax=").append(tax).append('\n');
		summary.append("total=").append(total).append('\n');
		summary.append("status=").append(status);
		return summary.toString();
	}

	public void clientCode() {
		System.out.println(processOrder("VIP", 12, 30, true));
		System.out.println(processOrder("STUDENT", 2, 50, false));
	}
}
