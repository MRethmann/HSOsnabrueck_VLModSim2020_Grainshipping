package model_grainshipping;

import core.*;

public class GasStation extends SimulationObject
{
    private String name = null;
    private Truck truckCurrentlyRefueled = null;

    private static Randomizer refuelTime = null;
    private static Randomizer roadToGasStation = null;


    private static EventQueue eventQueue = EventQueue.getInstance();

    public GasStation(String name)
    {
        this.name = name;

        refuelTime = new Randomizer();
        refuelTime.addProbInt(0.1, 5);
        refuelTime.addProbInt(0.2, 6);
        refuelTime.addProbInt(0.3, 7);
        refuelTime.addProbInt(0.4, 8);
        refuelTime.addProbInt(0.5, 9);
        refuelTime.addProbInt(0.6, 10);
        refuelTime.addProbInt(0.7, 11);
        refuelTime.addProbInt(0.8, 12);
        refuelTime.addProbInt(0.9, 13);
        refuelTime.addProbInt(1, 14);

        roadToGasStation = new Randomizer();
        roadToGasStation.addProbInt(0.25, 2);
        roadToGasStation.addProbInt(0.5, 4);
        roadToGasStation.addProbInt(0.75, 6);
        roadToGasStation.addProbInt(1, 8);

        SimulationObjects.getInstance().add(this);
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return "Refueld in: " + this.name;
    }

    @Override
    public boolean simulate(long timeStep)
    {
        //TODO: Refueling
        Event event = eventQueue.next(timeStep, true, GrainShippingEvents.Refueling, GrainShipping.refuelQueue.pollFirst(), this.getClass(), null);

        if (truckCurrentlyRefueled == null
                && event != null
                &&event.getObjectAttached() != null
                &&event.getObjectAttached().getClass() == Truck.class)
        {
            eventQueue.remove(event);

            truckCurrentlyRefueled = (Truck) event.getObjectAttached();
            truckCurrentlyRefueled.increaseRefuelingCosts(((1000 - truckCurrentlyRefueled.getRemainingTank()) * 1.27));
            truckCurrentlyRefueled.setRemainingTank((double) 1000);
            eventQueue.add(new Event(timeStep + refuelTime.nextInt(), GrainShippingEvents.RefuelingDone, truckCurrentlyRefueled, null, this));

            utilStart(timeStep);
            return true;
        }

        event = eventQueue.next(timeStep, true, GrainShippingEvents.RefuelingDone, null, null, this);
        if (event != null
                && event.getObjectAttached() != null
                && event.getObjectAttached().getClass() == Truck.class)
        {
            eventQueue.remove(event);

            long driveToGasstation = roadToGasStation.nextInt();
            if (truckCurrentlyRefueled.getPreviousEvent() == GrainShippingEvents.LoadingDone)
            {
                eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()),
                        GrainShippingEvents.EntranceWeighing, truckCurrentlyRefueled, EntranceWeighingStation.class, null));
            }
            else if (truckCurrentlyRefueled.getPreviousEvent() == GrainShippingEvents.EntranceWeighingDone)
            {
                eventQueue.add(new Event(timeStep + driveToGasstation,
                        GrainShippingEvents.Unloading, truckCurrentlyRefueled, UnloadingDock.class, null));
            }
            else if (truckCurrentlyRefueled.getPreviousEvent() == GrainShippingEvents.UnloadingDone)
            {
                eventQueue.add(new Event(timeStep + driveToGasstation,
                        GrainShippingEvents.ExitWeighing, truckCurrentlyRefueled, ExitWeighingStation.class, null));
            }
            else if (truckCurrentlyRefueled.getPreviousEvent() == GrainShippingEvents.ExitWeighingDone)
            {
                if (LoadingDock.currentAssignment.getLoadingCapacity() > 0 && GrainShipping.grainToShip > 0)
                {
                    if (truckCurrentlyRefueled.getCurrentDriver().checkDrivingPermission()) //Trucker muss abschätzen ob er noch eine Fahrt machen kann
                    {
                        eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()), GrainShippingEvents.Loading,
                                truckCurrentlyRefueled, LoadingDock.class, null));
                        truckCurrentlyRefueled.runEnd(timeStep + driveToGasstation);
                    }
                    else
                    {
                        SpeditionSite.returnToSpedition(timeStep, truckCurrentlyRefueled, driveToGasstation);
                    }
                }
                else if (GrainShipping.grainToShip > 0)
                {
                    LoadingDock.assignmentcount += 1;
                    eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()), GrainShippingEvents.Loading,
                            truckCurrentlyRefueled, LoadingDock.class, null));
                    truckCurrentlyRefueled.runEnd(timeStep);
                }
                else
                {
                    SpeditionSite.returnToSpedition(timeStep, truckCurrentlyRefueled, driveToGasstation);
                }
            }
            else
            {
                System.out.println("Fehler Klasse Gasstation: Zeile 141!");
            }

            truckCurrentlyRefueled = null;
            utilStop(timeStep);
            return true;
        }
        return false;
    }
}
