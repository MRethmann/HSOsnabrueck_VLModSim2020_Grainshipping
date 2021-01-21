package model_grainshipping;

import core.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;

public class GrainShipping extends Simulation
{
	public static Integer grainToShip = 100000;
	public static Integer unsuccessfullLoadingSizes = 0;
	public static Integer unsuccessfullLoadings = 0;
	public static long totalDurationOfRuns = 0;
	public static long meanDurationPerRun = 0;
	public static double totalCosts = 0.0;
	public static double totalIncome = 0.0;
	public static final int WORKERWAGE = 22;
	public static double profitPerTon = 8.47;
	public static int newTrucksBought = 0;


	public static Integer grainShipped = 0;
	public static Integer successfullLoadingSizes = 0;
	public static int successfullLoadings = 0;

	private static final Integer GRAIN_TO_SHIP_INITIALLY = grainToShip;
	private static final int NUM_TRUCKS = 12;
	private static final int NUM_EMPLOYEES = 18;
	private static final int NUM_LOADING_DOCKS = 2;
	private static final int NUM_UNLOADING_DOCKS = 6;
	private static final int NUM_ENTRANCEWEIGHING_STATIONS = 1;
	private static final int NUM_EXITWEIGHING_STATIONS = 1;
	private static final int NUM_REPAIRSHOPS = 2;
	private static final int NUM_GAS_STATIONS = 2;
	private static final int NUM_ASSIGNMENTS = ((grainToShip / 100) * 2);

	public static Queue entranceWeighingQueue = new Queue("EntranceWeighingQueue");
	public static Queue exitWeighingQueue = new Queue("ExitWeighingQueue");
	public static Queue loadingQueue = new Queue("LoadingQueue");
	public static Queue unLoadingQueue = new Queue("UnloadingQueue");
	public static Queue repairingQueue = new Queue("RepairingQueue");
	public static Queue refuelQueue = new Queue("RefuelQueue");

	public static void main(String[] args)
	{
		EventQueue eventQueue = EventQueue.getInstance();

		new SpeditionSite("SpeditionSite");

		for (int i = 0; i < NUM_EMPLOYEES; i++)
			new Employee("Mitarbeiter_" + i, i);

		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("Loading-D-" + i);

		for (int i = 0; i < NUM_UNLOADING_DOCKS; i++)
			new UnloadingDock("Unloading-D-" + i);

		for (int i = 0; i < NUM_ENTRANCEWEIGHING_STATIONS; i++)
			new EntranceWeighingStation("Entrance-WS-" + i);

		for (int i = 0; i < NUM_EXITWEIGHING_STATIONS; i++)
			new ExitWeighingStation("Exit-WS-" + i);

		for (int i = 0; i < NUM_REPAIRSHOPS; i++)
			new Repairshop("Repairshop-" + i);

		for (int i = 0; i < NUM_GAS_STATIONS; i++)
			new GasStation("Gas Station-" + i);

		for (int i = 0; i < NUM_ASSIGNMENTS; i++)
			new Assignment("Assignment-" + i);

		for (int i = 0; i < NUM_TRUCKS; i++)
		{
			Truck truck = new Truck ("T" + i);
			if (truck.getCurrentDriver() != null)
			{
				eventQueue.add(new Event(0l, GrainShippingEvents.Loading, truck, LoadingDock.class, null));
			}
			else
			{
				eventQueue.add(new Event(0l, GrainShippingEvents.Idle, truck, SpeditionSite.class, null));
				System.out.println("Für Truck " + truck + " ist kein freier Fahrer vorhanden!");
			}
		}

		GrainShipping gs = new GrainShipping();
		long timeStep = gs.simulate();
		//System.out.println(EventQueue.getInstance());

		// output some gravel shipping stats
		System.out.println("Grain shipped\t\t\t\t= " + grainShipped + "t");
		System.out.println("Grain to ship\t\t\t\t= " + grainToShip + "t");
		System.out.println("Total simulation time\t\t= " + round((double) timeStep / 60, 2) + " hours");
		System.out.println("Mean time per grain unit\t= " + round((double) timeStep / grainShipped, 3) + " minutes");
		System.out.println("Mean Duration Per Run\t\t= " + meanDurationPerRun + " minutes");

		System.out.println(String.format("Total unloadings\t\t\t= %d(%.2f%%), mean size %.2ft", successfullLoadings,
				(double) successfullLoadings / (successfullLoadings + unsuccessfullLoadingSizes) * 100,
				(double) successfullLoadingSizes / successfullLoadings));
		System.out.println("------------------------------------");



		for (int i = 0; i < Employee.Employees.size(); i++)
		{
			Collections.sort(Employee.Employees, new Comparator<Employee>()
					{
						@Override
						public int compare(Employee e1, Employee e2)
						{
							return Integer.valueOf(e1.getSortVariable()).compareTo(e2.getSortVariable());
						}
					}
			);
			Employee e = Employee.Employees.get(i);
			System.out.print(e.getMitarbeiterID() + ":\t\t|\t\tWorking time: " + String.format("%5s", round(e.getTotalWorkingTime() / 60, 2)) + " hours in " + e.getWorkingSessions() + " sessions" + "\t\t|\t\t");
			System.out.print("Resting Time: " + String.format("%5s", round(e.getTotalRestingTime() / 60, 2)) + " hours in " + e.getRestingSessions() + " sessions" + "\t\t|\t\t");
			System.out.println("Wage: " + (e.getTotalWorkingTime() / 60) * WORKERWAGE + "€");
			totalCosts += (e.getTotalWorkingTime() / 60) * WORKERWAGE;
		}

		System.out.println("------------------------------------");
		for (int i = 0; i < SimulationObjects.getInstance().size(); i++)
		{
			SimulationObject t = SimulationObjects.getInstance().get(i);
			if (t.getClass() == Truck.class)
			{
				GrainShipping.totalCosts -= ((Truck) t).getTotalRepairTime() * WORKERWAGE; //Workaround for Emyploee costs
				System.out.print("Truck " + t.toString() + "\t\t|\t\tRefueling Costs: " + String.format("%8s", round(((Truck) t).getRefuelingCosts(),2)) + "€");
				System.out.print("\t\t|\t\tRepairing Costs: " + String.format("%8s", round(((Truck) t).getRepairCosts(),2)) + "€");
				System.out.print("\t\t|\t\tTotal Costs: " + String.format("%8s", round(((Truck) t).getRepairCosts() + (((Truck) t).getRefuelingCosts()),2)) + "€");
				System.out.println("\t\t|\t\tTotal Driving Route: " + String.format("%8s", round(((Truck) t).getTotalDrivingTime(),2)) + " km");
				totalCosts += ((Truck) t).getRefuelingCosts() + ((Truck) t).getRepairCosts();
			}
		}
		System.out.println("------------------------------------");
		System.out.println("Total Income\t\t= " + String.format("%.2f", totalIncome) + "€");
		System.out.println("Total Costs\t\t\t= " + String.format("%.2f", totalCosts) + "€");
		System.out.println("Outcome\t\t\t\t= " + String.format("%.2f", totalIncome - totalCosts) + "€");
	}

