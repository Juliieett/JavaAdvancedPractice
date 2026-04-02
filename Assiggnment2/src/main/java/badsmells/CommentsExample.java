package badsmells;

/*
 * Smell: Comments
 *
 * Problem: finalPrice() relied on comments to explain magic numbers (0.10, 0.05, 20, 0.18)
 * and tangled discount/tax logic that was unreadable on its own.
 *
 * Refactoring: Replaced magic numbers with named constants and extracted each pricing
 * step into its own method (applyVipDiscount, applyBulkDiscount, applyTax).
 *
 * Result: The code is self-documenting so comments are unnecessary, following
 * Clarity of Intent and Abstraction principles.
 */
public class CommentsExample {

	private static final double VIP_DISCOUNT_RATE = 0.10;
	private static final double BULK_DISCOUNT_RATE = 0.05;
	private static final int BULK_QUANTITY_THRESHOLD = 20;
	private static final double TAX_RATE = 0.18;

	public double finalPrice(double basePrice, boolean isVip, int quantity) {
		double price = basePrice;
		price = applyVipDiscount(price, isVip);
		price = applyBulkDiscount(price, quantity);
		price = applyTax(price);
		return price;
	}

	private double applyVipDiscount(double price, boolean isVip) {
		return isVip ? price * (1 - VIP_DISCOUNT_RATE) : price;
	}

	private double applyBulkDiscount(double price, int quantity) {
		return quantity > BULK_QUANTITY_THRESHOLD ? price * (1 - BULK_DISCOUNT_RATE) : price;
	}

	private double applyTax(double price) {
		return price * (1 + TAX_RATE);
	}

	public void clientCode() {
		double vipOrder = finalPrice(120, true, 25);
		double regularOrder = finalPrice(120, false, 5);
		System.out.println(vipOrder);
		System.out.println(regularOrder);
	}
}