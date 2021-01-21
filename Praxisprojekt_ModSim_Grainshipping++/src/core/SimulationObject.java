package core;

public abstract class SimulationObject
{
	private Long timeUtilized = 0l;
	private Long utilStart = null;

	
	// each SimulationObject is simulated (until it returns false = no event was submitted)
	public abstract boolean simulate(long timeStep);
	
	
	public void utilStart(long timeStep)
	{
		utilStart = timeStep;
	}
	
	
	public void utilStop(long timeStep)
	{
		timeUtilized += timeStep - utilStart;
	}


	public void setTimeUtilized(long timeStep)
	{
		timeUtilized += timeStep;
	}
	
	
	public Long getTimedUtilized()
	{
		return timeUtilized;
	}

	
	public long addUtilization(long timeUtilizedDelta)
	{
		timeUtilized += timeUtilizedDelta;
		return timeUtilizedDelta;
	}
	
}
