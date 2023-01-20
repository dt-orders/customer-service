package com.ewolff.microservice.customer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ComponentScan
@EnableAutoConfiguration
@Component
public class CustomerApp {

	private final CustomerRepository customerRepository;
	private static final Logger logger = LoggerFactory.getLogger(CustomerApp.class);

	@Autowired
	public CustomerApp(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@PostConstruct
	public void generateTestData() {
        CloseableHttpClient HttpClient = HttpClients.createDefault();
        try {
        	for (int it=0; it< 10; it++) {
	            HttpGet httpGet = new HttpGet("https://randomuser.me/api/?nat=us");
	
	            logger.debug("Executing request " + httpGet.getRequestLine());
	
	            // Create a custom response handler
	            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
	
	            	@Override
	                public String handleResponse(
	                        final HttpResponse response) throws ClientProtocolException, IOException {
	                    int status = response.getStatusLine().getStatusCode();
	                    if (status >= 200 && status < 300) {
	                        HttpEntity entity = response.getEntity();
	                        return entity != null ? EntityUtils.toString(entity) : null;
	                    } else {
	                        // sometimes get unicode data for a customer that causes errors
							//throw new ClientProtocolException("Unexpected response status: " + status);
							logger.error("Unexpected response status: " + status);
							return null;
	                    }
	                }
	
	            };

				String responseBody = HttpClient.execute(httpGet, responseHandler);
				if (responseBody != null) {
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(responseBody);
					JSONArray result = (JSONArray) obj.get("results");
					Iterator i = result.iterator();
					while (i.hasNext()) {
						JSONObject user = (JSONObject) i.next();
						JSONObject name = (JSONObject) user.get("name");
						String fName = ((String)name.get("first"));
						String lName = ((String)name.get("last"));
						JSONObject address = (JSONObject) user.get("location");
						String street = address.get("street").toString();
						String city = address.get("city").toString();
						logger.info("Adding test data customer fName: " + fName + " , lName: " +lName + " ,email: " + fName + "." + lName + "@gmail.com" + " ,street: " + street + " ,city:" + city);
						customerRepository.save(new Customer(fName, lName, "aa@gmail.com", street, city));
					}
				}
				else {
					logger.debug("Skipping customer: " + it);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        finally {
        	try {
        		HttpClient.close();
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}

	public static void main(String[] args) {
		SpringApplication.run(CustomerApp.class, args);
	}

}