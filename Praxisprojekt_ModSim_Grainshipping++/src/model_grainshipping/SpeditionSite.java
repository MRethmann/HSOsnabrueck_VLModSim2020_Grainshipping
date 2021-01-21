package model_grainshipping;

import core.*;

import java.util.ArrayList;

public class SpeditionSite extends SimulationObject
{

    private String name = null;
    private Truck truckCurrentlyIdle = null;

    private static EventQueue eventQueue = EventQueue.getInstance();
    public static ArrayList<Truck> idleTrucks = new ArrayList<>(GrainShipping.getNumTrucks());

    private static Randomizer drivingToLoadingDockTime = null;

    public SpeditionSite(String name)
    {
        this.name = name;

        drivingToLoadingDockTime = new Randomizer();
        drivingToLoadingDockTime.addProbInt(0.1, 30);
        drivingToLoadingDockTime.addProbInt(0.2, 31);
        drivingToLoadingDockTime.addProbInt(0.3, 32);
        drivingToLoadingDockTime.addProbInt(0.4, 33);
        drivingToLoadingDockTime.addProbInt(0.5, 34);
        drivingToLoadingDockTime.addProbInt(0.6, 35);
        drivingToLoadingDockTime.addProbInt(0.7, 35);
        drivingToLoadingDockTime.addProbInt(0.8, 37);
        drivingToLoadingDockTime.addProbInt(0.9, 38);
        drivingToLoadingDockTime.addProbInt(1.0, 39);

        SimulationObjects.getInstance().add(this);
    }


    @Override
    public String toString()
    {
        return "Spedition Site: " + name  + " :" + (truckCurrentlyIdle != null ? truckCurrentlyIdle : "---");
    }


    @Override
    public boolean simulate(long timeStep)
    {
        if (GrainShipping.grainToShip > 0)
        {
            Event event = eventQueue.next(timeStep, true, GrainShippingEvents.Idle, null, this.getClass(), null);

            if (event != null)
            {
                Truck t = (Truck) event.getObjectAttached();
                eventQueue.remove(event);
                idleTrucks.remove(t);
                t.getNewDriver();
                if (t.getCurrentDriver() != null)
                {
                    eventQueue.add(new Event(timeStep + 20, GrainShippingEvents.Loading, t, LoadingDock.class, null));
                }
                else
                {
                    eventQueue.add(new Event(timeStep + 20, GrainShippingEvents.Idle, t, SpeditionSite.class, null));
                }
                return true;
            }
        }
        return false;
    }

    public static void returnToSpedition(long timeStep, Truck truck, long drivingTime)
    {
        Employee currentDriver = truck.getCurrentDriver();
        currentDriver.setCurrentlyWorking(false);
        currentDriver.resetWorkingSessionTime();
        currentDriver.increaseRestingSessions();
        truck.setCurrentDriver(null);
        eventQueue.add(new Event(timeStep + drivingTime, GrainShippingEvents.Idle,
                truck, SpeditionSite.class, null));
    }
}

