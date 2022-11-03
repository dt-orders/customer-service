package com.ewolff.microservice.customer.web;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.io.*;

import javax.servlet.http.HttpServletRequest;

import com.ewolff.microservice.customer.Customer;
import com.ewolff.microservice.customer.CustomerRepository;
import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;

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

	private CustomerRepository customerRepository;
	private String version;
	private LDClient ldClient;
	private String launchDarklySdkKey = "";

	static final String LAUNCH_DARKLY_NEW_FEATURE_1_FLAG_KEY = "new-feature-1";
	static final String LAUNCH_DARKLY_NICE_TO_HAVE_FEATURE_1_FLAG_KEY = "nice-to-have-feature-1";
	private boolean launchDarklyNewFeature1Flag = false;
	private boolean launchDarklyNiceToHaveFeature1Flag = false;

	private String getVersion() {
		System.out.println("Current APP_VERSION: " + this.version);
		return this.version;
	}

	private void setVersion(String newVersion) {
		this.version = newVersion;
		System.out.println("Setting APP_VERSION to: " + this.version);
	}
	private void niceToHaveFeature1(boolean failureProblemEnabled) {
		System.out.println("Running niceToHaveFeature1");
		if (failureProblemEnabled) {
			throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE, "niceToHaveFeature1 returning service unavailable."
			);
		}
	}

	private void showLaunchDarklyFlags(LDClient ldClient, LDUser user){
		System.out.println("==========================================");
		System.out.println("showLaunchDarklyFlags");
		System.out.println("==========================================");
		System.out.printf("launchDarklyNewFeature1Flag : \"%s\"\n", launchDarklyNewFeature1Flag);
		System.out.printf("launchDarklyNiceToHaveFeature1Flag : \"%s\"\n", launchDarklyNiceToHaveFeature1Flag);
	}

	private void logWheneverAnyFlagChanges(LDClient ldClient, LDUser user) {

		ldClient.getFlagTracker().addFlagChangeListener(event -> {
			String ldKey = event.getKey();
			System.out.println("==========================================");
			System.out.printf("LaunchDarkly Flag \"%s\" has changed\n", ldKey);
			System.out.println("==========================================");

			if (ldKey == LAUNCH_DARKLY_NEW_FEATURE_1_FLAG_KEY) {
				launchDarklyNewFeature1Flag = ldClient.boolVariation(ldKey, user, false);
			}
			if (ldKey == LAUNCH_DARKLY_NICE_TO_HAVE_FEATURE_1_FLAG_KEY) {
				launchDarklyNiceToHaveFeature1Flag = ldClient.boolVariation(ldKey, user, false);
			}

			this.showLaunchDarklyFlags(ldClient, user);
		});
	}

	private void slowMeDown() throws InterruptedException {
		System.out.println("Doing a fake slowdown");
		// ************************************************
		// Response Time problem
		// ************************************************
		Integer sleepTime = Integer.valueOf(System.getenv("SLEEP_TIME"));
		Thread.sleep(sleepTime);
	}

	@Autowired
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.version = System.getenv("APP_VERSION");

		// setup LaunchDarkly client if the key is set
		this.launchDarklySdkKey = System.getenv("LAUNCH_DARKLY_SDK_KEY");

		if (this.launchDarklySdkKey.length()> 0) {
			System.out.println("Found LAUNCH_DARKLY_SDK_KEY: " + this.launchDarklySdkKey + " setting up LDClient");
			ldClient = new LDClient(launchDarklySdkKey);
		}
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

		if (this.getVersion().equals("3")) {
			this.slowMeDown();
		}
		return new ModelAndView("customer", "customer", customerRepository.findById(id).get());
	}

	private ModelAndView getCustomerList(boolean responseTimeProblemEnabled) {

		System.out.println("getCustomerList() - Response Time problem = " + responseTimeProblemEnabled);

		if (responseTimeProblemEnabled) {
			try
			{
				slowMeDown();
			}
			catch(InterruptedException ex)
			{
			   Thread.currentThread().interrupt();
			}
			return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
		}
		else {
			return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
		}
	}

	@RequestMapping("/list.html")
	public ModelAndView customerList(@RequestHeader(value = "x-test-user", required = false) String user) {

		// only do this logic if using launchDarkly
		if (this.launchDarklySdkKey.length()> 0) {

			Random rand = new Random();
			String randomUserId = Integer.toString(rand.nextInt(1000000) + 1);
			LDUser ldUser = new LDUser.Builder(randomUserId).build();
		
			// get launchDarkly values for this user
			launchDarklyNewFeature1Flag = ldClient.boolVariation(LAUNCH_DARKLY_NEW_FEATURE_1_FLAG_KEY, ldUser, false);
			launchDarklyNiceToHaveFeature1Flag = ldClient.boolVariation(LAUNCH_DARKLY_NICE_TO_HAVE_FEATURE_1_FLAG_KEY, ldUser, false);

			// show ldUser starting values
			this.showLaunchDarklyFlags(ldClient, ldUser);
			// register ldUser for dynamic flag changes
			this.logWheneverAnyFlagChanges(ldClient, ldUser);
		}

		// call a separate function so we can profile easier
		niceToHaveFeature1(launchDarklyNiceToHaveFeature1Flag);

		// call a separate function so we can profile easier
		if ((this.version.equals("2") || this.version.equals("3") || launchDarklyNewFeature1Flag)) {
			return getCustomerList(true);
		}
		else {
			return getCustomerList(false);
		}
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView add() throws InterruptedException {
		if (this.getVersion().equals("3")) {
			this.slowMeDown();
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
			this.slowMeDown();
		}
		customer.setId(id);
		customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") long id) throws InterruptedException {

		if (this.getVersion().equals("3")) {
			this.slowMeDown();
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
