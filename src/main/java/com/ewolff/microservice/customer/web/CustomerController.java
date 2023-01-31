package com.ewolff.microservice.customer.web;

import java.util.Calendar;
import java.util.Date;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.ewolff.microservice.customer.Customer;
import com.ewolff.microservice.customer.CustomerRepository;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
public class CustomerController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	private CustomerRepository customerRepository;
	private String version;
	private final OpenFeatureAPI openFeatureAPI;

	private String getVersion() {
		logger.info("Current APP_VERSION: " + this.version);
		return this.version;
	}

	private void setVersion(String newVersion) {
		this.version = newVersion;
		logger.info("setVersion: Setting APP_VERSION to: " + this.version);
	}

	private void throwServiceUnavailable(boolean isEnabled) {
		logger.info("Running throwServiceUnavailable method");
		if (isEnabled) {
			throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE, "Returning service unavailable exception"
			);
		}
	}

	private void throwException(boolean isEnabled)  {
		logger.info("Running throwException method");
		if (isEnabled) {
			try {
				throw new Exception("Throwing fake exception");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void slowMeDown(boolean isEnabled) { 
		logger.info("Running slowMeDown method");
		if (isEnabled) {
			logger.info("slowMeDown: Doing a fake slowdown");
			Integer sleepTime = Integer.valueOf(System.getenv("SLEEP_TIME"));
			try {
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex)
			{
			   Thread.currentThread().interrupt();
			}
		}
		else {
			logger.info("slowMeDown: No need to slowdown");
		}
	}

	@Autowired
	public CustomerController(CustomerRepository customerRepository, OpenFeatureAPI OFApi) {
		this.openFeatureAPI = OFApi;
		this.customerRepository = customerRepository;
		this.version = System.getenv("APP_VERSION");
	}
	
	@RequestMapping(value = "/manifest", method = RequestMethod.GET)
	@ResponseBody
	public String getManifest() {
		 File file = new File("/MANIFEST"); 
		 String manifest = "MANIFEST file not found";
		 try {
			 BufferedReader br = new BufferedReader(new FileReader(file));
			 String line = br.readLine();
			 manifest = line + "<BR>";
			 while ((line = br.readLine()) != null) {
				manifest = manifest + line + "<BR>";
			 }
			 br.close();
		 }
		 catch(Exception e) {
			manifest = e.getMessage();
		 }
		 return manifest;
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView customer(@PathVariable("id") long id) throws Exception {
		final Client openFeatureClient = openFeatureAPI.getClient();
		logger.info("Getting customer detail for id = " + id);
		if ((this.getVersion().equals("3")) || openFeatureClient.getBooleanValue("slowdown", false)) {
			this.slowMeDown(true);
		}
		return new ModelAndView("customer", "customer", customerRepository.findById(id).get());
	}

	@RequestMapping("/list.html")
	public ModelAndView customerList(@RequestHeader(value = "x-test-user", required = false) String user) {
		final Client openFeatureClient = openFeatureAPI.getClient();
		logger.info("Getting customer list");
		// Check if slow down
		if ((this.version.equals("2")) || (this.version.equals("3")) || (openFeatureClient.getBooleanValue("slowdown", false))) {
			slowMeDown(true);
		}
		else {
			slowMeDown(false);
		}

		// Check if throw exception
		if ((this.version.equals("2")) || (openFeatureClient.getBooleanValue("exception", false))) {
			throwException(true);
		}

		// Check if raise throw Service Unavailable
		if ((this.version.equals("2")) || (openFeatureClient.getBooleanValue("service_unvailable", false))) {
			throwServiceUnavailable(true);
		}

		// Return the list
		return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView add() throws InterruptedException {
		if (this.getVersion().equals("3")) {
			this.slowMeDown(true);
		}
		return new ModelAndView("customer", "customer", new Customer());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.POST)
	public ModelAndView post(Customer customer, HttpServletRequest httpRequest) {
		customer = customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.PUT)
	public ModelAndView put(@PathVariable("id") long id, Customer customer,
			HttpServletRequest httpRequest) throws InterruptedException {

		if (this.getVersion().equals("3")) {
			this.slowMeDown(true);
		}
		customer.setId(id);
		customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") long id) throws InterruptedException {

		if (this.getVersion().equals("3")) {
			this.slowMeDown(true);
		}
		
		customerRepository.deleteById(id);
		return new ModelAndView("success");
	}

   @RequestMapping(value = "/version", method = RequestMethod.GET)
   @ResponseBody
   public String showVersion() {
		String version;
		try {
			version = this.getVersion();
		}
		catch(Exception e) {
			version = "APP_VERSION not found";
		}
		return version;
   } 

	@RequestMapping(value = "setversion/{version}", method = RequestMethod.GET)
	public ModelAndView webSetVersion(@PathVariable("version") String newVersion) {
		this.setVersion(newVersion);
		return new ModelAndView("success");
	}

   @RequestMapping(value = "/health", method = RequestMethod.GET)
   @ResponseBody
   public String getHealth() {

	   Date dateNow = Calendar.getInstance().getTime();
	   String health = "{ \"health\":[{\"service\":\"customer-service\",\"status\":\"OK\",\"date\":\"" + dateNow + "\" }]}";
	   return health;
   }
}
