package model_grainshipping;

import core.Event;
import core.EventQueue;
import core.Randomizer;
import core.SimulationObject;
import core.SimulationObjects;

//ToDo: Zusammenführung Entrance- und ExitWeighingStation zu einer Klasse
public class EntranceWeighingStation extends SimulationObject
{
	private static final int TIME_TO_WEIGH_TRUCK = 5;
	private static final int MAXLOAD = 40;

	private String name = null;
	private Truck truckCurrentlyWeighted = null;

	private static EventQueue eventQueue = EventQueue.getInstance();

	private static Randomizer drivingToUnloadingDock = null;

	public EntranceWeighingStation(String name)
	{
		this.name = name;

		drivingToUnloadingDock = new Randomizer();
		drivingToUnloadingDock.addProbInt(0.1, 1);
		drivingToUnloadingDock.addProbInt(0.2, 2);
		drivingToUnloadingDock.addProbInt(0.3, 3);
		drivingToUnloadingDock.addProbInt(0.4, 4);
		drivingToUnloadingDock.addProbInt(0.5, 5);
		drivingToUnloadingDock.addProbInt(0.6, 6);
		drivingToUnloadingDock.addProbInt(0.7, 7);
		drivingToUnloadingDock.addProbInt(0.8, 8);
		drivingToUnloadingDock.addProbInt(0.9, 9);
		drivingToUnloadingDock.addProbInt(1.0, 10);

		SimulationObjects.getInstance().add(this);
	}


	@Override
	public String toString()
	{
		return "Weighing Station: " + name + " Truck: " + (truckCurrentlyWeighted != null ? truckCurrentlyWeighted : "---");
	}


	// TODO: change logic
	@Override
	public boolean simulate(long timeStep)
	{
		Event event = eventQueue.next(timeStep, true, GrainShippingEvents.EntranceWeighing, GrainShipping.entranceWeighingQueue.pollFirst(), this.getClass(), null);
		if (truckCurrentlyWeighted == null
				&& event != null
				&& event.getObjectAttached() != null
				&& event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);

			truckCurrentlyWeighted = (Truck) event.getObjectAttached();
			eventQueue.add(new Event(timeStep + truckCurrentlyWeighted.addUtilization(TIME_TO_WEIGH_TRUCK),
					GrainShippingEvents.EntranceWeighingDone, truckCurrentlyWeighted, null, this));
			utilStart(timeStep);
			return true;
		}

		event = eventQueue.next(timeStep, true, GrainShippingEvents.EntranceWeighingDone, null, null, this);
		if (event != null
				&& event.getObjectAttached() != null
				&& event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);
			long driveToUnLoadingDock = 0;
			driveToUnLoadingDock = truckCurrentlyWeighted.addUtilization(drivingToUnloadingDock.nextInt());
			truckCurrentlyWeighted.wearDurability(null, (double) driveToUnLoadingDock);
			truckCurrentlyWeighted.consumeRemainingTank((double) driveToUnLoadingDock);

			if (truckCurrentlyWeighted.getDurability() < 0)
			{
				System.out.println(truckCurrentlyWeighted + " needs to be repaired!");
				truckCurrentlyWeighted.repairTruck(timeStep, event.getDescription(), truckCurrentlyWeighted);
			}
			else if (truckCurrentlyWeighted.getRemainingTank() < 50)
			{
				System.out.println(truckCurrentlyWeighted + " needs to be refueled!");
				truckCurrentlyWeighted.refuelTruck(timeStep, event.getDescription(), truckCurrentlyWeighted);
			}
			else
			{
				eventQueue.add(new Event(timeStep + driveToUnLoadingDock, GrainShippingEvents.Unloading,
						truckCurrentlyWeighted, UnloadingDock.class, null));
			}
			truckCurrentlyWeighted = null;
			utilStop(timeStep);
			return true;
		}

		return false;
	}
}
