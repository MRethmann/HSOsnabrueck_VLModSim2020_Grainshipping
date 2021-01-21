package model_grainshipping;

import core.UniqueEventDescription;

public enum GrainShippingEvents implements UniqueEventDescription
{
	Loading("Loading Truck"),
	LoadingDone("Loading Truck done"),

	EntranceWeighing("Entrance Weighing of Truck"),
	EntranceWeighingDone("Entrance Weighing of Truck done"),

	Unloading("Unloading Truck"),
	UnloadingDone("Unloading Truck done"),

	ExitWeighing("Exit Weighing of Truck"),
	ExitWeighingDone("Exit Weighing of Truck done"),

	Repairing("Repairing Truck"),
	RepairingDone("Repairing Truck Done"),

	Idle("Truck is inactive"),
	IdlingDone ("Truck is becoming active again!"),

	//TODO: Änderung der Grainshippingevents
	Refueling("Refueling of Truck"),
	RefuelingDone("Refueling of Truck Done");


	String uniqueEventDescription = null;


	private GrainShippingEvents(String uniqueEventDescription)
	{
		this.uniqueEventDescription = uniqueEventDescription;
	}


	@Override
	public String get()
	{
		return uniqueEventDescription;
	}

}
