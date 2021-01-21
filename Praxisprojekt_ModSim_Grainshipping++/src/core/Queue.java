package core;

import model_grainshipping.Truck;
import java.util.LinkedList;

public class Queue extends LinkedList<SimulationObject>
{
    public String name = null;
    public Queue(String name)
    {
        this.name = name;
    }

    public boolean add(SimulationObject simulationObject)
    {
        System.out.println("Truck " + simulationObject + " wurde zur " + name + " hinzugefügt!");
        return super.add(simulationObject);
    }

    @Override
    public SimulationObject pollFirst()
    {
        if (super.isEmpty() == false)
        {
            System.out.println("Truck " + super.peekFirst() + " wurde aus der " + name + " genommen und ist nun an der Reihe!");
            return super.pollFirst();
        }
        else
        {
            return null;
        }
    }

    public boolean remove(SimulationObject simulationObject)
    {
        if (super.isEmpty() == false)
        {
            System.out.println("Truck " + simulationObject + " wurde aus der " + name + " gelöscht.");
            return super.remove(simulationObject);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
