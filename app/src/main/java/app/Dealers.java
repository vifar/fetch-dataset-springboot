package app;

import java.util.ArrayList;
import java.util.List;

public class Dealers {

	private int dealerId;
	private String name;
	private List<Vehicles> vehicles = new ArrayList<Vehicles>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Vehicles> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicles> vehicles) {
		this.vehicles = vehicles;
	}
	
	public int getDealerId() {
		return dealerId;
	}
	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}
	

	@Override
	public String toString() {
		return "{" +
			" dealerId='" + getDealerId() + "'" +
			", name='" + getName() + "'" +
			", vehicles='" + getVehicles() + "'" +
			"}";
	}

}
