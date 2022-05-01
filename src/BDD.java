import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BDD {

	public static BDD BDD_create(final String bfunkcia, final String poradie) {
		final String function = reduceFunction(formatInput(bfunkcia, poradie));
		Node root = new Node(function, "" + poradie.charAt(0), null);
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		final ArrayList<Node> allNodes = new ArrayList<>();
		final Node zeroNode = new Node("0", "*", null);
		final Node oneNode = new Node("1", "*", null);
		queue.add(root);
		while (!queue.isEmpty()) {
			final Node current = queue.remove();

			// If already exists just point the parent to it
			boolean skip = false;
			for (final Node node : allNodes) {
				if (node.getFunction().equals(current.getFunction())) {
					if (current.getParent().getLeft().equals(current)) {
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
			} else if (leftFunction == "1") {
				left = oneNode;
			} else {
				for (int i = 0; i < poradie.length(); i++) {
					if (leftFunction.contains(("" + poradie.charAt(i)).toLowerCase())
							|| leftFunction.contains(("" + poradie.charAt(i)))) {
						left = new Node(getFunctionForSymbol(current.getFunction(), current.getSymbol().toLowerCase()),
								"" + poradie.charAt(i), current);
						break;
					}
				}
			}
			// If the right function is a termination add no symbol, else find next symbol
			// found in fuction
			if (rightFunction == "0") {
				right = zeroNode;
			} else if (rightFunction == "1") {
				right = oneNode;
			} else {
				for (int i = 0; i < poradie.length(); i++) {
					if (rightFunction.contains("" + poradie.charAt(i))
							|| rightFunction.contains(("" + poradie.charAt(i)).toLowerCase())) {
						right = new Node(getFunctionForSymbol(current.getFunction(), current.getSymbol()),
								"" + poradie.charAt(i), current);
						break;
					}
				}
			}
			// If the same function is for the left and right
			if (left.getFunction().equals(right.getFunction())) {
				if (!left.getFunction().equals("0") && !left.getFunction().equals("1")) {
					queue.add(left);
				}
				if (current.getParent() != null) {
					if (current.getParent().getLeft() == current) {
						current.getParent().setLeft(left);
					} else {
						current.getParent().setRight(left);
					}
				} else {
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
			System.out.println("Root: " + current.getFunction() + " Symbol: " + current.getSymbol());
			System.out.println("Left: " + left.getFunction());
			System.out.println("Right: " + right.getFunction());
		}
		final char charArray[] = poradie.toCharArray();
		Arrays.sort(charArray);
		return new BDD(root, new String(charArray));
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
		final ArrayList<String> divided = new ArrayList<>();
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
			for (final String element : divided) {
				if (!element.contains(symbol.toLowerCase())) {
					followingFunction += element + "+";
				}
			}
			if (followingFunction.isBlank()) {
				return "0";
			}
			followingFunction = followingFunction.substring(0, followingFunction.length() - 1);
			followingFunction = followingFunction.replace(symbol, "");
			return followingFunction;
		}
		// No else needed here as all possible routes from previous if lead to a return.
		if ((divided.indexOf(symbol) != -1) && (divided.get(divided.indexOf(symbol)).length() < 2)) {
			return "1";
		}
		for (final String element : divided) {
			if (!element.contains(symbol.toUpperCase())) {
				followingFunction += element + "+";
			}
		}
		if (followingFunction.isBlank()) {
			return "0";
		}
		followingFunction = followingFunction.substring(0, followingFunction.length() - 1);
		followingFunction = followingFunction.replace(symbol, "");
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
		final ArrayList<String> removeable = new ArrayList<>();
		for (final String element : divided) {
			if (element.length() == 1) {
				for (int i = 0; i < divided.size(); i++) {
					// Absorption
					if ((divided.get(i).length() > 1) && divided.get(i).contains(element)) {
						removeable.add(divided.get(i));
					} else if ((divided.get(i).length() > 1) && divided.get(i).contains(element.toLowerCase())) {
						divided.set(i, divided.get(i).replace(element.toLowerCase(), ""));
					}
				}
			}
		}
		divided.removeAll(removeable);
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

	private Node root = null;

	private final String order;

	public BDD(final Node root, final String order) {
		this.root = root;
		this.order = order;
	}

	public int getNodeCount() {
		final ArrayList<Node> nodes = new ArrayList<>();
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		queue.add(root);
		Node current;
		if (root.getLeft() == null) {
			return 0;
		}
		while (!queue.isEmpty()) {
			current = queue.remove();
			if (!nodes.contains(current)) {
				nodes.add(current);
			}
			if ((current.getLeft().getFunction() != "0") && (current.getLeft().getFunction() != "1")) {
				queue.add(current.getLeft());
			}
			if ((current.getRight().getFunction() != "0") && (current.getRight().getFunction() != "1")) {
				queue.add(current.getRight());
			}
		}
		return nodes.size();
	}

	public void printTree() {
		final ArrayDeque<Node> queue = new ArrayDeque<>();
		queue.add(root);
		Node current;
		while (!queue.isEmpty()) {
			current = queue.remove();
			System.out.println("Root: " + current.getFunction() + " Symbol: " + current.getSymbol() + " Instance: "
					+ current.toString());
			if (current.getLeft() != null) {
				System.out.println("Left: " + current.getLeft().getFunction());
				System.out.println("Right: " + current.getRight().getFunction());
				if ((current.getLeft().getFunction() != "0") && (current.getLeft().getFunction() != "1")) {
					queue.add(current.getLeft());
				}
				if ((current.getRight().getFunction() != "0") && (current.getRight().getFunction() != "1")) {
					queue.add(current.getRight());
				}
			}
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