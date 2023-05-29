package com.starworks.kronos;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.starworks.kronos.exception.KronosException;
import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.resources.InternalResourceLoader;
import com.starworks.kronos.resources.ResourceManager;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public final class Kronos {

	private static String s_license = null;
	
	private Kronos() {
	}

	public static void load(String path) throws KronosException {
		try {
			FileHandle handle = FileSystem.INSTANCE.getFileHandle(path, true, true);
			if (handle.wasGenerated()) {
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<kronos version=\"" + Version.getVersion() + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
						+ "\t<license>0</license>\r\n"
						+ "</kronos>";
				handle.write(xml);
			}
			VTDGen vg = new VTDGen();
			if (vg.parseFile(path, true)) {
				VTDNav vn = vg.getNav();
				AutoPilot ap = new AutoPilot(vn);
				
				int version = -1;
				// version
				ap.selectXPath("//kronos/@version");
				if (ap.evalXPath() != -1) {
					version = Integer.parseInt(vn.toString(vn.getAttrVal("version")));
				}
				if (Version.getVersion() != version) {
					throw new IllegalStateException("Version of kronos file does not match version of engine!");
				}
				
				// license
				ap.selectXPath("//kronos/license");
				if (ap.evalXPath() != -1) {
					s_license = vn.toString(vn.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LicenseValidator.INSTANCE.validate(s_license);
	}

	private enum LicenseValidator {

		INSTANCE;

		private static final String VALIDATION_SERVER_URL = "https://kronosengine.com/license/validate.php";
		private static final String SETUP_COMPLETION_SERVER_URL = "https://kronosengine.com/license/isSetupComplete.php";
		private static final String COMPLETE_SETUP_SERVER_URL = "https://kronosengine.com/license/completeSetup.php";

		private final HttpClient m_client;

		private LicenseValidator() {
			this.m_client = HttpClient.newHttpClient();
		}

		private boolean validate(final String licenseKey) throws KronosException {
			boolean valid = isLicenseValid(licenseKey);
			if (!(valid)) {
				throw new KronosException("Invalid Kronos license key!");
			}
			if (!isSetupComplete(licenseKey)) {
				ResourceManager.INSTANCE.load(new InternalResourceLoader());
				completeSetup(licenseKey);
			}
			return valid;
		}

		private boolean isLicenseValid(final String licenseKey) {
			JsonObject jsonRequest = new JsonObject();
			jsonRequest.addProperty("licenseKey", licenseKey);
			String jsonString = jsonRequest.toString();

			HttpRequest request = null;
			try {
				request = HttpRequest.newBuilder().uri(new URI(VALIDATION_SERVER_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonString, StandardCharsets.UTF_8)).build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			HttpResponse<String> response = null;
			try {
				response = m_client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

			Gson gson = new Gson();
			JsonObject jsonReponse = gson.fromJson(response.body(), JsonObject.class);
			return jsonReponse.get("valid").getAsBoolean();
		}

		private boolean isSetupComplete(final String licenseKey) {
			JsonObject jsonRequest = new JsonObject();
			jsonRequest.addProperty("licenseKey", licenseKey);
			String jsonString = jsonRequest.toString();

			HttpRequest request = null;
			try {
				request = HttpRequest.newBuilder()
						.uri(new URI(SETUP_COMPLETION_SERVER_URL))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(jsonString, StandardCharsets.UTF_8))
						.build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			HttpResponse<String> response = null;
			try {
				response = m_client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

			Gson gson = new Gson();
			JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

			return jsonResponse.get("setup").getAsInt() == 1;
		}

		private void completeSetup(final String licenseKey) {
			JsonObject jsonRequest = new JsonObject();
			jsonRequest.addProperty("licenseKey", licenseKey);
			String jsonString = jsonRequest.toString();

			HttpRequest request = null;
			try {
				request = HttpRequest.newBuilder()
						.uri(new URI(COMPLETE_SETUP_SERVER_URL))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(jsonString, StandardCharsets.UTF_8))
						.build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			try {
				m_client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
