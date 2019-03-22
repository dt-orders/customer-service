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

@ComponentScan
@EnableAutoConfiguration
@Component
public class CustomerApp {

	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerApp(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@PostConstruct
	public void generateTestData() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	for (int it=0; it< 100; it++) {
	            HttpGet httpget = new HttpGet("https://randomuser.me/api/");
	
	            //System.out.println("Executing request " + httpget.getRequestLine());
	
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
	                        throw new ClientProtocolException("Unexpected response status: " + status);
	                    }
	                }
	
	            };
	            String responseBody = httpclient.execute(httpget, responseHandler);
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
	        			//System.out.println(fName + " " +lName + " " + fName + "." + lName + "@gmail.com" + " " + ((String) address.get("street")) + " " + ((String) address.get("city")));
	        			customerRepository.save(new Customer(fName, lName,
	        					"aa@gmail.com", ((String) address.get("street")),((String) address.get("city"))));
	        		}
	        	}
        }
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        finally {
        	try {
        		httpclient.close();
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