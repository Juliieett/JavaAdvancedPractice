package badsmells;

/*
 * Smell: Refused Bequest
 *
 * Problem: Penguin inherited fly() from Bird and had to throw an exception,
 * violating the Liskov Substitution Principle and crashing at runtime.
 *
 * Refactoring: Split the hierarchy into Bird (shared behavior) and FlyingBird
 * (adds fly()); Penguin now extends Bird directly with its own swim() method.
 *
 * Result: Each subclass only inherits what it can truly support, so substitution
 * is always safe (Liskov Substitution Principle, Proper Inheritance).
 */
public class RefusedBequestExample {

	static class Bird {

		public String describe() {
			return "I am a bird";
		}
	}

	static class FlyingBird extends Bird {

		public void fly() {
			System.out.println("Flying");
		}
	}

	static class Sparrow extends FlyingBird {

		@Override
		public void fly() {
			System.out.println("Sparrow is flying");
		}
	}

	static class Eagle extends FlyingBird {

		@Override
		public void fly() {
			System.out.println("Eagle is soaring");
		}
	}

	static class Penguin extends Bird {

		public void swim() {
			System.out.println("Penguin is swimming");
		}
	}

	public void clientCode() {
		FlyingBird sparrow = new Sparrow();
		FlyingBird eagle = new Eagle();
		Penguin penguin = new Penguin();

		sparrow.fly();
		eagle.fly();
		penguin.swim();
	}
}
