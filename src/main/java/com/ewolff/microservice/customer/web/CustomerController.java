package com.ewolff.microservice.customer.web;

import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomerController {

	private CustomerRepository customerRepository;
	private String version;
	private LDClient ldClient;
	private String launchDarklySdkKey = "";

	static final String LAUNCH_DARKLY_ENHANCED_CUSTOMER_LIST_FLAG_KEY = "enhanced-customer-list";
	private boolean launchDarklyEnhancedCustomerListFlag = false;

	private String getVersion() {
		System.out.println("Current APP_VERSION: " + this.version);
		return this.version;
	}

	private void setVersion(String newVersion) {
		this.version = newVersion;
		System.out.println("Setting APP_VERSION to: " + this.version);
	}

	private void showLaunchDarklyFlags(LDClient ldClient, LDUser user){
		System.out.println("==========================================");
		System.out.println("showLaunchDarklyFlags");
		System.out.println("==========================================");
		
		launchDarklyEnhancedCustomerListFlag = ldClient.boolVariation(LAUNCH_DARKLY_ENHANCED_CUSTOMER_LIST_FLAG_KEY, user, false);
		System.out.printf("launchDarklyEnhancedCustomerListFlag : \"%s\"\n", launchDarklyEnhancedCustomerListFlag);
	}

	private void logWheneverAnyFlagChanges(LDClient ldClient, LDUser user) {

		ldClient.getFlagTracker().addFlagChangeListener(event -> {
			String ldKey = event.getKey();
			System.out.println("==========================================");
			System.out.printf("LaunchDarkly Flag \"%s\" has changed\n", ldKey);
			System.out.println("==========================================");

			launchDarklyEnhancedCustomerListFlag = ldClient.boolVariation(ldKey, user, false);

			this.showLaunchDarklyFlags(ldClient, user);
		});
	}

	@Autowired
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.version = System.getenv("APP_VERSION");
		this.launchDarklySdkKey = System.getenv("LAUNCH_DARKLY_SDK_KEY");

		ldClient = new LDClient(launchDarklySdkKey);
		LDUser user = new LDUser.Builder("aa0ceb")
			.anonymous(true)
			.build();

		logWheneverAnyFlagChanges(ldClient, user);
		this.showLaunchDarklyFlags(ldClient, user);

		// TODO: do I need to add this somewhere?
		//ldClient.close();
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

	@RequestMapping(value = "/showflags", method = RequestMethod.GET)
	@ResponseBody
	public String showFlags() {
		 String response;
		 response = "<b>LaunchDarkly Flags</b><BR>";
		 response += "launchDarklyEnhancedCustomerListFlag : " + launchDarklyEnhancedCustomerListFlag;
		 return response;
	} 

	@RequestMapping(value = "/{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView customer(@PathVariable("id") long id) {
		return new ModelAndView("customer", "customer",
				customerRepository.findById(id).get());
	}

	@RequestMapping("/list.html")
	public ModelAndView customerList() {

		if ((this.getVersion().equals("2") || launchDarklyEnhancedCustomerListFlag)) {
			System.out.println("Response Time problem = ON");
			try
			{
				// ************************************************
				// Response Time problem
				// ************************************************
				Thread.sleep(5000);
			}
			catch(InterruptedException ex)
			{
			   Thread.currentThread().interrupt();
			}
			return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
		}
		else {
			System.out.println("Response Time problem = OFF");
			return new ModelAndView("customerlist", "customers",
					customerRepository.findAll());
		}
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView add() {
		return new ModelAndView("customer", "customer", new Customer());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.POST)
	public ModelAndView post(Customer customer, HttpServletRequest httpRequest) {
		customer = customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.PUT)
	public ModelAndView put(@PathVariable("id") long id, Customer customer,
			HttpServletRequest httpRequest) {
		customer.setId(id);
		customerRepository.save(customer);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") long id) {
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
