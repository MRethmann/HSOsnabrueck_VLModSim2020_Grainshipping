package tests;

import core.Event;
import core.EventQueue;
import model_grainshipping.GrainShippingEvents;
import model_grainshipping.Repairshop;
import model_grainshipping.Assignment;
import model_grainshipping.Truck;

public class TestEvent
{

	public static void main(String[] args)
	{
		Assignment a1 = new Assignment("A1");
		Truck t1 = new Truck("T1");
		Repairshop r1 = new Repairshop("Shop 1");
		System.out.println("Assignment dauert: " + a1.getRoutingTime() + " Einheiten & besitzt: " + a1.getLoadingCapacity());
		//t1.repair();
		System.out.println("Es War: " + t1.getIntTimetolive() + " & " + t1.getDurability());
		//t1.repair();
		System.out.println("Es ist: " + t1.getIntTimetolive() + " & " + t1.getDurability());

		EventQueue eq = EventQueue.getInstance();
		eq.add(new Event(200l, GrainShippingEvents.Repairing, t1, Repairshop.class, r1));

		r1.simulate(200l);
	}

}
