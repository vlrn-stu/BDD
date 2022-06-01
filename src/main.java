import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class main {

	public static String createFunction(final String order, final int numberOfAdditions) {
		String function = "";
		for (int i = 0; i < numberOfAdditions; i++) {
			for (int j = 0; j < order.length(); j++) {
				switch (new Random().nextInt(1, 4)) {
				case 1:
					function += order.charAt(j);
					break;
				case 2:
					function += Character.toLowerCase(order.charAt(j));
					break;
				}
			}
			if(i != numberOfAdditions -1)
			{
				function += '+';
			}
		}
		return function;
	}

	public static String createOrder(final int length) {
		String order = "";
		for (int i = 0; i < length; i++) {
			char symbol = (char) new Random().nextInt(65, 90);
			while (order.contains("" + symbol)) {
				symbol = (char) new Random().nextInt(65, 90);
			}
			order += symbol;
		}
		final List<String> characters = Arrays.asList(order.split(""));
		Collections.shuffle(characters);
		StringBuilder afterShuffle = new StringBuilder();
		for (final String character : characters) {
			afterShuffle.append(character);
		}
		return afterShuffle.toString();
	}

	public static String shuffleOrder(final String order)
	{
		final List<String> characters = Arrays.asList(order.split(""));
		Collections.shuffle(characters);
		StringBuilder afterShuffle = new StringBuilder();
		for (final String character : characters) {
			afterShuffle.append(character);
		}
		return afterShuffle.toString();
	}
	
	public static void runTest(int testCount, int variableCount, int additionCount, int orderShuffleCount)
	{
		ArrayList<Long> creationTimes = new ArrayList<Long>();
		ArrayList<Long> useTimes = new ArrayList<Long>();
		int errorCount = 0;
		for(int i = 0; i<testCount; i++)
		{
			String order = createOrder(variableCount);
			String function = createFunction(order, additionCount);
			BDD bdd;
			for(int j = 0; j<orderShuffleCount; j ++)
			{
				order = shuffleOrder(order);
				long creationStartTime = System.nanoTime();
				bdd = BDD.BDD_create(function, order);
				long creationEndTime = System.nanoTime();
				creationTimes.add(creationEndTime-creationStartTime);
				String input = String.format("%" + order.length() + "s", Integer.toBinaryString(new Random().nextInt(0,variableCount))).replaceAll(" ",
						"0");
				long useStartTime = System.nanoTime();
				BDD.BDD_use(bdd, input);
				long useEndTime = System.nanoTime();
				useTimes.add(useEndTime-useStartTime);
				if(!BDD.verify(bdd, function))
				{
					errorCount++;
					System.out.println("Error in BDD!!!");
				}
				System.out.println("Node count without reduction: " + (int)(Math.pow(2, order.length()+1) -1));
				System.out.println("Node count after reduction: " + bdd.getNodeCount());
				System.out.println("Reduction of: " + (100 - (bdd.getNodeCount()/((Math.pow(2, order.length()+1) -1)))*100) + "%");
			}
		}
		Long creationTimeSum = 0L;
		for(Long time:creationTimes)
		{
			creationTimeSum+=time;
		}
		Long creationTime = creationTimeSum/creationTimes.size();
		Long useTimeSum = 0L;
		for(Long time:useTimes)
		{
			useTimeSum+=time;
		}
		Long useTime = useTimeSum/useTimes.size();
		System.out.println("----------------------------------------------------------------------------------------------------");
		System.out.println("Average creation time for "+variableCount+" variables and " + additionCount + " additions ("+testCount*orderShuffleCount+" creations): "+(double)creationTime/(double)1000000 + " ms ("+creationTime+" nano)");
		System.out.println("Average use time for "+variableCount+" variables and " + additionCount + " additions ("+testCount*orderShuffleCount+" creations): "+(double)useTime/(double)1000000 + " ms ("+useTime+" nano)");
		System.out.println("Testing complete for "+testCount*orderShuffleCount+" tests with "+errorCount+" errors");
		System.out.println("----------------------------------------------------------------------------------------------------");
	}
	
	public static void main(final String[] args) {
		final String function = "AB+C";
		final String order = "ABC";
//		String order = createOrder(20);
//		String function = createFunction(order,100);
		final BDD bdd = BDD.BDD_create(function, order);
		System.out.println("------------------------");
		bdd.printTree();
		System.out.println("------------------------");
		System.out.println("Node count without reduction: " + (int)(Math.pow(2, order.length()+1) -1));
		System.out.println("Node count after reduction: " + bdd.getNodeCount());
		System.out.println("Reduction of: " + (100 - (bdd.getNodeCount()/((Math.pow(2, order.length()+1) -1)))*100) + "%");
		System.out.println("Correct: " + BDD.verify(bdd, function));
		
//		runTest(Number of test to be done (actual tests done is this times reshuffles), Number of variables ,Number of additions, Number of reshuffles);	
		runTest(1,15,100,10);
	}

}
