package app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class DealersAndVehicles {

	public static void main(String[] args) throws IOException, JSONException, InterruptedException {

		String response1 = "";
		String dataSetId;

		// 1. Connect to GET /api/dataSetId to retrieve datasetId
		// 2. Object retrieved is a json object so convert and use key to retrieve datsetId
		response1 = retieve(AppConstants.GET.DATASET_ID, AppConstants.REQUEST_METHODS.GET, "");
		dataSetId = new JSONObject(response1).get("datasetId").toString();
		System.out.println("Dataset ID: " + dataSetId);

		// 1. Connect to GET /api/{datasetId}/vehicles to retrieve datasetId, add dataSetId
		// 2. Map String of array of vehicle ids to a array using Object Mapper
		response1 = retieve(AppConstants.GET.VEHICLES.replace(AppConstants.REPLACEMENTS.DATASET_ID, dataSetId), AppConstants.REQUEST_METHODS.GET, "");
		response1 = new JSONObject(response1).get("vehicleIds").toString();
		int[] vehicles = new ObjectMapper().readValue(response1, int[].class);
		System.out.println("Retrieved " + vehicles.length + " vehicle IDs...");

		// Create synchronized lists to effectively add too list while executing tasks 
		List<Vehicles> listOfVehicles = Collections.synchronizedList(new ArrayList<>());
		List<Dealers> listOfDealers = Collections.synchronizedList(new ArrayList<>());

		//Define task executor
		ExecutorService executor = Executors.newFixedThreadPool(vehicles.length);

		//For each vehicleId perform the following operations
		for(int vehicleId : vehicles) {
			System.out.println("Getting vehicle info for Vehicle ID: " + vehicleId + ".");
			executor.submit(() -> {
				try {
					// Connect to GET /api/{datasetId}/vehicles/{vehicleid}, add to synchronized list -- Map String object to Vehicle class
					String response2 = retieve(AppConstants.GET.VEHICLES_BY_VEHICLE_ID.replace(AppConstants.REPLACEMENTS.DATASET_ID, dataSetId)
						.replace(AppConstants.REPLACEMENTS.VEHICLE_ID, String.valueOf(vehicleId)), AppConstants.REQUEST_METHODS.GET, "");
					Vehicles vehicle =  new ObjectMapper().readValue(response2, Vehicles.class);
					listOfVehicles.add(vehicle);
				} catch(final Exception e) {
					e.printStackTrace();
				}
			});
		}

		// 1. Allow no more tasks to be added or executed
		// 2. Await the termination of all tasks
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		for(Vehicles v : listOfVehicles) {
			// 1. If dealer does not exist already in the dealer synchronizedList fetch it
			// 2. Add the vehicle to the dealer and add the dealer to the synchronized list
			// 3. If dealer does exist add all the vehicles with that particular dealer id
			if(!listOfDealers.stream().filter( d -> d.getDealerId() == v.getDealerId()).findFirst().isPresent()) {
				// GET /api/{datasetId}/dealers/{dealerid}, add to synchronized list -- Map String object to Dealer class
				try {
					response1 = retieve(AppConstants.GET.DEALERS_BY_DEALER_ID.replace(AppConstants.REPLACEMENTS.DATASET_ID, dataSetId)
					.replace(AppConstants.REPLACEMENTS.DEALER_ID, String.valueOf(v.getDealerId())), AppConstants.REQUEST_METHODS.GET, "");
					Dealers d = new ObjectMapper().readValue(response1, Dealers.class);
					d.getVehicles().add(v);
					listOfDealers.add(d);
					System.out.println("Adding vehicle to Dealer ID: " + d.getDealerId());
					System.out.println("Retrieved Dealer info for Dealer ID: " + v.getDealerId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//Find the dealer which whome the vehicle belongs to and add it to the vehicleList
				for(Dealers d : listOfDealers) {
					if(d.getDealerId() == v.getDealerId()) {
						d.getVehicles().add(v);
						System.out.println("Adding vehicle to Dealer ID: " + d.getDealerId());
						break;
					} 
				}
			}
		}

		// GET /api/{datasetId}/dealers/{dealerid}, add to synchronized list -- Map String object to Dealer class
		try {
			System.out.println("Submitting answer...");
			//Add data to map so that it can be successfully consumed
			Map<String, List<Dealers>> obj = new HashMap<String, List<Dealers>>();
			obj.put("dealers", listOfDealers);
			String body = new ObjectMapper().writeValueAsString(obj);
			response1 = retieve(AppConstants.POST.ANSWER.replace(AppConstants.REPLACEMENTS.DATASET_ID, dataSetId), AppConstants.REQUEST_METHODS.POST, body);
			JSONObject postResponse = new JSONObject(response1);

			//Repsonse
			System.out.println("\nSuccess: " + postResponse.get("success").toString());
			System.out.println("Message: " + postResponse.get("message").toString());
			System.out.println("Total Time in Seconds: " + Integer.parseInt(postResponse.get("totalMilliseconds").toString())/1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to call api to retieve dataset information
	public static String retieve(String uri, String requestMethod, String urlParameters) throws IOException {

		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(requestMethod);
		conn.setRequestProperty("Content-Type", "application/json");
		if(requestMethod.equals(AppConstants.REQUEST_METHODS.POST)) {
			//Send Post Request
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		return br.readLine();

	}

}
