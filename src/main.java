
public class main {
	
	public static void main(String[] args)
	{
		BDD bdd = BDD.BDD_create("ABCDE!F!G!HIJ+A!B!C!DEFGH+BCDE!FG", "ABCDEFGHIJKLMNOPQ");
		System.out.println("------------FUCK------------");
		bdd.printTree();
		System.out.println("------------FUCK------------");
		System.out.println(BDD.BDD_use(bdd, "1111100011"));
	}

}
