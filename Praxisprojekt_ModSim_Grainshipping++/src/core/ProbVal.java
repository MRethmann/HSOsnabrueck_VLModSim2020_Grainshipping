package core;

public class ProbVal<T> implements Comparable<ProbVal<T>>
{
	Double to = null;
	T value;
	
	
	public ProbVal(Double to, T value)
	{
		this.to = to;
		this.value = value;
	}

	@Override
	public int compareTo(ProbVal<T> o)
	{
		return to.compareTo(o.to);
	}

}
