package app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Vehicles {

	private int vehicleId = 0;
	private int year = 0;
	private String make = "";
	private String model = "";
	@JsonIgnoreProperties
	private int dealerId = 0;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getDealerId() {
		return this.dealerId;
	}

	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}

	@Override
	public String toString() {
		return "{" +
			" vehicleId='" + getVehicleId() + "'" +
			", year='" + getYear() + "'" +
			", make='" + getMake() + "'" +
			", model='" + getModel() + "'" +
			"}";
	}

}
