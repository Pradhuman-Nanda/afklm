package com.testing;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SSLUtils {

	@Bean
	public RestTemplate restTemplateWithSSL() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		char[] password = "***exL**a9YyHl******".toCharArray(); // Keystore password
		try (InputStream keystoreInputStream = new FileInputStream("/NDC_TRIPJACK_w04281971_CAE.pfx")) {
			keyStore.load(keystoreInputStream, password);
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, password);
		// Create a trust manager that trusts all certificates
		TrustManager[] trustAllCertificates = new TrustManager[] {new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(X509Certificate[] certs, String authType) {}
			public void checkServerTrusted(X509Certificate[] certs, String authType) {}
		}};
		
		HttpHost proxy = new HttpHost("10.10.16.165", 3128);
		// Create SSL context with client certificate and trusting all certificates
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCertificates, new java.security.SecureRandom());
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
				.setProxy(proxy).build();
		ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}


	public void callSecureApiEndpoint() throws RestClientException, Exception {
		try {
			String apiUrl = "https://wscert-rct.airfrance.fr/passenger/distribmgmt/001448v02";																								
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" > <soapenv:Header> <wsse:Security> <wsse:UsernameToken> <wsse:Username>w04281971</wsse:Username> <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">uZymC85r7d</wsse:Password> </wsse:UsernameToken> </wsse:Security> <trackingMessageHeader xmlns=\"http://www.af-klm.com/soa/xsd/MessageHeader-V1_0\"> <consumerRef> <userID>w04281971</userID> <partyID>partyID</partyID> <consumerID>w04281971</consumerID> <consumerLocation>External</consumerLocation> <consumerType>A</consumerType> <consumerTime>2018-02-13T02:03:19Z</consumerTime> </consumerRef> </trackingMessageHeader> <MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">307d5511-ee6c-487b-9f75-25ea761fa31c</MessageID> <RelatesTo RelationshipType=\"http://www.af-klm.com/soa/tracking/InitiatedBy\" xmlns=\"http://www.w3.org/2005/08/addressing\">e8fdd9f3-54da-4da1-9b47-5863b09080ca</RelatesTo> <RelatesTo RelationshipType=\"http://www.af-klm.com/soa/tracking/PrecededBy\" xmlns=\"http://www.w3.org/2005/08/addressing\">89562767-5cb7-4e90-a159-1070b25992fc</RelatesTo> </soapenv:Header> <soapenv:Body> <IATA_AirShoppingRQ xmlns=\"http://www.iata.org/IATA/2015/00/2018.2/IATA_AirShoppingRQ\"> <Party> <Participant> <Aggregator> <AggregatorID>Tripjack</AggregatorID> <Name>Tripjack</Name> </Aggregator> </Participant> <Recipient> <ORA> <AirlineDesigCode>AF</AirlineDesigCode> </ORA> </Recipient> <Sender> <TravelAgency> <AgencyID>w04281971</AgencyID> <IATANumber>12345675</IATANumber> <Name>AGENCE TEST </Name> <PseudoCityID>PAR</PseudoCityID> </TravelAgency> </Sender> </Party> <PayloadAttributes> <CorrelationID>Passport-2</CorrelationID> <VersionNumber>18.2</VersionNumber> </PayloadAttributes> <Request> <FlightCriteria> <OriginDestCriteria> <DestArrivalCriteria> <IATALocationCode>NCE</IATALocationCode> </DestArrivalCriteria> <OriginDepCriteria> <Date>2024-05-25</Date> <IATALocationCode>CDG</IATALocationCode> </OriginDepCriteria> <PreferredCabinType> <CabinTypeName>ECONOMY</CabinTypeName> </PreferredCabinType> </OriginDestCriteria> </FlightCriteria> <Paxs> <Pax> <PaxID>PAX1</PaxID> <PTC>ADT</PTC> </Pax> </Paxs> </Request> </IATA_AirShoppingRQ> </soapenv:Body> </soapenv:Envelope>";	
			MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
			header.add("SOAPAction", "http://www.af-klm.com/services/passenger/ProvideAirShopping/airShopping");
			header.add("Content-Type", "text/xml");
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, header);
			ResponseEntity<String> response = restTemplateWithSSL().exchange(apiUrl,HttpMethod.POST, requestEntity, String.class);
			System.out.println("Secure API Response: " + response.getBody());
		} catch (HttpClientErrorException e) {
			System.err.println("HTTP Client Error: " + e.getMessage());
			System.err.println("Response body: " + e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			System.err.println("HTTP Server Error: " + e.getMessage());
			System.err.println("Response body: " + e.getResponseBodyAsString());
		} catch (ResourceAccessException e) {
			System.err.println("Resource Access Error: " + e.getMessage());
		} catch (RestClientException e) {
			System.err.println("Rest Client Error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
		}
	}
}
