
public class main {

	public static void main(final String[] args) {
		final BDD bdd = BDD.BDD_create("AB+C", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		System.out.println("------------FUCK------------");
		bdd.printTree();
		System.out.println("------------FUCK------------");
		//System.out.println(BDD.BDD_use(bdd, "1001"));
		if (BDD.BDD_use(bdd, "000") != '0') {
			System.out.print("Te retard 1");
		}
		if (BDD.BDD_use(bdd, "001") != '1') {
			System.out.print("Te retard 2");
		}
		if (BDD.BDD_use(bdd, "010") != '0') {
			System.out.print("Te retard 3");
		}
		if (BDD.BDD_use(bdd, "011") != '1') {
			System.out.print("Te retard 4");
		}
		if (BDD.BDD_use(bdd, "100") != '0') {
			System.out.print("Te retard 5");
		}
		if (BDD.BDD_use(bdd, "101") != '1') {
			System.out.print("Te retard 6");
		}
		if (BDD.BDD_use(bdd, "110") != '1') {
			System.out.print("Te retard 7");
		}
		if (BDD.BDD_use(bdd, "111") != '1') {
			System.out.print("Te retard 8");
		}
		System.out.println("Node count: " + bdd.getNodeCount());
	}

}
