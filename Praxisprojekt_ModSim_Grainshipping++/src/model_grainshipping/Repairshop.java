package model_grainshipping;

import core.*;


public class Repairshop extends SimulationObject
{
    private String name = null;
    private static Randomizer repairingTime = null;
    private static Randomizer roadToRepairshop = null;
    private static Randomizer repairCosts = null;
    private Truck truckCurrentlyRepaired = null;

    private static EventQueue eventQueue = EventQueue.getInstance();


    public Repairshop(String name)
    {
        super();
        this.name = name;

        repairingTime = new Randomizer();
        repairingTime.addProbInt(0.45, 180); //Truck kann vor Ort repariert werden
        repairingTime.addProbInt(0.9, 360); //
        repairingTime.addProbInt(1.0, 2880); //Aufwendige Reparatur

        repairCosts = new Randomizer();
        repairCosts.addProbInt(0.4, 800);
        repairCosts.addProbInt(0.8, 1100);
        repairCosts.addProbInt(1.0, 15000);

        roadToRepairshop = new Randomizer(); //ToDo: Strecke Repairshop -> LoadingDock hinzufügen
        roadToRepairshop.addProbInt(0.5, 120);
        roadToRepairshop.addProbInt(0.8, 150);
        roadToRepairshop.addProbInt(1.0, 180);

        SimulationObjects.getInstance().add(this);
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "Repairshop: " + name + " Truck: " + (truckCurrentlyRepaired != null ? truckCurrentlyRepaired : "---");
    }

    @Override
    public boolean simulate(long timeStep)
    {
        Event event = eventQueue.next(timeStep, true, GrainShippingEvents.Repairing, GrainShipping.repairingQueue.pollFirst(), this.getClass(), null);
        if (truckCurrentlyRepaired == null
                && event != null
                && event.getObjectAttached() != null
                && event.getObjectAttached().getClass() == Truck.class)
        {

            EventQueue.getInstance().remove(event);
            truckCurrentlyRepaired = (Truck) event.getObjectAttached();
            truckCurrentlyRepaired.increaseRepairCosts((double) repairCosts.nextInt());
            truckCurrentlyRepaired.setDurability((double) 100);
            truckCurrentlyRepaired.decreaseTimeToLive();
            if (truckCurrentlyRepaired.getIntTimetolive() < 1)
            {
                truckCurrentlyRepaired.increaseRepairCosts((double) 150000);
                GrainShipping.newTrucksBought++;
                truckCurrentlyRepaired.resetTimeToLive();
            }
            Integer repairTime = repairingTime.nextInt();
            truckCurrentlyRepaired.increaseTotalRepairtime(repairTime);
            eventQueue.add(new Event(timeStep + repairTime,
                    GrainShippingEvents.RepairingDone, truckCurrentlyRepaired, null, this));

            utilStart(timeStep);
            return true;
        }

        event = eventQueue.next(timeStep, true, GrainShippingEvents.RepairingDone, null, null, this);
        if (event != null
                && event.getObjectAttached() != null
                && event.getObjectAttached().getClass() == Truck.class)
        {
            eventQueue.remove(event);
            long driveToRepairshop = roadToRepairshop.nextInt();
            if (truckCurrentlyRepaired.getCurrentDriver() != null)
            {
                if (truckCurrentlyRepaired.getPreviousEvent() == GrainShippingEvents.LoadingDone)
                {
                    eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()),
                            GrainShippingEvents.EntranceWeighing, truckCurrentlyRepaired, EntranceWeighingStation.class, null));
                }
                else if (truckCurrentlyRepaired.getPreviousEvent() == GrainShippingEvents.EntranceWeighingDone)
                {
                    eventQueue.add(new Event(timeStep + driveToRepairshop,
                            GrainShippingEvents.Unloading, truckCurrentlyRepaired, UnloadingDock.class, null));
                }
                else if (truckCurrentlyRepaired.getPreviousEvent() == GrainShippingEvents.UnloadingDone)
                {
                    eventQueue.add(new Event(timeStep + driveToRepairshop,
                            GrainShippingEvents.ExitWeighing, truckCurrentlyRepaired, ExitWeighingStation.class, null));
                }
                else if (truckCurrentlyRepaired.getPreviousEvent() == GrainShippingEvents.ExitWeighingDone)
                {
                    if (LoadingDock.currentAssignment.getLoadingCapacity() > 0 && GrainShipping.grainToShip > 0)
                    {
                        if (truckCurrentlyRepaired.getCurrentDriver().checkDrivingPermission()) //Trucker muss abschätzen ob er noch eine Fahrt machen kann
                        {
                            eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()), GrainShippingEvents.Loading,
                                    truckCurrentlyRepaired, LoadingDock.class, null));
                            truckCurrentlyRepaired.runEnd(timeStep + (roadToRepairshop.nextInt()));
                        }
                        else
                        {
                            SpeditionSite.returnToSpedition(timeStep, truckCurrentlyRepaired, driveToRepairshop);
                        }
                    }
                    else if (GrainShipping.grainToShip > 0)
                    {
                        LoadingDock.assignmentcount += 1;
                        eventQueue.add(new Event(timeStep + (LoadingDock.currentAssignment.getRoutingTime()), GrainShippingEvents.Loading,
                                truckCurrentlyRepaired, LoadingDock.class, null));
                    }
                    else
                    {
                        SpeditionSite.returnToSpedition(timeStep, truckCurrentlyRepaired, driveToRepairshop);
                    }
                }
            }
            truckCurrentlyRepaired = null;
            utilStop(timeStep);
            return true;
        }
        return false;
    }



}