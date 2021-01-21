package core;

import java.util.ArrayList;
import java.util.Random;

public class Randomizer
{
	private static final double MIN_PROBABILITY = 0.0;
	private static final double MAX_PROBABILITY = 1.0;
	
	private static Random random = new Random();
	private ArrayList<ProbVal<Integer>> prob2Int = new ArrayList<ProbVal<Integer>>();

	public void addProbInt(double to, int value)
	{
		if (to >= MIN_PROBABILITY && to <= MAX_PROBABILITY)
		{
			prob2Int.add(new ProbVal<Integer>(to, value));
		}
	}
	
	public Integer nextInt()
	{
		double r = random.nextDouble();
		
		for (ProbVal<Integer> pI : prob2Int)
		{
			if (r <= pI.to)
				return pI.value;
		}
		
		return null;
	}
}
