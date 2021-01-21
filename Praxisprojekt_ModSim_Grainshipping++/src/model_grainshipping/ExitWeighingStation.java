package model_grainshipping;

import core.*;

public class ExitWeighingStation extends SimulationObject
{
	private static final int TIME_TO_WEIGH_TRUCK = 10;
	private static final int MAXLOAD = 40;

	private String name = null;
	private Truck truckCurrentlyWeighted = null;

	private static EventQueue eventQueue = EventQueue.getInstance();

	private static Randomizer drivingToLoadingDock  = null;


	public ExitWeighingStation(String name)
	{
		this.name = name;
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
		Event event = eventQueue.next(timeStep, true, GrainShippingEvents.ExitWeighing, GrainShipping.exitWeighingQueue.pollFirst(), this.getClass(), null);
		if (truckCurrentlyWeighted == null
				&& event != null
				&& event.getObjectAttached() != null
				&& event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);

			truckCurrentlyWeighted = (Truck) event.getObjectAttached();
			eventQueue.add(new Event(timeStep + truckCurrentlyWeighted.addUtilization(TIME_TO_WEIGH_TRUCK),
					GrainShippingEvents.ExitWeighingDone, truckCurrentlyWeighted, null, this));
			utilStart(timeStep);
			return true;
		}

		event = eventQueue.next(timeStep, true, GrainShippingEvents.ExitWeighingDone, null, null, this);
		if (event != null
				&& event.getObjectAttached() != null
				&& event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);
			long driveToLoadingDock = 0;

			driveToLoadingDock = truckCurrentlyWeighted.addUtilization(LoadingDock.currentAssignment.getRoutingTime());
			GrainShipping.totalIncome += GrainShipping.profitPerTon * LoadingDock.currentAssignment.getPricePerTon() * truckCurrentlyWeighted.getTare_weight();
			GrainShipping.grainShipped += truckCurrentlyWeighted.getTare_weight();
			GrainShipping.successfullLoadingSizes += truckCurrentlyWeighted.getTare_weight();
			GrainShipping.successfullLoadings++;
			truckCurrentlyWeighted.runEnd(timeStep + driveToLoadingDock);
			truckCurrentlyWeighted.unload();
			truckCurrentlyWeighted.wearDurability(LoadingDock.currentAssignment.getWear(), (double) driveToLoadingDock);
			truckCurrentlyWeighted.consumeRemainingTank((double) driveToLoadingDock);

			if (truckCurrentlyWeighted.getDurability() < 0 )
			{
				System.out.println(truckCurrentlyWeighted + " needs to be repaired!");
				truckCurrentlyWeighted.repairTruck(timeStep, event.getDescription(), truckCurrentlyWeighted);
			}
			else if (truckCurrentlyWeighted.getRemainingTank() < 50)
			{
				System.out.println(truckCurrentlyWeighted + " needs to be refueled!");
				truckCurrentlyWeighted.refuelTruck(timeStep, event.getDescription(), truckCurrentlyWeighted);
			}
			else if (LoadingDock.currentAssignment.getLoadingCapacity() > 0 && GrainShipping.grainToShip > 0)
			{
				if ((truckCurrentlyWeighted.getCurrentDriver().checkDrivingPermission()) == true) //Trucker muss abschätzen ob er noch eine Fahrt machen kann
				{
					eventQueue.add(new Event(timeStep + driveToLoadingDock, GrainShippingEvents.Loading,
							truckCurrentlyWeighted, LoadingDock.class, null));
				}
				else
				{
					SpeditionSite.returnToSpedition(timeStep, truckCurrentlyWeighted, driveToLoadingDock);
				}
			}
			else if (GrainShipping.grainToShip > 0)
			{
				LoadingDock.assignmentcount += 1;
				eventQueue.add(new Event(timeStep + driveToLoadingDock, GrainShippingEvents.Loading,
						truckCurrentlyWeighted, LoadingDock.class, null));
			}
			else if (GrainShipping.grainToShip <= 0)
			{
				SpeditionSite.returnToSpedition(timeStep, truckCurrentlyWeighted, driveToLoadingDock);
			}

			truckCurrentlyWeighted = null;
			utilStop(timeStep);
			return true;
		}
		return false;
	}
}
