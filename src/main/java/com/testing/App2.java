package com.testing;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class App2 {


	public static void trustAllCertificates(String trustStorePath, String trustStorePassword) throws Exception {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		TrustManager[] trustAllCerts = new TrustManager[] {new TrustAllCertificates()};

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCerts, null);
		SSLContext.setDefault(sslContext);
	}

	private static class TrustAllCertificates implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	public static void main(String[] args) {
		try {
			String req =
					"<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><soapenv:Envelopexmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header><ns5:Securityxmlns:mustUnderstand=\"http://schemas.xmlsoap.org/soap/envelope/\"xmlns:ns5=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"mustUnderstand:mustUnderstand=\"0\"><ns5:UsernameToken><ns5:Username>w04281971</ns5:Username><ns5:PasswordType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">uZymC85r7d</ns5:Password></ns5:UsernameToken></ns5:Security></soapenv:Header><soapenv:Body><ns1:IATA_AirShoppingRQxmlns:ns1=\"http://www.iata.org/IATA/2015/00/2018.2/IATA_AirShoppingRQ\"><ns1:Party><ns1:Participant><ns1:Aggregator><ns1:AggregatorID>Tripjack</ns1:AggregatorID><ns1:Name>Tripjack</ns1:Name></ns1:Aggregator></ns1:Participant><ns1:Recipient><ns1:ORA><ns1:AirlineDesigCode>AF</ns1:AirlineDesigCode></ns1:ORA></ns1:Recipient><ns1:Sender><ns1:TravelAgency><ns1:AgencyID>w04281971</ns1:AgencyID><ns1:IATANumber>12345675</ns1:IATANumber><ns1:Name>Tripjack</ns1:Name><ns1:PseudoCityID>PAR</ns1:PseudoCityID></ns1:TravelAgency></ns1:Sender></ns1:Party><ns1:PayloadAttributes><ns1:CorrelationID>Passport-2</ns1:CorrelationID><ns1:VersionNumber>18.2</ns1:VersionNumber></ns1:PayloadAttributes><ns1:Request><ns1:FlightCriteria><ns1:OriginDestCriteria><ns1:DestArrivalCriteria><ns1:IATALocationCode>CDG</ns1:IATALocationCode></ns1:DestArrivalCriteria><ns1:OriginDepCriteria><ns1:Date>2024-04-24+05:30</ns1:Date><ns1:IATALocationCode>NCE</ns1:IATALocationCode></ns1:OriginDepCriteria><ns1:PreferredCabinType><ns1:CabinTypeName>ECONOMY</ns1:CabinTypeName></ns1:PreferredCabinType><ns1:PreferredCabinType><ns1:CabinTypeName>PREMIUM_ECONOMY</ns1:CabinTypeName></ns1:PreferredCabinType><ns1:PreferredCabinType><ns1:CabinTypeName>BUSINESS</ns1:CabinTypeName></ns1:PreferredCabinType><ns1:PreferredCabinType><ns1:CabinTypeName>PREMIUM_BUSINESS</ns1:CabinTypeName></ns1:PreferredCabinType><ns1:PreferredCabinType><ns1:CabinTypeName>FIRST</ns1:CabinTypeName></ns1:PreferredCabinType><ns1:PreferredCabinType><ns1:CabinTypeName>PREMIUMFIRST</ns1:CabinTypeName></ns1:PreferredCabinType></ns1:OriginDestCriteria></ns1:FlightCriteria><ns1:Paxs><ns1:Pax><ns1:PaxID>PAX</ns1:PaxID><ns1:PTC>ADT</ns1:PTC></ns1:Pax></ns1:Paxs></ns1:Request></ns1:IATA_AirShoppingRQ></soapenv:Body></soapenv:Envelope>";

			String trustStorePath = "app.keystore";
			String trustStorePassword = "changeit";
			trustAllCertificates(trustStorePath, trustStorePassword);


			String url = "https://wscert-rct.airfrance.fr/passenger/distribmgmt/001448v02";
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(
					new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.10.16.165", 3128)));
			connection.setRequestMethod("POST");
			connection.setRequestProperty("SOAPAction",
					"http://www.af-klm.com/services/passenger/ProvideAirShopping/airShopping");

			connection.setDoOutput(true);
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = req.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			}

			connection.disconnect();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}


}
