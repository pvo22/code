package dxc.com.group.service;

import java.rmi.activation.ActivationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

@Service
public class DefectDateServiceImpl implements DefectDateService {

	public String getDefectDateByNameProject(String nameProject) {
		// login to get json data from jira
		String auth = new String(Base64.encode("lmqnam:dxcvietnam"));
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:8080/rest/api/latest/search?expand=changelog");
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			try {
				throw new Exception("Invalid Username or Password");
			} catch (ActivationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String responses = response.getEntity(String.class);

		JSONObject object = new JSONObject(responses);
		JSONArray issues = (JSONArray) object.get("issues");
		JSONObject json = new JSONObject();
		LocalDate currentdate = LocalDate.now();
		long currenttime = System.currentTimeMillis();
		// get each issue
		for (int i = 0; i < issues.length(); i++) {
			String projectname = issues.getJSONObject(i).getJSONObject("fields").getJSONObject("project")
					.getString("name");
			try {
				json.getJSONArray(projectname);
			} catch (JSONException e) {
				JSONArray date = new JSONArray();
				for (int j = 0; j < 28; j++) {
					JSONObject dateobject = new JSONObject();
					dateobject.put("Date", currentdate.minusDays(j));
					dateobject.put("TotalOpenedByDate", 0);
					dateobject.put("TotalResolvedByDate", 0);
					dateobject.put("TotalClosedByDate", 0);
					date.put(dateobject);
				}
				json.put(projectname, date);
			}
			// get history of change status of each issue
			JSONArray histories = (JSONArray) issues.getJSONObject(i).getJSONObject("changelog").get("histories");
			String datecreated = issues.getJSONObject(i).getJSONObject("fields").getString("created").substring(0, 10);
			String[] temporarydate = new String[histories.length() + 2];

			if (histories.length() != 0) {
				String[] temporarystatus = new String[histories.length() + 2];
				int n = 1;

				for (int j = 0; j < histories.length(); j++) {
					for (int k = 0; k < histories.getJSONObject(j).getJSONArray("items").length(); k++) {
						if (histories.getJSONObject(j).getJSONArray("items").getJSONObject(k).getString("field")
								.equals("status")) {
							temporarydate[n] = histories.getJSONObject(j).getString("created").substring(0, 10);
							temporarystatus[n] = histories.getJSONObject(j).getJSONArray("items").getJSONObject(k)
									.getString("toString");
							n++;
						}
					}
				}

				if (temporarydate[1] != null) {
					temporarydate[0] = datecreated;
					temporarystatus[0] = "Opened";
					int[] temporarydate2 = new int[histories.length() + 1];
					String[] temporarystatus2 = new String[histories.length() + 1];
					n = 0;

					for (int j = 0; j < histories.length() + 1; j++) {
						if ((temporarydate[j + 1] != null) && (temporarydate[j].equals(temporarydate[j + 1]))) {
							continue;
						}

						if (temporarydate[j] == null) {
							break;
						}

						Date tempdate = null;
						try {
							tempdate = new SimpleDateFormat("yyyy-MM-dd").parse(temporarydate[j]);
						} catch (ParseException e) {
						}

						long dategap = (currenttime - tempdate.getTime()) / (24 * 60 * 60 * 1000);
						temporarydate2[n] = (int) dategap;
						temporarystatus2[n] = temporarystatus[j];
						n++;
					}
					JSONArray date = new JSONArray();
					int k = 0;
					int l = 0;
					for (int j = n - 1; j >= 0; j--) {
						for (k = l; k < 28; k++) {
							JSONObject dateobject = new JSONObject();
							dateobject.put("Date", currentdate.minusDays(k));
							if (k <= temporarydate2[j]) {
								if (temporarystatus2[j].equals("Opened")) {
									dateobject.put("TotalOpenedByDate",
											json.getJSONArray(projectname).getJSONObject(k).getInt("TotalOpenedByDate")
													+ 1);
									dateobject.put("TotalResolvedByDate", json.getJSONArray(projectname)
											.getJSONObject(k).getInt("TotalResolvedByDate"));
									dateobject.put("TotalClosedByDate", json.getJSONArray(projectname).getJSONObject(k)
											.getInt("TotalClosedByDate"));
								} else if (temporarystatus2[j].equals("Resolved")) {
									dateobject.put("TotalOpenedByDate", json.getJSONArray(projectname).getJSONObject(k)
											.getInt("TotalOpenedByDate"));
									dateobject.put("TotalResolvedByDate", json.getJSONArray(projectname)
											.getJSONObject(k).getInt("TotalResolvedByDate") + 1);
									dateobject.put("TotalClosedByDate", json.getJSONArray(projectname).getJSONObject(k)
											.getInt("TotalClosedByDate"));
								} else if (temporarystatus2[j].equals("Closed")) {
									dateobject.put("TotalOpenedByDate", json.getJSONArray(projectname).getJSONObject(k)
											.getInt("TotalOpenedByDate"));
									dateobject.put("TotalResolvedByDate", json.getJSONArray(projectname)
											.getJSONObject(k).getInt("TotalResolvedByDate"));
									dateobject.put("TotalClosedByDate",
											json.getJSONArray(projectname).getJSONObject(k).getInt("TotalClosedByDate")
													+ 1);
								}
							} else if ((k > temporarydate2[j]) && (j != 0)) {
								l = k;
								break;
							} else if (j == 0) {
								dateobject.put("TotalOpenedByDate",
										json.getJSONArray(projectname).getJSONObject(k).getInt("TotalOpenedByDate"));
								dateobject.put("TotalResolvedByDate",
										json.getJSONArray(projectname).getJSONObject(k).getInt("TotalResolvedByDate"));
								dateobject.put("TotalClosedByDate",
										json.getJSONArray(projectname).getJSONObject(k).getInt("TotalClosedByDate"));
							}
							date.put(dateobject);
						}
					}
					json.put(projectname, date);
				}
			}
			if (histories.length() == 0 || temporarydate[1] == null) {
				Date tempdate = null;
				try {
					tempdate = new SimpleDateFormat("yyyy-MM-dd").parse(datecreated);
				} catch (ParseException e) {
				}

				long dategap = (currenttime - tempdate.getTime()) / (24 * 60 * 60 * 1000);
				JSONArray date = new JSONArray();

				for (int j = 0; j < 28; j++) {
					JSONObject dateobject = new JSONObject();
					if (j <= dategap) {
						dateobject.put("Date", currentdate.minusDays(j));
						dateobject.put("TotalOpenedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalOpenedByDate") + 1);
						dateobject.put("TotalResolvedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalResolvedByDate"));
						dateobject.put("TotalClosedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalClosedByDate"));
					} else {
						dateobject.put("Date", currentdate.minusDays(j));
						dateobject.put("TotalOpenedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalOpenedByDate"));
						dateobject.put("TotalResolvedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalResolvedByDate"));
						dateobject.put("TotalClosedByDate",
								json.getJSONArray(projectname).getJSONObject(j).getInt("TotalClosedByDate"));
					}
					date.put(dateobject);
				}
				json.put(projectname, date);
			}
		}
		return json.getJSONArray(nameProject).toString();
	}
}