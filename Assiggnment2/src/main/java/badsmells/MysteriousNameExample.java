package badsmells;

/*
 * Smell: Mysterious Name
 *
 * Problem: The method was called f() with parameters a, b, c and locals x, y —
 * readers had to reverse-engineer the math to understand the code's purpose.
 *
 * Refactoring: Renamed f() to calculateCostPerPerson(), parameters to quantity,
 * pricePerUnit, discount, and locals to totalCost, adjustedCost.
 *
 * Result: The code is self-documenting — names communicate intent so the method can
 * be understood without reading its body. (Abstraction, Clarity of Intent)
 */
public class MysteriousNameExample {

	public int calculateCostPerPerson(int quantity, int pricePerUnit, int discount) {
		int totalCost = quantity * pricePerUnit;
		int adjustedCost = totalCost - discount;
		return adjustedCost / 2;
	}

	public void clientCode() {
		int result = calculateCostPerPerson(8, 4, 6);
		System.out.println(result);
	}
}
