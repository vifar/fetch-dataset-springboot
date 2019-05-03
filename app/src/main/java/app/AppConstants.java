package app;

public interface AppConstants {
	
	public interface GET {
		public static final String DATASET_ID = "http://vautointerview.azurewebsites.net/api/datasetId";
		public static final String CHEAT = "http://vautointerview.azurewebsites.net/api/{datasetId}/cheat";
		public static final String DEALERS_BY_DEALER_ID = "http://vautointerview.azurewebsites.net/api/{datasetId}/dealers/{dealerid}";
		public static final String VEHICLES = "http://vautointerview.azurewebsites.net/api/{datasetId}/vehicles";
		public static final String VEHICLES_BY_VEHICLE_ID = "http://vautointerview.azurewebsites.net/api/{datasetId}/vehicles/{vehicleid}";
	}
	
	public interface POST {
		public static final String ANSWER = "http://vautointerview.azurewebsites.net/api/{datasetId}/answer";
	}

	public interface REQUEST_METHODS {
		public static final String GET = "GET";
		public static final String POST = "POST";
	}

	public interface REPLACEMENTS {
		public static final String DATASET_ID = "{datasetId}";
		public static final String VEHICLE_ID = "{vehicleid}";
		public static final String DEALER_ID = "{dealerid}";
	}

}