	@Override
	protected void printEveryStep()
	{
		System.out.println(String.format(" shipped/toShip : %dt(%.2f%%) / %dt", grainShipped, (double) grainShipped / GRAIN_TO_SHIP_INITIALLY * 100, grainToShip));
	}

	public static int getNumEmployees()
	{
		return NUM_EMPLOYEES;
	}
	public static int getNumTrucks() {
		return NUM_TRUCKS;
	}
	public static int getNumAssignments()
	{
		return NUM_ASSIGNMENTS;
	}
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	} //Source: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places

	@Override
	public long simulate()
	{
		EventQueue eventqueue = EventQueue.getInstance();
		SimulationObjects simulationObjects = SimulationObjects.getInstance();

		long numberOfSteps = 1;
		long timeStep = 0;

		Event e = null;


		// as long as events are in queue
		do
		{
			System.out.print(numberOfSteps++ + ". " + Time.stepsToString(timeStep) + " " + eventqueue);
			printEveryStep();

			// at least one simulationobject did something = consumes events, creates events
			boolean oneSimulationObjectDidSomething;
			do {
				oneSimulationObjectDidSomething = false;

				// each simulation object is asked to simulate itself
				for (SimulationObject so : simulationObjects)
				{
					if (so.simulate(timeStep))
					{
						oneSimulationObjectDidSomething = true;
						System.out.println("= " + so);
					}
				}

			} while (oneSimulationObjectDidSomething);

			for (int i = 0; i < Employee.Employees.size(); i++)
			{
				Employee employee = Employee.Employees.get(i);
				if (employee.getCurrentlyWorking() == true)
				{
					employee.increaseWorkingSessionTime(timeStep - employee.getUtilWorkStart());
					employee.increaseTotalWorkingTime(timeStep - employee.getUtilWorkStart());
				}
				else if (employee.getCurrentlyWorking() == false)
				{
					employee.increaseRestingSessionTime(timeStep - employee.getUtilRestStart());
					employee.increaseTotalRestingTime(timeStep - employee.getUtilRestStart());
				}
				employee.setUtilRestStart(timeStep);
				employee.setUtilWorkStart(timeStep);
			}


			System.out.println();
			System.out.println("Loading Queue: " + GrainShipping.loadingQueue.toString());
			System.out.println("Entrance Weighing Queue: " + GrainShipping.entranceWeighingQueue.toString());
			System.out.println("Unloading Queue: " + GrainShipping.unLoadingQueue.toString());
			System.out.println("Exit Weighing Queue: " + GrainShipping.exitWeighingQueue.toString());
			System.out.println("Repairing Queue: " + GrainShipping.repairingQueue.toString());
			System.out.println("Idle Trucks: " + SpeditionSite.idleTrucks.toString());
			System.out.println();
			System.out.println("__________________________________________________________________________");
			System.out.println();


			// progress time a little
			timeStep++;
			// switch time to next event
			e = eventqueue.next(timeStep, false, null, null,null, null);
			if (e != null)
				timeStep = e.getTimeStep();

		} while (e != null
				|| GrainShipping.grainToShip > 0);

		timeStep--; // correction after last step / no event source
		printPostSimStats(timeStep);
		return timeStep;
	}
}
