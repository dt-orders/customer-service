package com.ewolff.microservice.customer.web;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.io.*;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.ewolff.microservice.customer.Customer;
import com.ewolff.microservice.customer.CustomerRepository;

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

	private String getVersion() {
		logger.info("Current APP_VERSION: " + this.version);
		return this.version;
	}

	private void setVersion(String newVersion) {
		this.version = newVersion;
		logger.info("setVersion: Setting APP_VERSION to: " + this.version);
	}

	private void checkForSlowDown() {
		if (this.version.equals("3")) {
			slowMeDown(true);
		}
	}

	private void checkForAllProblemPatterns() {

		// Check if slow down
		if ((this.version.equals("2")) || (this.version.equals("3"))) {
			slowMeDown(true);
		}

		// Check if throw exception
		if ((this.version.equals("4"))) {
			throwException(true);
		}

		// Check if raise throw Service Unavailable
		if ((this.version.equals("5"))) {
			throwServiceUnavailable(true);
		}
	}

	private void throwServiceUnavailable(boolean isEnabled) {
		logger.info("Running throwServiceUnavailable method");
		
		if (isEnabled) {

			Random rand = new Random();
			// get random numbers
			int randInt1 = rand.nextInt(2);
			int randInt2 = rand.nextInt(2);
			int randInt3 = randInt1 * randInt2;
			logger.info("throwServiceUnavailable: randInt1: " + randInt1);
			logger.info("throwServiceUnavailable: randInt2: " + randInt2);
			logger.info("throwServiceUnavailable: randInt3: " + randInt3);

			if (randInt3 == 0) {
				logger.info("throwServiceUnavailable: Throwing SERVICE_UNAVAILABLE exception");
				throw new ResponseStatusException(
					HttpStatus.SERVICE_UNAVAILABLE, "Returning service unavailable exception"
				);		
			}
		}
	}

	private void throwException(boolean isEnabled)  {
		logger.info("Running throwException method");
		if (isEnabled) {
			String generatedString = RandomStringUtils.randomAlphabetic(10);
			logger.info("throwException: generatedString: " + generatedString);
			if (generatedString != "IamAGoodValue") {
				logger.info("throwException: Throwing INTERNAL_SERVER_ERROR exception");
				throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, "Throwing fake exception"
				);		
			}
		}
	}

	private void slowMeDown(boolean isEnabled) { 
		logger.info("Running slowMeDown method");

		if (isEnabled) {
			logger.info("slowMeDown: Doing a fake slowdown");
			long duration;

			// GetRandom number between 0 and 1
			Random rand = new Random();
			int randInt = rand.nextInt(2);
			if (randInt == 0) {
				// get the default value 
				duration = Long.valueOf(System.getenv("SLEEP_TIME"));
			}
			else
			{
				// make it s fixed value
				duration = 3000;
			}
			logger.info("slowMeDown: randInt: " + randInt);
			logger.info("slowMeDown: duration: " + duration);

			long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(duration, TimeUnit.MILLISECONDS);
			while ( System.nanoTime() < endTime ){
				// wait for end time to occur
			}
		}
		else {
			logger.info("slowMeDown: No need to slowdown");
		}
	}

	@Autowired
	public CustomerController(CustomerRepository customerRepository) {
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
		logger.info("Getting customer detail for id = " + id);
		checkForSlowDown();
		return new ModelAndView("customer", "customer", customerRepository.findById(id).get());
	}

	@RequestMapping("/list.html")
	public ModelAndView customerList(@RequestHeader(value = "x-test-user", required = false) String user) {
		logger.info("Getting customer list");
		// Return the list
		checkForAllProblemPatterns();
		return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView add() throws InterruptedException {
		checkForSlowDown();
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

		checkForSlowDown();
		customer.setId(id);
		customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") long id) throws InterruptedException {

		checkForSlowDown();
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
