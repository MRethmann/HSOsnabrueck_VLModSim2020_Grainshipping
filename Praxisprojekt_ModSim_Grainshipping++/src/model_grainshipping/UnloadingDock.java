package model_grainshipping;

import core.Event;
import core.EventQueue;
import core.Randomizer;
import core.SimulationObject;
import core.SimulationObjects;

public class UnloadingDock extends SimulationObject
{
	private String name = null;
	private Truck truckCurrentlyUnloaded = null;

	private static EventQueue eventQueue = EventQueue.getInstance();

	private static Randomizer loadingWeight = null;
	private static Randomizer unLoadingTime = null;
	private static Randomizer drivingToExitWeighingStationTime = null;


	public UnloadingDock(String name)
	{
		this.name = name;

		unLoadingTime = new Randomizer();
		unLoadingTime.addProbInt(0.1, 45);
		unLoadingTime.addProbInt(0.2, 46);
		unLoadingTime.addProbInt(0.3, 47);
		unLoadingTime.addProbInt(0.4, 48);
		unLoadingTime.addProbInt(0.5, 49);
		unLoadingTime.addProbInt(0.6, 50);
		unLoadingTime.addProbInt(0.7, 51);
		unLoadingTime.addProbInt(0.8, 52);
		unLoadingTime.addProbInt(0.9, 53);
		unLoadingTime.addProbInt(1.0, 54);

		drivingToExitWeighingStationTime = new Randomizer();
		drivingToExitWeighingStationTime.addProbInt(0.1, 1);
		drivingToExitWeighingStationTime.addProbInt(0.2, 2);
		drivingToExitWeighingStationTime.addProbInt(0.3, 3);
		drivingToExitWeighingStationTime.addProbInt(0.4, 4);
		drivingToExitWeighingStationTime.addProbInt(0.5, 5);
		drivingToExitWeighingStationTime.addProbInt(0.6, 6);
		drivingToExitWeighingStationTime.addProbInt(0.7, 7);
		drivingToExitWeighingStationTime.addProbInt(0.8, 8);
		drivingToExitWeighingStationTime.addProbInt(0.9, 9);
		drivingToExitWeighingStationTime.addProbInt(1.0, 10);

		SimulationObjects.getInstance().add(this);
	}


	@Override
	public String toString()
	{
		return "Unloading Dock: " + name + " Truck: " + (truckCurrentlyUnloaded != null ? truckCurrentlyUnloaded : "---");
	}


	@Override
	public boolean simulate(long timeStep)
	{
		if (truckCurrentlyUnloaded == null)
		{
			Event event = eventQueue.next(timeStep, true, GrainShippingEvents.Unloading, GrainShipping.unLoadingQueue.pollFirst(), this.getClass(), null);

			if (event != null
					&& event.getObjectAttached() != null
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				truckCurrentlyUnloaded = (Truck) event.getObjectAttached();
				eventQueue.add(new Event(timeStep + truckCurrentlyUnloaded.addUtilization(unLoadingTime.nextInt()),
						GrainShippingEvents.UnloadingDone, truckCurrentlyUnloaded, null, this));

				utilStart(timeStep);
				return true;
			}
		}
		else
		{
			Event event = eventQueue.next(timeStep, true, GrainShippingEvents.UnloadingDone, null, null, this);
			if (event != null
					&& event.getObjectAttached() != null
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				Integer driveToExitWeighing = drivingToExitWeighingStationTime.nextInt();
				truckCurrentlyUnloaded.consumeRemainingTank((double) driveToExitWeighing);
				truckCurrentlyUnloaded.wearDurability(null, (double) driveToExitWeighing);

				if (truckCurrentlyUnloaded.getDurability() < 0)
				{
					System.out.println(truckCurrentlyUnloaded + " needs to be repaired!");
					truckCurrentlyUnloaded.repairTruck(timeStep, event.getDescription(), truckCurrentlyUnloaded);
				}
				else if (truckCurrentlyUnloaded.getRemainingTank() < 50)
				{
					System.out.println(truckCurrentlyUnloaded + " needs to be refueled!");
					truckCurrentlyUnloaded.refuelTruck(timeStep, event.getDescription(), truckCurrentlyUnloaded);
				}
				else
				{
					eventQueue.add(new Event(timeStep + truckCurrentlyUnloaded.addUtilization(driveToExitWeighing),
							GrainShippingEvents.ExitWeighing, truckCurrentlyUnloaded, ExitWeighingStation.class, null));
				}

				truckCurrentlyUnloaded = null;
				utilStop(timeStep);
				return true;
			}
		}
		return false;
	}
}

