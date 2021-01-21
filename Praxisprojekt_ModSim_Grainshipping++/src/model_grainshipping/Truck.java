package model_grainshipping;

import core.*;

import java.util.Collections;
import java.util.Comparator;

public class Truck extends SimulationObject
{
	private String name = null;
	private String truckType = null;
	private double durability = 100;
	private Integer IntTimeToLive = null;
	private Integer gross_weight = null;
	private Integer net_weight = null; //ToDo: Überarbeiten da Exception ausgelöst werden kann
	private Integer tare_weight = null;
	private Double remainingTank = (double) 1000;
	private UniqueEventDescription previousEvent = null;
	private Employee currentDriver = null;
	private long runDuration = 0l;
	private long runStart = 0l;
	private long idleStart = 0l;
	private Double repairCosts = 0.0;
	private Double fuelCosts = 0.0;
	private Double totalTruckCosts = 0.0;
	private Double totalDrivingTime = 0.0;
	private long totalRepairTime = 0;


	private static EventQueue eventQueue = EventQueue.getInstance();

	private static Randomizer timeToLive = null; 	//= randomizer? Wie oft kann ein truck repariert werden
	private static Randomizer wearRate = null;
	private static Randomizer timeUntilRepairStart = null;
	private static Randomizer drivingToGasStation = null;
	private static Randomizer durabilityStart	= null;

	public Truck(String name)
	{
		super();
		this.name = name;
		this.net_weight = 14;
		this.gross_weight = this.net_weight;
		this.getNewDriver();

		durabilityStart = new Randomizer();
		durabilityStart.addProbInt(0.2, 20);
		durabilityStart.addProbInt(0.4, 40);
		durabilityStart.addProbInt(0.6, 60);
		durabilityStart.addProbInt(0.8, 80);
		durabilityStart.addProbInt(1.0, 100);
		this.durability = durabilityStart.nextInt();

		timeToLive = new Randomizer();
		timeToLive.addProbInt(0.2, 5);
		timeToLive.addProbInt(0.4, 6);
		timeToLive.addProbInt(0.6, 7);
		timeToLive.addProbInt(0.8, 8);
		timeToLive.addProbInt(1.0, 9);
		this.IntTimeToLive = timeToLive.nextInt();

		SimulationObjects.getInstance().add(this);
	}

	public Employee getCurrentDriver()
	{
		if (currentDriver != null)
		{
			return currentDriver;
		}
		return null;
	}

	public void getNewDriver()
	{
		for (int i = 0; i < Employee.Employees.size(); i++)
		{
			Collections.sort(Employee.Employees, new Comparator<Employee>()
					{
						@Override
						public int compare(Employee e1, Employee e2)
						{
							return Long.valueOf(e2.getRestingSessionTime()).compareTo(e1.getRestingSessionTime());
						}
					}
			);
		}

		Employee employee = null;
		int i = 0;

		do
		{
			if (Employee.Employees.get(i).getCurrentlyWorking() == false
					&& Employee.Employees.get(i).getRestingSessionTime() >= Employee.NEEDEDREST)
			{
				employee = Employee.Employees.get(i);
				break;
			}
			i++;
		}
		while (i > Employee.Employees.size());

		if (employee != null)
		{
			{
				this.currentDriver = employee;
				employee.setCurrentlyWorking(true);
				employee.resetWorkingSessionTime();
				employee.resetRestingSessionTime();
				employee.increaseWorkingSessions();
				System.out.println(employee.getMitarbeiterID() + " fährt jetzt Truck " + this.name);
			}
		}
		else
		{
			this.currentDriver = null;
		}
	}

	public void setCurrentDriver(Employee e)
	{
		currentDriver = e;
	}

	public void wearDurability(Double number, Double drivingTime)
	{
		if (number == null)
		{
			wearRate = new Randomizer();
			wearRate.addProbInt(0.25, 8);
			wearRate.addProbInt(0.50, 9);
			wearRate.addProbInt(0.75, 10);
			wearRate.addProbInt(1.0, 11);
			this.durability = this.durability - (drivingTime * wearRate.nextInt() / 10000);
		}
		else
		{
			this.durability = this.durability - drivingTime * number;
		}
		this.setTotalDrivingTime(drivingTime);
	}

