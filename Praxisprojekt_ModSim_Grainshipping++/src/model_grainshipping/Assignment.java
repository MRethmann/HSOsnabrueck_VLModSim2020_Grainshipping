package model_grainshipping;

import core.Randomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Assignment
{
	private String name = null;
	private Integer loadingCapacity = null;
	private Integer routingTime = null;
	private Double wear = null;
	private Double vRating = null;
	private Integer rating = null;
	private Double pricePerTon = null;

	private static Randomizer randomLoadingCapacity = null;
	private static Randomizer randomRoutingTime = null;
	private static Randomizer randomWear = null;
	private static Randomizer randomPricePerTon = null;

	public static ArrayList<Assignment> Assignments = new ArrayList<>(GrainShipping.getNumAssignments());

	public Assignment(String name)
	{
		super();
		this.name = name;

		randomLoadingCapacity = new Randomizer();
		randomLoadingCapacity.addProbInt(0.1, 10000);
		randomLoadingCapacity.addProbInt(0.2, 15000);
		randomLoadingCapacity.addProbInt(0.3, 20000);
		randomLoadingCapacity.addProbInt(0.4, 25000);
		randomLoadingCapacity.addProbInt(0.5, 30000);
		randomLoadingCapacity.addProbInt(0.6, 35000);
		randomLoadingCapacity.addProbInt(0.7, 40000);
		randomLoadingCapacity.addProbInt(0.8, 45000);
		randomLoadingCapacity.addProbInt(0.9, 50000);
		randomLoadingCapacity.addProbInt(1.0, 55000);
		this.loadingCapacity = randomLoadingCapacity.nextInt();

		randomRoutingTime = new Randomizer();
		randomRoutingTime.addProbInt(0.1, 21);
		randomRoutingTime.addProbInt(0.2, 23);
		randomRoutingTime.addProbInt(0.3, 27);
		randomRoutingTime.addProbInt(0.4, 29);
		randomRoutingTime.addProbInt(0.5, 34);
		randomRoutingTime.addProbInt(0.6, 120);
		randomRoutingTime.addProbInt(0.7, 125);
		randomRoutingTime.addProbInt(0.8, 130);
		randomRoutingTime.addProbInt(0.9, 132);
		randomRoutingTime.addProbInt(1.0, 135);
		this.routingTime = randomRoutingTime.nextInt();

		randomWear = new Randomizer();
		randomWear.addProbInt(0.5 ,12);
		randomWear.addProbInt(0.8 , 13);
		randomWear.addProbInt( 1.0 , 14);
		this.wear = randomWear.nextInt().doubleValue() / 10000;

		randomPricePerTon = new Randomizer();
		randomPricePerTon.addProbInt(0.1 , 91);
		randomPricePerTon.addProbInt(0.2,92);
		randomPricePerTon.addProbInt(0.3,93);
		randomPricePerTon.addProbInt(0.4,94);
		randomPricePerTon.addProbInt(0.5,95);
		randomPricePerTon.addProbInt(0.6,96);
		randomPricePerTon.addProbInt(0.7,97);
		randomPricePerTon.addProbInt(0.8,98);
		randomPricePerTon.addProbInt(0.9,99);
		randomPricePerTon.addProbInt(1.0,100);
		this.pricePerTon = randomPricePerTon.nextInt().doubleValue() / 100;

		vRating = ((this.loadingCapacity * this.pricePerTon) / this.wear);
		this.rating= (int) Math.round(vRating);


		Assignments.add(this);

		Collections.sort(Assignments, new Comparator<Assignment>()
		{
			public int compare(Assignment a1, Assignment a2)
			{
				return Integer.valueOf(a2.rating).compareTo(a1.rating);
			}
		});
	}

	public Integer getRoutingTime()
	{
		return routingTime;
	}
	public void lowerLoadingCapacity(Integer Capacity)
	{
		loadingCapacity=loadingCapacity - Capacity;
	}
	public Double getWear()
	{
		return wear;
	}
	public Integer getLoadingCapacity()
	{
		return loadingCapacity;
	}
	public Double getPricePerTon()
	{
		return pricePerTon;
	}
	public String toString()
	{
		return name;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
}
