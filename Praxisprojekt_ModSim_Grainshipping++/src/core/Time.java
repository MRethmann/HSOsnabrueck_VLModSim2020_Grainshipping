package core;

public class Time
{

	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;
	
	public static String stepsToString(Long timeStepInMinutes)
	{
		StringBuilder timeStringBuild = new StringBuilder();
		
		timeStepInMinutes = computeSubTimeString(timeStepInMinutes, timeStringBuild, MINUTES_PER_DAY, "d");
		timeStepInMinutes = computeSubTimeString(timeStepInMinutes, timeStringBuild, MINUTES_PER_HOUR, "h");
		timeStepInMinutes = computeSubTimeString(timeStepInMinutes, timeStringBuild, 1, "m");
		
		return timeStringBuild.toString();
	}

	private static Long computeSubTimeString(Long timeStepInMinutes, StringBuilder timeStringBuild, int minutesPerTimeUnit, String timeAppendix)
	{
		long timeUnit = timeStepInMinutes / minutesPerTimeUnit;
		if (timeUnit >= 0)
		{
			if (timeStringBuild.length() > 0)
				timeStringBuild.append(":");
			
			timeStringBuild.append(timeUnit + timeAppendix);
			timeStepInMinutes -= timeUnit * minutesPerTimeUnit;
		}
		
		return timeStepInMinutes;
	}
	
}
