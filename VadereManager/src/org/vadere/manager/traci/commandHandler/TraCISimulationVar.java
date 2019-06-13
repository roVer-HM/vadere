package org.vadere.manager.traci.commandHandler;

import org.vadere.manager.TraCIException;
import org.vadere.manager.traci.TraCIDataType;

public enum TraCISimulationVar {

	CURR_SIM_TIME(0x66, TraCIDataType.DOUBLE),
	NUM_LOADED_VEHICLES(0x71, TraCIDataType.INTEGER),
	LOADED_VEHICLES_IDS(0x72, TraCIDataType.STRING_LIST),
	NUM_DEPARTED_VEHICLES(0x73, TraCIDataType.INTEGER),
	DEPARTED_VEHICLES_IDS(0x74, TraCIDataType.STRING_LIST),
	NUM_VEHICLES_START_TELEPORT(0x75, TraCIDataType.INTEGER),
	VEHICLES_START_TELEPORT_IDS(0x76, TraCIDataType.STRING_LIST),
	NUM_VEHICLES_END_TELEPORT(0x77, TraCIDataType.INTEGER),
	VEHICLES_END_TELEPORT_IDS(0x78, TraCIDataType.STRING_LIST),
	VEHICLES_START_PARKING_IDS(0x6d, TraCIDataType.STRING_LIST),
	VEHICLES_STOP_PARKING_IDS(0x6f, TraCIDataType.STRING_LIST),
	//
	NETWORK_BOUNDING_BOX_2D(0x7c, TraCIDataType.POLYGON)

	;

	public int id;
	public TraCIDataType returnType;

	TraCISimulationVar(int id, TraCIDataType retVal) {
		this.id = id;
		this.returnType = retVal;
	}

	public static TraCISimulationVar fromId(int id){
		for(TraCISimulationVar var : values()){
			if (var.id == id)
				return var;
		}
		throw new TraCIException(String.format("No simulation variable found with id: %02X", id));
	}

	@Override
	public String toString() {
		return "TraCISimulationVar{" +
				"id=" + id +
				", returnType=" + returnType +
				'}';
	}
}