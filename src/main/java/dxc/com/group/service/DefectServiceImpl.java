package dxc.com.group.service;

import java.rmi.activation.ActivationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import dxc.com.group.model.Defect;

@Service
public class DefectServiceImpl implements DefectService {

	public Defect getDefectByNameProject(String nameProject) throws JSONException {
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
		// get each issue
		for (int i = 0; i < issues.length(); i++) {
			// get current status of each issue
			String projectname = issues.getJSONObject(i).getJSONObject("fields").getJSONObject("project")
					.getString("name");
			String statusname = issues.getJSONObject(i).getJSONObject("fields").getJSONObject("status")
					.getString("name");
			try {
				json.getJSONObject(projectname);
				int count[] = new int[3];
				if (statusname.equals("Opened")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalOpened") + 1;
					count[1] = json.getJSONObject(projectname).getInt("TotalResolved");
					count[2] = json.getJSONObject(projectname).getInt("TotalClosed");
				} else if (statusname.equals("Resolved")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalOpened");
					count[1] = json.getJSONObject(projectname).getInt("TotalResolved") + 1;
					count[2] = json.getJSONObject(projectname).getInt("TotalClosed");
				} else if (statusname.equals("Closed")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalOpened");
					count[1] = json.getJSONObject(projectname).getInt("TotalResolved");
					count[2] = json.getJSONObject(projectname).getInt("TotalClosed") + 1;
				}

				JSONObject childjson = new JSONObject();
				childjson.put("TotalOpened", count[0]);
				childjson.put("TotalResolved", count[1]);
				childjson.put("TotalClosed", count[2]);
				childjson.put("TotalCritical", json.getJSONObject(projectname).getInt("TotalCritical"));
				childjson.put("TotalMajor", json.getJSONObject(projectname).getInt("TotalMajor"));
				childjson.put("TotalMinor", json.getJSONObject(projectname).getInt("TotalMinor"));
				json.put(projectname, childjson);

			} catch (JSONException e) {
				int count[] = new int[3];
				if (statusname.equals("Opened")) {
					count[0]++;
				} else if (statusname.equals("Resolved")) {
					count[1]++;
				} else if (statusname.equals("Closed")) {
					count[2]++;
				}

				JSONObject childjson = new JSONObject();
				childjson.put("TotalOpened", count[0]);
				childjson.put("TotalResolved", count[1]);
				childjson.put("TotalClosed", count[2]);
				childjson.put("TotalCritical", 0);
				childjson.put("TotalMajor", 0);
				childjson.put("TotalMinor", 0);
				json.put(projectname, childjson);
			}
			// get current severity of each issue
			try {
				String severityname = issues.getJSONObject(i).getJSONObject("fields").getJSONObject("customfield_10200")
						.getString("value");
				int count[] = new int[3];
				if (severityname.equals("Critical")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalCritical") + 1;
					count[1] = json.getJSONObject(projectname).getInt("TotalMajor");
					count[2] = json.getJSONObject(projectname).getInt("TotalMinor");
				} else if (severityname.equals("Major")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalCritical");
					count[1] = json.getJSONObject(projectname).getInt("TotalMajor") + 1;
					count[2] = json.getJSONObject(projectname).getInt("TotalMinor");
				} else if (severityname.equals("Minor")) {
					count[0] = json.getJSONObject(projectname).getInt("TotalCritical");
					count[1] = json.getJSONObject(projectname).getInt("TotalMajor");
					count[2] = json.getJSONObject(projectname).getInt("TotalMinor") + 1;
				}

				JSONObject childjson = new JSONObject();
				childjson.put("TotalOpened", json.getJSONObject(projectname).getInt("TotalOpened"));
				childjson.put("TotalResolved", json.getJSONObject(projectname).getInt("TotalResolved"));
				childjson.put("TotalClosed", json.getJSONObject(projectname).getInt("TotalClosed"));
				childjson.put("TotalCritical", count[0]);
				childjson.put("TotalMajor", count[1]);
				childjson.put("TotalMinor", count[2]);
				json.put(projectname, childjson);
			} catch (Exception e) {
			}
		}

		Defect defect = new Defect();
		defect.setNameProject(nameProject);
		defect.setTotalClosed(json.getJSONObject(nameProject).getInt("TotalClosed"));
		defect.setTotalResolved(json.getJSONObject(nameProject).getInt("TotalResolved"));
		defect.setTotalMinor(json.getJSONObject(nameProject).getInt("TotalMinor"));
		defect.setTotalOpened(json.getJSONObject(nameProject).getInt("TotalOpened"));
		defect.setTotalMajor(json.getJSONObject(nameProject).getInt("TotalMajor"));
		defect.setTotalCritical(json.getJSONObject(nameProject).getInt("TotalCritical"));
		return defect;
	}
}