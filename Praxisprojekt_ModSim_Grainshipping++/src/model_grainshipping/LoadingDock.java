package model_grainshipping;

import core.*;

//ToDo: Zusammenführung Loading- und Unloadingdock zu einer Klasse
public class LoadingDock extends SimulationObject
{
	private String name = null;
	private Truck truckCurrentlyLoaded = null;
	public static Integer assignmentcount = 0;
	public static Assignment currentAssignment = null;
	public static Integer lowestCapacity = null;

	private static EventQueue eventQueue = EventQueue.getInstance();

	private static Randomizer loadingWeight = null;
	private static Randomizer loadingTime = null;
	private static Randomizer drivingToEntranceWeighingStationTime = null;


	public LoadingDock(String name)
	{
		this.name = name;

		loadingWeight = new Randomizer();
		loadingWeight.addProbInt(0.3, 25);
		loadingWeight.addProbInt(0.6, 26);
		loadingWeight.addProbInt(1.0, 27); //Sollten wir entsprechend anpassen

		loadingTime = new Randomizer();
		loadingTime.addProbInt(0.1, 45);
		loadingTime.addProbInt(0.2, 46);
		loadingTime.addProbInt(0.3, 47);
		loadingTime.addProbInt(0.4, 48);
		loadingTime.addProbInt(0.5, 49);
		loadingTime.addProbInt(0.6, 50);
		loadingTime.addProbInt(0.7, 51);
		loadingTime.addProbInt(0.8, 52);
		loadingTime.addProbInt(0.9, 53);
		loadingTime.addProbInt(1.0, 54);


		SimulationObjects.getInstance().add(this);
	}

	@Override
	public String toString()
	{
		return "Loading Dock: " + name + " Truck: " + (truckCurrentlyLoaded != null ? truckCurrentlyLoaded : "---");
	}

	@Override
	public boolean simulate(long timeStep)
	{
		if (truckCurrentlyLoaded == null)
		{
			Event event = eventQueue.next(timeStep, true, GrainShippingEvents.Loading, GrainShipping.loadingQueue.pollFirst(), this.getClass(), null);

			if (event != null
					&& event.getObjectAttached() != null
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				truckCurrentlyLoaded = (Truck) event.getObjectAttached();
				truckCurrentlyLoaded.runStart(timeStep);
				currentAssignment = Assignment.Assignments.get(assignmentcount);
				lowestCapacity = (Math.min(currentAssignment.getLoadingCapacity() , GrainShipping.grainToShip));
				truckCurrentlyLoaded.load(Math.min(loadingWeight.nextInt(), lowestCapacity));
				currentAssignment.lowerLoadingCapacity(truckCurrentlyLoaded.getTare_weight());
				GrainShipping.grainToShip-=truckCurrentlyLoaded.getTare_weight();//Bruttogewicht des Trucks wird gesetzt
				eventQueue.add(new Event(timeStep + truckCurrentlyLoaded.addUtilization(loadingTime.nextInt()),
						GrainShippingEvents.LoadingDone, truckCurrentlyLoaded, null, this));

				truckCurrentlyLoaded.runStart(timeStep);
				utilStart(timeStep);
				return true;
			}
		}
		else
		{
			Event event = eventQueue.next(timeStep, true, GrainShippingEvents.LoadingDone,null, null, this);
			if (event != null
					&& event.getObjectAttached() != null
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				truckCurrentlyLoaded.wearDurability(currentAssignment.getWear(), (double) currentAssignment.getRoutingTime());
				truckCurrentlyLoaded.consumeRemainingTank((double) currentAssignment.getRoutingTime());

				if (truckCurrentlyLoaded.getDurability() < 0)
				{
					System.out.println(truckCurrentlyLoaded + " needs to be repaired!");
					truckCurrentlyLoaded.repairTruck(timeStep, event.getDescription(), truckCurrentlyLoaded);
				}
				else if (truckCurrentlyLoaded.getRemainingTank() < 500)
				{
					System.out.println(truckCurrentlyLoaded + " needs to be refueled!");
					truckCurrentlyLoaded.refuelTruck(timeStep, event.getDescription(), truckCurrentlyLoaded);
				}
				else
				{
					eventQueue.add(new Event(timeStep + event.getObjectAttached().addUtilization(currentAssignment.getRoutingTime()),
							GrainShippingEvents.EntranceWeighing, truckCurrentlyLoaded, EntranceWeighingStation.class, null));
				}

				truckCurrentlyLoaded = null;
				utilStop(timeStep);
				return true;
			}
		}
		return false;
	}
}
