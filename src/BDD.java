import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BDD {

	public static BDD BDD_create(final String bfunkcia, final String poradie) {
		final String function = reduceFunction(formatInput(bfunkcia, poradie));
		System.out.println(function);
		System.out.println(poradie);
		String startingSymbol = "" + poradie.charAt(0);
		// We get the starting symbol
		for (int i = 1; i < poradie.length(); i++) {
			if (function.toUpperCase().contains(startingSymbol)) {
				break;
			}
			startingSymbol = "" + poradie.charAt(i);
		}
		Node root = new Node(function, startingSymbol, null);
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		final ArrayList<Node> allNodes = new ArrayList<>();
		final Node zeroNode = new Node("0", "*", null);
		final Node oneNode = new Node("1", "*", null);
		boolean containsZero = false;
		boolean containsOne = false;
		queue.add(root);
		while (!queue.isEmpty()) {
			Node current = queue.remove();

			// If already exists just point the parent to it
			boolean skip = false;
			for (final Node node : allNodes) {
				if (node.getFunction().equals(current.getFunction())) {
					if(current.getParent() == null)
					{
						root = current;
					}
					else if (current.getParent().getLeft().equals(current)) {
						current.getParent().setLeft(node);
					} else {
						current.getParent().setRight(node);
					}
					skip = true;
				}
			}
			if (skip) {
				continue;
			}

			Node left = null;
			Node right = null;
			final String leftFunction = getFunctionForSymbol(current.getFunction(), current.getSymbol().toLowerCase());
			final String rightFunction = getFunctionForSymbol(current.getFunction(), current.getSymbol());
			// If the left function is a termination add no symbol, else find next symbol
			// found in fuction
			if (leftFunction == "0") {
				left = zeroNode;
				containsZero = true;
			} else if (leftFunction == "1") {
				left = oneNode;
				containsOne = true;
			} else {
				int i = 0;
				while(!leftFunction.toUpperCase().contains(""+poradie.charAt(i)))
				{
					i++;
				}
				left = new Node(getFunctionForSymbol(current.getFunction(), current.getSymbol().toLowerCase()),
						"" + poradie.charAt(i), current);
			}
			// If the right function is a termination add no symbol, else find next symbol
			// found in fuction
			if (rightFunction == "0") {
				right = zeroNode;
				containsZero = true;
			} else if (rightFunction == "1") {
				right = oneNode;
				containsOne = true;
			} else {
				int i = 0;
				while(!rightFunction.toUpperCase().contains(""+poradie.charAt(i)))
				{
					i++;
				}
				right = new Node(getFunctionForSymbol(current.getFunction(), current.getSymbol()),
						"" + poradie.charAt(i), current);
			}
			// If the same function is for the left and right
			if (left.getFunction().equals(right.getFunction())) {
				if (!left.getFunction().equals("0") && !left.getFunction().equals("1")) {
					queue.add(left);
				}
				if (current.getParent() != null) {
					left.setParent(current.getParent());
					if (current.getParent().getLeft() == current) {
						current.getParent().setLeft(left);
					} else {
						current.getParent().setRight(left);
					}
				} else {
					left.setParent(null);
					root = left;
				}
			} else {
				// If left isn't terminating
				if (!left.getFunction().equals("0") && !left.getFunction().equals("1")) {
					queue.add(left);
				}
				// If right isn't terminating
				if (!right.getFunction().equals("1") && !right.getFunction().equals("0")) {
					queue.add(right);
				}
				current.setLeft(left);
				current.setRight(right);
				allNodes.add(current);
			}
		}
		final char sortedOrder[] = poradie.toCharArray();
		Arrays.sort(sortedOrder);
		// If this function is always one
		if (!containsZero) {
			return new BDD(oneNode, new String(sortedOrder));
		}
		// If this function is always zero
		if (!containsOne) {
			return new BDD(zeroNode, new String(sortedOrder));
		}
		return new BDD(root, new String(sortedOrder));
	}

	public static char BDD_use(final BDD bdd, final String vstupy) {
		return bdd.use(vstupy);
	}

	private static String formatInput(final String function, final String order) {
		// Just cleaning up the formating to match what we used
		String correctFormat = function;
		correctFormat = correctFormat.replace(".", "");
		correctFormat = correctFormat.replace("(", "");
		correctFormat = correctFormat.replace(")", "");
		correctFormat = correctFormat.replace(" ", "");
		for (int i = 0; i < order.length(); i++) {
			correctFormat = correctFormat.replace("!" + order.charAt(i), "" + Character.toLowerCase(order.charAt(i)));
		}
		return correctFormat;
	}

	private static String getFunctionForSymbol(final String function, final String symbol) {
		final String reduced = reduceFunction(function);
		ArrayList<String> divided = new ArrayList<>();
		String tmp = "";
		for (int i = 0; i < reduced.length(); i++) {
			if (reduced.charAt(i) != '+') {
				tmp += reduced.charAt(i);
			} else {
				divided.add(tmp);
				tmp = "";
			}
		}
		divided.add(tmp);
		String followingFunction = "";
		// If symbol matches
		if (reduced.equals(symbol)) {
			return "1";
		}
		// If symbol is negative and function is positive
		if (reduced.equals(symbol.toLowerCase()) || reduced.equals(symbol.toUpperCase())) {
			return "0";
		}
		if (Character.isUpperCase(symbol.charAt(0))) {
			// If the current reduced symbol is on it's own this will make the current
			// function always 1 when we are looking for the positive
			if ((divided.indexOf(symbol) != -1) && (divided.get(divided.indexOf(symbol)).length() < 2)) {
				return "1";
			}
			if ((divided.indexOf(symbol) != -1) && (divided.indexOf(symbol.toLowerCase()) != -1)
					&& (divided.get(divided.indexOf(symbol.toLowerCase())).length() < 2)) {
				return "1";
			}
			for (final String element : divided) {
				if (!element.contains(symbol.toLowerCase())) {
					followingFunction += element.replace(symbol, "") + "+";
				}
			}
			if (followingFunction.isBlank()) {
				return "0";
			}
			followingFunction = followingFunction.substring(0, followingFunction.length() - 1);
			return followingFunction;
		}
		// No else needed here as all possible routes from previous if lead to a return.
		if ((divided.indexOf(symbol) != -1) && (divided.get(divided.indexOf(symbol)).length() < 2)) {
			return "1";
		}
		if ((divided.indexOf(symbol) != -1) && (divided.indexOf(symbol.toUpperCase()) != -1)
				&& (divided.get(divided.indexOf(symbol.toUpperCase())).length() < 2)) {
			return "1";
		}
		for (final String element : divided) {
			if (!element.contains(symbol.toUpperCase())) {
				followingFunction += element.replace(symbol, "") + "+";
			}
		}
		if (followingFunction.isBlank()) {
			return "0";
		}
		followingFunction = followingFunction.substring(0, followingFunction.length() - 1);
		return followingFunction;
	}

	private static String reduceFunction(final String function) {
		String reduced = function;
		ArrayList<String> divided = new ArrayList<>();
		String tmp = "";
		for (int i = 0; i < reduced.length(); i++) {
			if (reduced.charAt(i) != '+') {
				tmp += reduced.charAt(i);
			} else {
				divided.add(tmp);
				tmp = "";
			}
		}
		divided.add(tmp);
		divided = (ArrayList<String>) divided.stream().distinct().collect(Collectors.toList());
		reduced = "";
		for (final String element : divided) {
			reduced += element + "+";
		}
		if (reduced.isBlank()) {
			return "";
		}
		reduced = reduced.substring(0, reduced.length() - 1);
		return reduced;
	}

	public static Boolean verify(final BDD bdd, String function) {
		final String order = bdd.getOrder();
		function = reduceFunction(formatInput(function, order));
		boolean correct = true;
		final ArrayList<String> divided = new ArrayList<>();
		String tmp = "";
		for (int j = 0; j < function.length(); j++) {
			if (function.charAt(j) != '+') {
				tmp += function.charAt(j);
			} else {
				divided.add(tmp);
				tmp = "";
			}
		}
		divided.add(tmp);
		for (int i = 0; i < (Math.pow(2, order.length())); i++) {
			final String input = String.format("%" + order.length() + "s", Integer.toBinaryString(i)).replaceAll(" ",
					"0");
			final char result = BDD.BDD_use(bdd, input);
			boolean one = false;
			for (final String element : divided) {
				boolean value = true;
				for (final char character : element.toCharArray()) {
					if (input.charAt(order.indexOf(Character.toUpperCase(character))) == '1') {
						if (Character.isLowerCase(character)) {
							value = false;
						}
					} else if (Character.isUpperCase(character)) {
						value = false;
					}
				}
				if (value) {
					one = true;
					break;
				}
			}
			if (one && (result != '1')) {
				System.out.println("Got 0 expected 1 for input: " + input);
				correct = false;
			} else if (!one && (result != '0')) {
				System.out.println("Got 1 expected 0 for input: " + input);
				correct = false;
			}
		}
		return correct;
	}

	private Node root = null;

	private final String order;

	public BDD(final Node root, final String order) {
		this.root = root;
		this.order = order;
	}

	public int getNodeCount() {
		final ArrayList<Node> nodes = new ArrayList<>();
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		if (root.getSymbol() == "*") {
			return 1;
		}
		queue.add(root);
		Node current;
		while (!queue.isEmpty()) {
			current = queue.remove();
			if (!nodes.contains(current)) {
				nodes.add(current);
			}
			if ((current.getLeft().getFunction() != "0") && (current.getLeft().getFunction() != "1")) {
				queue.add(current.getLeft());
			} else if (!nodes.contains(current.getLeft())) {
				nodes.add(current.getLeft());
			}
			if ((current.getRight().getFunction() != "0") && (current.getRight().getFunction() != "1")) {
				queue.add(current.getRight());
			} else if (!nodes.contains(current.getRight())) {
				nodes.add(current.getRight());
			}
		}
		return nodes.size();
	}

	public String getOrder() {
		return order;
	}

	public void printTree() {
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		queue.add(root);
		Node current;
		ArrayList<Node> nodes = new ArrayList<Node>();
		while (!queue.isEmpty()) {
			current = queue.remove();
			if(!nodes.contains(current))
			{
				System.out.println("Root: " + current.getFunction() + " Symbol: " + current.getSymbol() + " Instance: "
						+ current.toString());
				if (current.getLeft() != null || current.getRight() != null) {
					System.out.println("Left: " + current.getLeft().getFunction());
					System.out.println("Right: " + current.getRight().getFunction());
				}
			}
			if (current.getLeft() != null) {
				if ((current.getLeft().getFunction() != "0") && (current.getLeft().getFunction() != "1")) {
					queue.add(current.getLeft());
				}
				if ((current.getRight().getFunction() != "0") && (current.getRight().getFunction() != "1")) {
					queue.add(current.getRight());
				}
			}
			nodes.add(current);
		}
	}

	public char use(final String inputs) {
		Node current = root;
		if (root.getLeft() == null) {
			return current.getFunction().charAt(0);
		}
		while ((current.getFunction() != "0") && (current.getFunction() != "1")) {
			if (inputs.charAt(order.indexOf(current.getSymbol().charAt(0))) == '0') {
				current = current.getLeft();
			} else {
				current = current.getRight();
			}
		}
		return current.getFunction().charAt(0);
	}
}