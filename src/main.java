
public class main {

	public static void main(final String[] args) {
		final BDD bdd = BDD.BDD_create("AB+C", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		System.out.println("------------FUCK------------");
		bdd.printTree();
		System.out.println("------------FUCK------------");
		//System.out.println(BDD.BDD_use(bdd, "1001"));
		if (BDD.BDD_use(bdd, "00000") != '0') {
			System.out.print("Te retard 1");
		}
		if (BDD.BDD_use(bdd, "00100") != '1') {
			System.out.print("Te retard 2");
		}
		if (BDD.BDD_use(bdd, "01000") != '0') {
			System.out.print("Te retard 3");
		}
		if (BDD.BDD_use(bdd, "01100") != '1') {
			System.out.print("Te retard 4");
		}
		if (BDD.BDD_use(bdd, "10000") != '0') {
			System.out.print("Te retard 5");
		}
		if (BDD.BDD_use(bdd, "10100") != '1') {
			System.out.print("Te retard 6");
		}
		if (BDD.BDD_use(bdd, "11000") != '1') {
			System.out.print("Te retard 7");
		}
		if (BDD.BDD_use(bdd, "11100") != '1') {
			System.out.print("Te retard 8");
		}
		System.out.println("Node count: " + bdd.getNodeCount());
	}

}