	public void repairTruck(long timeStep, UniqueEventDescription previousEvent, Truck truck)
	{
		timeUntilRepairStart = new Randomizer();
		timeUntilRepairStart.addProbInt(0.45, 30);
		timeUntilRepairStart.addProbInt(0.9, 60);
		timeUntilRepairStart.addProbInt(1, 120);

		eventQueue.add(new Event(timeStep + timeUntilRepairStart.nextInt(), GrainShippingEvents.Repairing, truck, Repairshop.class, null));
		this.previousEvent = previousEvent;
	}

	public void consumeRemainingTank(Double drivingTime)
	{
		this.remainingTank = this.remainingTank - (drivingTime * 0.3); //Wert errechnet anhand der Durchschnittsgeschwindigkeit eines LKWs
	}

	public void refuelTruck(long timeStep, UniqueEventDescription previousEvent, Truck truck) //Variable Truck kann auch "this" sein
	{
		drivingToGasStation = new Randomizer();
		drivingToGasStation.addProbInt(0.33, 4);
		drivingToGasStation.addProbInt(0.66, 5);
		drivingToGasStation.addProbInt(1, 6);

		truck.consumeRemainingTank((double) drivingToGasStation.nextInt());
		eventQueue.add(new Event(timeStep + drivingToGasStation.nextInt(), GrainShippingEvents.Refueling, truck, GasStation.class, null));
		this.previousEvent = previousEvent;
	}

	public void load(int weight)
	{
		gross_weight = net_weight + weight;
		tare_weight = gross_weight - net_weight;
	}

	public void unload()
	{
		tare_weight = null;
		gross_weight = net_weight;
	}

	public void runStart(long timeStep)
	{
		runStart = timeStep;
	}

	public void runEnd(long timeStep)
	{
		runDuration = timeStep - runStart;
		GrainShipping.totalDurationOfRuns += runDuration;
		GrainShipping.meanDurationPerRun = (GrainShipping.totalDurationOfRuns / GrainShipping.successfullLoadings);
	}

	public void increaseRepairCosts(Double currentCosts)
	{
		this.repairCosts += currentCosts;
	}

	public void increaseRefuelingCosts(Double currentCosts)
	{
		this.fuelCosts += currentCosts;
	}

	public Double getRefuelingCosts()
	{
		return this.fuelCosts;
	}

	public Double getRepairCosts()
	{
		return this.repairCosts;
	}

	public Integer getIntTimetolive()
	{
		return IntTimeToLive;
	}

	public Double getRemainingTank(){return this.remainingTank;}

	public void setRemainingTank(Double remainingTank)
	{
		this.remainingTank = remainingTank;
	}

	public Double getDurability()
	{
		return durability;
	}

	public Integer getTare_weight()
	{
		return tare_weight;
	}

	public UniqueEventDescription getPreviousEvent()
	{
		return previousEvent;
	}

	public void setDurability(Double durability)
	{
		this.durability = durability;
	}

	public void decreaseTimeToLive()
	{
		IntTimeToLive--;
	}

	public void resetTimeToLive()
	{
		IntTimeToLive = timeToLive.nextInt();
	}

	public Double getTotalDrivingTime()
	{
		return totalDrivingTime;
	}

	public void setTotalDrivingTime(Double totalDrivingTime)
	{
		this.totalDrivingTime += totalDrivingTime;
	}

	public long getTotalRepairTime()
	{
		return totalRepairTime;
	}

	public void increaseTotalRepairtime(long totalRepairTime)
	{
		this.totalRepairTime += totalRepairTime;
	}

	@Override
	public String toString()
	{
		return name + (tare_weight != null ? " (Ladung: " + tare_weight + "t" + ")" : "");
	}

	@Override
	public boolean simulate(long timeStep)
	{
		// intentionally doing nothing
		return false;
	}

}
