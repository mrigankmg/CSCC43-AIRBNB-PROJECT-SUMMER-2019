package sql;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

import users.*;

public class CommandLine {
    // 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
    // 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	private static final String[] userColumns = new String [] {"email", "first_name", "last_name", "dob", "address", "occupation", "sin", "password", "cc", "num_cancellations"};
	private static final String[] locationColumns = new String [] {"listing_num", "suite_num","house_num", "street_name","postal_code", "city", "country", "latitude", "longitude", "type"};
	private static final String[] availabilityColumns = new String [] {"listing_num", "start_date", "end_date", "cost_per_day"};
	private static final String[] hostColumns = new String [] {"listing_num", "sin"};
	private static final String[] bookingColumns = new String[] {"booking_num", "listing_num", "start_date", "end_date", "cost_per_day","sin", "renter_comment_on_listing","renter_comment_on_host","host_comment_on_renter","listing_rating","host_rating", "renter_rating"};
	private static final String[] amenitiesColumns = new String [] {"listing_num", "toilet_paper_included", "wifi_included", "towels_included", "iron_included", "pool_included", "ac_included", "fireplace_included"};
	private static final Map<String, String> locationTypeOptionMap = new HashMap<String, String>(){{
	    put("1", "Apartment");
	    put("2", "House");
	    put("3", "Room");
	}};
	private User user;

	//Public functions - CommandLine State Functions

    /* Function used for initializing an instance of current
     * class
     */
	public boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
			success = sqlMngr.connect();
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

    /* Function that acts as destructor of an instance of this class.
     * Performs some housekeeping setting instance's private field
     * to null
     */
	public void endSession() {
		if (sqlMngr != null)
			sqlMngr.disconnect();
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}

    /* Function that executes an infinite loop and activates the respective
     * functionality according to user's choice. At each time it also outputs
     * the menu of core functionalities supported from our application.
     */
	public void execute() throws SQLException, ParseException {
		if (sc != null && sqlMngr != null) {
			System.out.println("");
			System.out.println("****************************");
			System.out.println("***CONNECTION ESTABLISHED***");
			System.out.println("****************************");
			try {
				mainMenu();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("");
			System.out.println("Connection could not been established!");
			System.out.println("");
		}
	}

	private void mainMenu() throws SQLException, ParseException, IOException {
		user = null;
		String input = "";
		int choice = -1;
		System.out.println("");
		do {
			System.out.println("=========MAIN MENU=========");
			System.out.println("0. Exit");
			System.out.println("1. Login");
			System.out.println("2. Sign-Up");
			System.out.println("3. Delete User");
			System.out.println("4. Generate Reports");
			System.out.print("Choose one of the options [0-4]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { 
				case 0:
					break;
				case 1:
					login();
					break;
				case 2:
					signUpMenu();
					break;
				case 3:
					deleteUser();
					break;
				case 4:
					generateReports();
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void login() throws SQLException, ParseException {
		String[] cred = new String[2];
		System.out.println("");
		do {
			System.out.println("=========LOGIN=========");
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
			System.out.print("Password: ");
			cred[1] = sc.nextLine();
		} while (!checkLoginCredentials(cred[0], cred[1]));
		List<String> userInfo = sqlMngr.getUserInfo(cred[0]);
		if (isHost(userInfo)) {
			user = new Host(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7), userInfo.get(9));
			System.out.println("");
			System.out.println("Welcome " + user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase() + "!");
			hostHome();
		} else {
			user = new Renter(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7), userInfo.get(8), userInfo.get(9));
			System.out.println("");
			System.out.println("Welcome " + user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase() + "!");
			renterHome();
		}
	}

	private void hostHome() throws SQLException, ParseException {
		String input = "";
		int choice = -1;
		System.out.println("");
		do {
			System.out.println("=========HOME=========");
			System.out.println("0. Exit");
			System.out.println("1. Create Listing");
			System.out.println("2. Modify Listing");
			System.out.println("3. Delete Listing");
			System.out.println("4. Cancel Booking");
			System.out.println("5. Comment/Rate Renter");
			System.out.println("6. Log Out");
			System.out.print("Choose one of the options [0-6]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { 
				case 0:
					break;
				case 1:
					createListingForm();
					break;
				case 2:
					modifyListing();
					break;
				case 3:
					deleteListing();
					break;
				case 4:
					cancelBooking(true);
					break;
				case 5:
					feedbackForRenter();
					hostHome();
					break;
				case 6:
					try {
						mainMenu();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0 && input.compareTo("5") != 0 && input.compareTo("6") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void renterHome() throws SQLException, ParseException {
		String input = "";
		int choice = -1;
		System.out.println("");
		do {
			System.out.println("=========HOME=========");
			System.out.println("0. Exit");
			System.out.println("1. Search Listings");
			System.out.println("2. Make Booking");
			System.out.println("3. Cancel Booking");
			System.out.println("4. Comment/Rate Listing & Host");
			System.out.println("5. Log Out");
			System.out.print("Choose one of the options [0-5]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { 
				case 0:
					break;
				case 1:
					searchListings();
					renterHome();
					break;
				case 2:
					startBookingByListingNumber();
					renterHome();
					break;
				case 3:
					cancelBooking(false);
					renterHome();
					break;
				case 4:
					feedbackForHost();
					renterHome();
					break;
				case 5:
					try {
						mainMenu();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0 && input.compareTo("5") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void createListingForm() throws SQLException, ParseException {
		String listing_num = UUID.randomUUID().toString();
		String[] location_vals = new String[10];
		String[] availability_vals = new String[4];
		String[] host_vals = new String[2];
		String[] amenity_vals = new String[8];
		location_vals[0] = listing_num;
		availability_vals[0] = listing_num;
		host_vals[0] = listing_num;
		host_vals[1] = user.getSin();
		amenity_vals[0] = listing_num;
		System.out.println("");
		System.out.println("=========Create Listing=========");
		do {
			System.out.print("Listing Type [1- Apartment, 2- House, 3- Room]: ");
			location_vals[9] = sc.nextLine().trim();
			if(!location_vals[9].equals("1") && !location_vals[9].equals("2") && !location_vals[9].equals("3")) {
				invalidEntry();
			}
		} while(!location_vals[9].equals("1") && !location_vals[9].equals("2") && !location_vals[9].equals("3"));
		location_vals[9] = locationTypeOptionMap.get(location_vals[9]);
		do {
			System.out.print("House Number: ");
			location_vals[2] = sc.nextLine().trim();
			if(location_vals[2].equals("")) {
				invalidEntry();
			}
		} while(location_vals[2].equals(""));
		if(location_vals[9].equals("House")) {
			location_vals[1] = null;
		} else {
			do {
				System.out.print("Suite Number: ");
				location_vals[1] = sc.nextLine().trim();
				if(location_vals[1].equals("") && location_vals[9].equals("Apartment")) {
					invalidEntry();
				}
			} while(location_vals[1].equals("") && location_vals[9].equals("Apartment"));
			if (location_vals[1].equals("")) {
				location_vals[1] = null;
			}
		}
		do {
			System.out.print("Street Name: ");
			location_vals[3] = sc.nextLine().trim();
			if(location_vals[3].equals("")) {
				invalidEntry();
			}
		} while(location_vals[3].equals(""));
		do {
			System.out.print("Postal Code: ");
			location_vals[4] = sc.nextLine().trim();
			if(location_vals[4].equals("")) {
				invalidEntry();
			}
		} while(location_vals[4].equals(""));
		do {
			System.out.print("City: ");
			location_vals[5] = sc.nextLine().trim();
			if(location_vals[5].equals("")) {
				invalidEntry();
			}
		} while(location_vals[5].equals(""));
		do {
			System.out.print("Country: ");
			location_vals[6] = sc.nextLine().trim();
			if(location_vals[6].equals("")) {
				invalidEntry();
			}
		} while(location_vals[6].equals(""));
		do {
			System.out.print("Latitude: ");
			location_vals[7] = sc.nextLine().trim();
		} while(!isValidLatitude(location_vals[7]));
		do {
			System.out.print("Longitude: ");
			location_vals[8] = sc.nextLine().trim();
		} while(!isValidLongitude(location_vals[8]));
		do {
			System.out.print("Availability Start Date (dd/mm/yyyy): ");
			availability_vals[1] = sc.nextLine().trim();
		} while(!isValidStartDate(availability_vals[1]) || !isNotOverlap(availability_vals[1], availability_vals[1], location_vals[1], location_vals[2], location_vals[7], location_vals[8], false, "", ""));
		do {
			System.out.print("Availability End Date (dd/mm/yyyy): ");
			availability_vals[2] = sc.nextLine().trim();
		} while(!isValidEndDate(availability_vals[1], availability_vals[2]) || !isNotOverlap(availability_vals[1], availability_vals[2], location_vals[1], location_vals[2], location_vals[7], location_vals[8], false, "", ""));
		List<List<String>> toiletPaperYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"toilet_paper_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> wifiYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"wifi_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> towelsYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"towels_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> ironYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"iron_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> poolYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"pool_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> acYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"ac_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		List<List<String>> fireplaceYes = sqlMngr.select("(SELECT * FROM amenities NATURAL JOIN location) AS t", new String[] {"listing_num"}, new String[] {"fireplace_included", "city", "country", "type"}, new String[] {"y",location_vals[5], location_vals[6], location_vals[9]});
		Map<String, Integer> amenitiesCountMap = new HashMap<String, Integer>();
		amenitiesCountMap.put("toilet paper", toiletPaperYes.size());
		amenitiesCountMap.put("wifi", wifiYes.size());
		amenitiesCountMap.put("towels", towelsYes.size());
		amenitiesCountMap.put("iron", ironYes.size());
		amenitiesCountMap.put("pool", poolYes.size());
		amenitiesCountMap.put("ac", acYes.size());
		amenitiesCountMap.put("fireplace", fireplaceYes.size());
		amenitiesCountMap = amenitiesCountMap.entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(3)
		        .collect(
		            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		System.out.println("");
		System.out.println("Based on other similar listings in " + location_vals[5].substring(0,1).toUpperCase() + location_vals[5].substring(1).toLowerCase() + ", " + location_vals[6].substring(0,1).toUpperCase() + location_vals[6].substring(1).toLowerCase() + ", we suggest to include the following amenities: ");
		amenitiesCountMap.entrySet().forEach(entry->{
		    System.out.println("-> " + entry.getKey());
		 });
		System.out.println("");
		do {
			System.out.print("Toilet Paper Included? (y/n): ");
			amenity_vals[1] = sc.nextLine().trim();
			if(!amenity_vals[1].equalsIgnoreCase("y") && !amenity_vals[1].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[1].equalsIgnoreCase("y") && !amenity_vals[1].equalsIgnoreCase("n"));
		do {
			System.out.print("WiFi Included? (y/n): ");
			amenity_vals[2] = sc.nextLine().trim();
			if(!amenity_vals[2].equalsIgnoreCase("y") && !amenity_vals[2].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[2].equalsIgnoreCase("y") && !amenity_vals[2].equalsIgnoreCase("n"));
		do {
			System.out.print("Towels Included? (y/n): ");
			amenity_vals[3] = sc.nextLine().trim();
			if(!amenity_vals[3].equalsIgnoreCase("y") && !amenity_vals[3].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[3].equalsIgnoreCase("y") && !amenity_vals[3].equalsIgnoreCase("n"));
		do {
			System.out.print("Iron Included? (y/n): ");
			amenity_vals[4] = sc.nextLine().trim();
			if(!amenity_vals[4].equalsIgnoreCase("y") && !amenity_vals[4].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[4].equalsIgnoreCase("y") && !amenity_vals[4].equalsIgnoreCase("n"));
		do {
			System.out.print("Pool Included? (y/n): ");
			amenity_vals[5] = sc.nextLine().trim();
			if(!amenity_vals[5].equalsIgnoreCase("y") && !amenity_vals[5].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[5].equalsIgnoreCase("y") && !amenity_vals[5].equalsIgnoreCase("n"));
		do {
			System.out.print("A/C Included? (y/n): ");
			amenity_vals[6] = sc.nextLine().trim();
			if(!amenity_vals[6].equalsIgnoreCase("y") && !amenity_vals[6].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[6].equalsIgnoreCase("y") && !amenity_vals[6].equalsIgnoreCase("n"));
		do {
			System.out.print("Fireplace Included? (y/n): ");
			amenity_vals[7] = sc.nextLine().trim();
			if(!amenity_vals[7].equalsIgnoreCase("y") && !amenity_vals[7].equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!amenity_vals[7].equalsIgnoreCase("y") && !amenity_vals[7].equalsIgnoreCase("n"));
		List<List<String>> costs = sqlMngr.select("(SELECT * FROM availability NATURAL JOIN location NATURAL JOIN amenities) AS t", new String [] {"cost_per_day"}, new String [] {"type", "city", "country", "toilet_paper_included", "wifi_included", "towels_included", "iron_included", "pool_included", "ac_included", "fireplace_included"}, new String [] {location_vals[9], location_vals[5], location_vals[6], amenity_vals[1], amenity_vals[2], amenity_vals[3], amenity_vals[4], amenity_vals[5], amenity_vals[6], amenity_vals[7]});
		double suggested = 0;
		for (List<String> cost : costs) {
			suggested += Double.parseDouble(cost.get(0));
		}
		if (costs.size() > 0) {
			suggested /= costs.size();
		} else {
			if(location_vals[9].equals("House")) {
				suggested = 200;
			} else if (location_vals[9].equals("Apartment")) {
				suggested = 120;
			} else {
				suggested = 50;
			}
			if(amenity_vals[1].equalsIgnoreCase("y")) {
				suggested += 5;
			}
			if(amenity_vals[2].equalsIgnoreCase("y")) {
				suggested += 10;
			}
			if(amenity_vals[3].equalsIgnoreCase("y")) {
				suggested += 5;
			}
			if(amenity_vals[4].equalsIgnoreCase("y")) {
				suggested += 10;
			}
			if(amenity_vals[5].equalsIgnoreCase("y")) {
				suggested += 30;
			}
			if(amenity_vals[6].equalsIgnoreCase("y")) {
				suggested += 5;
			}
			if(amenity_vals[7].equalsIgnoreCase("y")) {
				suggested += 15;
			}
		}
		System.out.println("");
		System.out.println("Based on other similar listings in " + location_vals[5].substring(0,1).toUpperCase() + location_vals[5].substring(1).toLowerCase() + ", " + location_vals[6].substring(0,1).toUpperCase() + location_vals[6].substring(1).toLowerCase() + ", we suggest a price of $" + suggested + "/day.");
		System.out.println("");
		do {
			System.out.print("Cost per Day: ");
			availability_vals[3] = sc.nextLine().trim();
		} while(!isDouble(availability_vals[3]));
		sqlMngr.insert("location", locationColumns, location_vals);
		sqlMngr.insert("availability", availabilityColumns, availability_vals);
		sqlMngr.insert("host", hostColumns, host_vals);
		sqlMngr.insert("amenities", amenitiesColumns, amenity_vals);
		System.out.println("");
		System.out.println("Listing successfully created! Listing number is '" + listing_num + "'.");
		hostHome();
	}

	private void deleteListing() throws SQLException, ParseException {
		System.out.println("");
		System.out.println("=========DELETE LISTING=========");
		String listing_num;
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, new String[] {"sin"}, new String [] {user.getSin()});
		do {
			System.out.print("Listing No.: ");
			listing_num = sc.nextLine().trim();
		} while (!listingInList(allListings, listing_num));
		sqlMngr.delete("listing_num", listing_num);
		System.out.println("");
		System.out.println("You have successfully deleted listing '" + listing_num + "'.");
		hostHome();
	}

	private boolean listingInList(List<List<String>> allListings, String listing_num) {
		for(List<String> listing : allListings) {
			if (listing.get(0).equalsIgnoreCase(listing_num)) {
				return true;
			}
		}
		System.out.println("");
		System.out.println("You don't have any listing with the given number!");
		System.out.println("");
		return false;
	}
	
	private void cancelBooking(boolean host) {
		System.out.println("");
		System.out.println("=========CANCEL BOOKING=========");
		String booking_num;
		if(!host) {
			do {
				System.out.print("Booking No.: ");
				booking_num = sc.nextLine().trim();
			} while (!isValidBookingNumForRenter(booking_num));
		} else {
			do {
				System.out.print("Booking No.: ");
				booking_num = sc.nextLine().trim();
			} while (!isValidBookingNumForHost(booking_num));
		}
		List<String> availInfo = sqlMngr.select("booking", new String[] {"listing_num", "start_date", "end_date", "cost_per_day"}, new String [] {"booking_num"}, new String [] {booking_num}).get(0);
		sqlMngr.delete("booking_num", booking_num);
		sqlMngr.insert("availability", availabilityColumns, new String[] {availInfo.get(0), availInfo.get(1), availInfo.get(2), availInfo.get(3)});
		sqlMngr.update("user", new String[] {"sin"}, new String[] {user.getSin()}, new String[] {"num_cancellations"}, new String[] {Integer.toString(user.getCancellations() + 1)});
		user.setCancellations(user.getCancellations() + 1);
		System.out.println("Your Booking was Cancelled !");
	}
	
	private boolean isValidBookingNumForRenter(String booking_num) {
		List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num"}, new String[] {"sin"}, new String [] {user.getSin()});
		for(List<String> booking : allBookings) {
			if (booking.get(0).equals(booking_num)) {
				return true;
			}
		}
		System.out.println("");
		System.out.println("You don't have a booking with the number '" + booking_num + "'!");
		System.out.println("");
		return false;
	}
	
	private boolean isValidBookingNumForHost(String booking_num) {
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, new String[] {"sin"}, new String [] {user.getSin()});
		for(List<String> listing : allListings) {
			List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num"}, new String[] {"listing_num"}, new String [] {listing.get(0)});
			for(List<String> booking : allBookings) {
				if (booking.get(0).equals(booking_num)) {
					return true;
				}
			}
		}
		System.out.println("");
		System.out.println("You don't have any listing booked with the booking number '" + booking_num + "'!");
		System.out.println("");
		return false;
	}
	
	
	private void modifyListing() throws SQLException, ParseException {
		System.out.println("");
		System.out.println("=========MODIFY LISTING=========");
		String[] newVals = new String[3];
		String listing_num;
		String start_date;
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, new String[] {"sin"}, new String [] {user.getSin()});
		do {
			System.out.print("Listing No. of Listing to Modify: ");
			listing_num = sc.nextLine().trim();
		} while (!listingInList(allListings, listing_num));
		do {
			System.out.print("Start Date of Listing to Modify: ");
			start_date = sc.nextLine().trim();
		} while (!isValidStartDate(start_date) || !isValidStartDateToBeModified(start_date, listing_num));
		List<String> location = sqlMngr.select("location", new String[] {"suite_num", "house_num", "latitude", "longitude"}, new String[] {"listing_num"}, new String [] {listing_num}).get(0);
//		List<List<String>> listingsWithoutModficationListing = new ArrayList<List<String>>();
//		List<List<String>> allAvailabilities = sqlMngr.select("availability", new String[] {"start_date"}, "listing_num", new String [] {listing_num});
//		for(List<String> listing : allListings) {
//			if (!listing.get(0).equalsIgnoreCase(listing_num)) {
//				listingsWithoutModficationListing.add(listing);
//			}
//		}
		do {
			System.out.print("Start Date (Leave blank for no modification): ");
			newVals[0] = sc.nextLine().trim();
			if(newVals[0].equals("")) {
				newVals[0] = start_date;
				break;
			}
		} while (!isValidStartDate(newVals[0]) || !isNotOverlap(newVals[0], newVals[0], location.get(0), location.get(1), location.get(2), location.get(3), true, start_date, listing_num));
		do {
			System.out.print("End Date (Leave blank for no modification): ");
			newVals[1] = sc.nextLine().trim();
			if(newVals[1].equals("")) {
				break;
			}
		} while (!isValidEndDate(newVals[0], newVals[1]) || !isNotOverlap(newVals[0], newVals[1], location.get(0), location.get(1), location.get(2), location.get(3), true, start_date, listing_num));
		do {
			System.out.print("Cost per Day (Leave blank for no modification): ");
			newVals[2] = sc.nextLine().trim();
			if(newVals[2].equals("")) {
				break;
			}
		} while (!isDouble(newVals[2]));
		if (!newVals[0].equals(start_date)) {

			sqlMngr.update("availability ", new String[] { "listing_num","start_date"}, new String[] {listing_num, start_date}, new String [] {"start_date"}, new String [] {newVals[0]});

		}
		if (!newVals[1].equals("")) {

			sqlMngr.update("availability ", new String[] { "listing_num","start_date"}, new String[] {listing_num, start_date}, new String [] {"end_date"}, new String [] {newVals[1]});
		}
		if (!newVals[2].equals("")) {

			sqlMngr.update("availability ", new String[] { "listing_num","start_date"}, new String[] {listing_num, start_date}, new String [] {"cost_per_day"}, new String [] {newVals[2]});

		}

		hostHome();

	}
	
	private boolean isValidStartDateToBeModified(String startDate, String listingNum) {
		List<List<String>> startDates = sqlMngr.select("availability", new String[] {"start_date"}, new String[] {"listing_num"}, new String [] {listingNum});
		for(List<String> date : startDates) {
			if (date.get(0).equals(startDate)) {
				return true;
			}
		}
		System.out.println("");
		System.out.println("Your listing '" + listingNum + "' does not have any current availability with start date as '" + startDate + "'.");
		System.out.println("");
		return false;
	}

	private void signUpMenu() throws SQLException, ParseException {
		String input = "";
		int choice = -1;
		System.out.println("");
		do {
			System.out.println("=========SIGN UP=========");
			System.out.println("0. Exit");
			System.out.println("1. Host");
			System.out.println("2. Renter");
			System.out.print("Choose to sign up either as a host or a renter [0-2]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { 
				case 0:
					break;
				case 1:
					signUpForm(false);
					break;
				case 2:
					signUpForm(true);
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void signUpForm(boolean renterSignUp) throws SQLException, ParseException {
		System.out.println("");
		System.out.println("=========SIGN UP FORM=========");
		String[] cred = new String[10];
		do {
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
			if(cred[0].equals("")) {
				invalidEntry();
			}
		} while (checkExistingAccount("user", "email", cred[0], false) || cred[0].equals(""));
		do {
			System.out.print("First Name: ");
			cred[1] = sc.nextLine().trim();
			if(cred[1].equals("")) {
				invalidEntry();
			}
		} while (cred[1].equals(""));
		do {
			System.out.print("Last Name: ");
			cred[2] = sc.nextLine().trim();
			if(cred[2].equals("")) {
				invalidEntry();
			}
		} while (cred[2].equals(""));
		do {
			System.out.print("DOB (dd/mm/yyyy): ");
			cred[3] = sc.nextLine();
		} while(!isAdult(cred[3]));
		do {
			System.out.print("Address: ");
			cred[4] = sc.nextLine().trim();
			if(cred[4].equals("")) {
				invalidEntry();
			}
		} while (cred[4].equals(""));
		do {
			System.out.print("Occupation: ");
			cred[5] = sc.nextLine().trim();
			if(cred[5].equals("")) {
				invalidEntry();
			}
		} while (cred[5].equals(""));
		do {
			System.out.print("SIN (9 digits): ");
			cred[6] = sc.nextLine().trim();
		} while (!isValidSin(cred[6]) || checkExistingAccount("user", "sin", cred[6], false));
		System.out.print("Password: ");
		cred[7] = sc.nextLine();
		if (renterSignUp) {
			do {
				System.out.print("Credit Card No. (>= 13 digits, <= 19 digits): ");
				cred[8] = sc.nextLine().trim();
			} while (!isValidCC(cred[8]));
		} else {
			cred[8] = null;
		}
		cred[9] = "0";
		sqlMngr.insert("user", userColumns, cred);
		System.out.println("");
		System.out.println("You have successfully signed up!");
		try {
			mainMenu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteUser() throws SQLException, ParseException {
		System.out.println("");
		System.out.println("=========DELETE USER=========");
		String email;
		do {
			System.out.print("Email: ");
			email = sc.nextLine().trim();
		} while (!checkExistingAccount("user", "email", email, true));
		List<String> userInfo = sqlMngr.getUserInfo(email);
		sqlMngr.delete("email", email);
		if(isHost(userInfo)) {
			List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, new String[] {"sin"}, new String [] {userInfo.get(6)});
			for(List<String> listing : allListings) {
				sqlMngr.delete("listing_num", listing.get(0));
			}
		} else {
			List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num", "listing_num", "start_date", "end_date", "cost_per_day"}, new String[] {"sin"}, new String [] {userInfo.get(6)});
			for(List<String> booking : allBookings) {
				sqlMngr.delete("booking_num", booking.get(0));
				sqlMngr.insert("availability", availabilityColumns, new String[] {booking.get(1), booking.get(2), booking.get(3), booking.get(4)});
			}
		}
		System.out.println("");
		System.out.println("User with email '" + email + "' has been deleted.");
		try {
			mainMenu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Print menu options
	private void invalidEntry() {
		System.out.println("");
		System.out.println("Not a valid entry! Please try again.");
		System.out.println("");
	}

	private boolean isAdult(String date) {
		Date dob = isValidDate(date);
		if (dob != null) {
			Calendar c = Calendar.getInstance();
	        c.setTime(dob);
	        int y = c.get(Calendar.YEAR);
	        int m = c.get(Calendar.MONTH) + 1;
	        int d = c.get(Calendar.DATE);
	        LocalDate ld = LocalDate.of(y, m, d);
	        LocalDate now = LocalDate.now();
	        Period diff = Period.between(ld, now);
	        if(diff.getYears() >= 18) {
	        	return true;
	        } else {
		    	System.out.println("");
		    	System.out.println("Not over 18 years of age. Please enter again.");
		    	System.out.println("");
	        	return false;
	        }
		} else {
			return false;
		}
	}

	private boolean isValidStartDate(String date) {
		Date start = isValidDate(date);
		if (start != null) {
			Calendar c = Calendar.getInstance();
	        c.setTime(start);
	        int y = c.get(Calendar.YEAR);
	        int m = c.get(Calendar.MONTH) + 1;
	        int d = c.get(Calendar.DATE);
	        LocalDate ld = LocalDate.of(y, m, d);
	        LocalDate now = LocalDate.now();
	        if(ld.isAfter(now)) {
	        	return true;
	        } else {
		    	System.out.println("");
		    	System.out.println("Please enter a start date in the future.");
		    	System.out.println("");
	        	return false;
	        }
		} else {
			return false;
		}
	}

	private boolean isValidEndDate(String startDate, String endDate) {
		Date start = isValidDate(startDate);
		Date end = isValidDate(endDate);
		if (end != null) {
	        if(end.after(start)) {
	        	return true;
	        } else {
		    	System.out.println("");
		    	System.out.println("Please enter an end date that is after the start date.");
		    	System.out.println("");
	        	return false;
	        }
		} else {
			return false;
		}
	}
	
	public Date isValidDate(String value) {
	    LocalDateTime ldt = null;
	    DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
	    try {
	        ldt = LocalDateTime.parse(value, fomatter);
	        String check = ldt.format(fomatter);
	        if (check.equals(value)) {
	        	return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	        } else {
		    	System.out.println("");
		    	System.out.println("Date does not match required format. Please enter again.");
		    	System.out.println("");
	        	return null;
	        }
	    } catch (DateTimeParseException e) {
	        try {
	            LocalDate ld = LocalDate.parse(value, fomatter);
	            String check = ld.format(fomatter);
		        if (check.equals(value)) {
		        	return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
		        } else {
			    	System.out.println("");
			    	System.out.println("Date does not match Hi required format. Please enter again.");
			    	System.out.println("");
		        	return null;
		        }
	        } catch (DateTimeParseException exp) {
//	            try {
//	                LocalTime lt = LocalTime.parse(value, fomatter);
//	                String check = lt.format(fomatter);
//	    	        if (check.equals(value)) {
//	    	        	return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
//	    	        } else {
//	    		    	System.out.println("");
//	    		    	System.out.println("Date does not match required format. Please enter again.");
//	    		    	System.out.println("");
//	    	        	return null;
//	    	        }
//	            } catch (DateTimeParseException e2) {
//	            }
	        }
	    }
    	System.out.println("");
    	System.out.println("Date does not match required format. Please enter again.");
    	System.out.println("");
	    return null;
	}

	private boolean isValidSin(String s) {
		boolean result = s.length() == 9 && isInteger(s);
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid SIN.");
	    	System.out.println("");
		}
		return result;
	}

	private boolean isValidCC(String s) {
		boolean result = s.length() >= 13 && s.length() <= 19 && isInteger(s);
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid CC.");
	    	System.out.println("");
		}
		return result;
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			System.out.println("");
			System.out.println("Please enter a valid decimal value.");
			System.out.println("");
			return false;
		}
	}

	private boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			System.out.println("");
			System.out.println("Please enter a valid decimal value.");
			System.out.println("");
			return false;
		}
	}
	
	private boolean isValidLongitude(String s) {
		try {
			Double num = Double.parseDouble(s);
			if (num >= -180 && num <= 180) {
				return true;
			}
		} catch (NumberFormatException e) {
		}
		System.out.println("");
		System.out.println("Please enter a valid longitude value.");
		System.out.println("");
		return false;
	}
	
	private boolean isValidLatitude(String s) {
		try {
			Double num = Double.parseDouble(s);
			if (num >= -90 && num <= 90) {
				return true;
			}
		} catch (NumberFormatException e) {
		}
		System.out.println("");
		System.out.println("Please enter a valid latitude value.");
		System.out.println("");
		return false;
	}

	private boolean checkExistingAccount(String table, String column, String value, boolean forDelete) throws SQLException {
		if (sqlMngr.select(table, new String[] {column}, new String[] {column}, new String[] {value}).size() > 0) {
			if (!forDelete) {
				System.out.println("");
				System.out.println("Account with this " + column + " already exists.");
				System.out.println("");
			}
			return true;
		}
		if (forDelete) {
			System.out.println("");
			System.out.println("Account with this " + column + " does not exist.");
			System.out.println("");
		}
		return false;
	}

	private boolean checkLoginCredentials(String email, String password) throws SQLException {
		List<List<String>> vals = sqlMngr.select("user", new String[] {"password"}, new String[] {"email"}, new String[] {email});
		boolean userExists = vals.size() == 1 && vals.get(0).get(0).equals(password);
		if (!userExists) {
			System.out.println("");
			System.out.println("Invalid email or password.");
			System.out.println("");
		}
		return userExists;
	}

	private boolean isHost(List<String> userInfo) {
		return userInfo.get(userInfo.size() - 2) == null;
	}

	private boolean isNotOverlap(String startDateToCheck, String endDateToCheck, String suite_num, String house_num, String latitude, String longitude, boolean modify, String modifyDate, String modifyListingNum) {
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, new String [] {"sin"}, new String [] {user.getSin()});
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		for (List<String> listing : allListings) {
			List<String> listingLocation = sqlMngr.select("location", new String[] {"suite_num", "house_num", "latitude", "longitude"}, new String[] {"listing_num"}, new String [] {listing.get(0)}).get(0);

			if ((listingLocation.get(0) == null || listingLocation.get(0).equalsIgnoreCase(suite_num)) && listingLocation.get(1).equalsIgnoreCase(house_num) && listingLocation.get(2).equalsIgnoreCase(latitude) && listingLocation.get(3).equalsIgnoreCase(longitude)) {
				List<List<String>> listingAvailabilities = sqlMngr.select("availability", new String[] {"start_date", "end_date"}, new String[] {"listing_num"}, new String [] {listing.get(0)});
				for(List<String> availability : listingAvailabilities) {
					if((modify && !modifyListingNum.equals(listing.get(0))) || (modify && modifyListingNum.equals(listing.get(0)) && !modifyDate.equals(availability.get(0))) || !modify) {
						String startDateStr = availability.get(0);
						String endDateStr = availability.get(1);
						try {
							Date startDateToCheckDate = sdf.parse(startDateToCheck);
							Date endDateToCheckDate = sdf.parse(endDateToCheck);
					        Date startDate = sdf.parse(startDateStr);
					        Date endDate = sdf.parse(endDateStr);
					        if (startDateToCheckDate.after(startDate) && startDateToCheckDate.before(endDate) || endDateToCheckDate.after(startDate) && endDateToCheckDate.before(endDate)|| startDateToCheckDate.compareTo(startDate) == 0 || endDateToCheckDate.compareTo(startDate) == 0 || startDateToCheckDate.compareTo(endDate) == 0 || endDateToCheckDate.compareTo(endDate) == 0 || startDate.before(endDateToCheckDate) && endDate.after(startDateToCheckDate)) {
					        	System.out.println("");
					        	System.out.println("You already have a listing that overlaps with this date!");
					        	System.out.println("");
					        	return false;
					        }
					    } catch (ParseException e) {
					    }
					}
				}
			}
		}
		return true;
	}
	
	private void searchListings() throws SQLException, ParseException {
		System.out.println("");
		String input = "";
		int choice = -1;
		do {
			System.out.println("=========SEARCH LISTINGS BY=========");
			System.out.println("0. Exit");
			System.out.println("1. Latitude, Longitude, Distance");
			System.out.println("2. Postal Code");
			System.out.println("3. Address");
			System.out.println("4. Home");
			System.out.println("5. Log Out");
			System.out.print("Choose one of the options [0-5]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { 
				case 0:
					break;
				case 1:
					search("1", isAdvancedSearch());
					searchListings();
					break;
				case 2:
					search("2", isAdvancedSearch());
					searchListings();
					break;
				case 3:
					search("3", isAdvancedSearch());
					searchListings();
					break;
				case 4:
					renterHome();
					break;
				case 5:
					try {
						mainMenu();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0 && input.compareTo("5") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}
	
	private boolean isAdvancedSearch() {
		System.out.println("");
		String type;
		do {
			System.out.print("Advanced Search (y/n): ");
			type = sc.nextLine().trim();
			if (!type.equalsIgnoreCase("y") && !type.equalsIgnoreCase("n")) {
				invalidEntry();
			}
		} while(!type.equalsIgnoreCase("y") && !type.equalsIgnoreCase("n"));
		if(type.equalsIgnoreCase("y")) {
			return true;
		}
		return false;
	}
	
	private void search(String search, boolean advancedSearch) {
		System.out.println("");
		String[] params;
		String[] dates = new String[2];
		dates[0] = "";
		String[] amenity_vals = new String[7];
		List<String> filtered_vals_final = new ArrayList<String>();
		List<String> filtered_columns_final = new ArrayList<String>();
		List<List<String>> filteredListings;
		if(search.equals("1")) {
			params = new String[3];
			do {
				System.out.print("Latitude: ");
				params[0] = sc.nextLine().trim();
			} while (!isValidLatitude(params[0]));
			do {
				System.out.print("Longitude: ");
				params[1] = sc.nextLine().trim();
			} while (!isValidLongitude(params[1]));
			do {
				System.out.print("Distance (Km, leaving it blank defaults to 100Km): ");
				params[2] = sc.nextLine().trim();
				if(params[2].equals("")) {
					params[2] = "100";
					break;
				}
			} while (!isDouble(params[2]));
		} else if(search.equals("3")) {
				params = new String[10];
				do {
					System.out.print("House Number: ");
					params[0] = sc.nextLine().trim();
					if(params[0].equals("")) {
						invalidEntry();
					} else {
						filtered_columns_final.add("house_num");
						filtered_vals_final.add(params[0]);
					}
				} while(params[0].equals(""));
				System.out.print("Suite Number (leave blank if not applicable): ");
				params[1] = sc.nextLine().trim();
				if (!params[1].equals("")) {
					filtered_columns_final.add("suite_num");
					filtered_vals_final.add(params[1]);
				}
				do {
					System.out.print("Street Name: ");
					params[2] = sc.nextLine().trim();
					if(params[2].equals("")) {
						invalidEntry();
					} else {
						filtered_columns_final.add("street_name");
						filtered_vals_final.add(params[2]);
					}
				} while(params[2].equals(""));
				do {
					System.out.print("City: ");
					params[3] = sc.nextLine().trim();
					if(params[3].equals("")) {
						invalidEntry();
					} else {
						filtered_columns_final.add("city");
						filtered_vals_final.add(params[3]);
					}
				} while(params[3].equals(""));
				do {
					System.out.print("Country: ");
					params[4] = sc.nextLine().trim();
					if(params[4].equals("")) {
						invalidEntry();
					} else {
						filtered_columns_final.add("country");
						filtered_vals_final.add(params[4]);
					}
				} while(params[4].equals(""));
		} else {
			params = new String[1];
			do {
				System.out.print("Postal Code: ");
				params[0] = sc.nextLine().trim();
				if(params[0].equals("")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("postal_code");
					filtered_vals_final.add(params[0]);
				}
			} while (params[0].equals(""));
		}
		if(advancedSearch) {
			System.out.println("----DATE FILTER----");
			do {
				System.out.print("Start Date (leave blank for no date filter): ");
				dates[0] = sc.nextLine().trim();
				if(dates[0].equals("")) {
					break;
				}
			} while(!isValidStartDate(dates[0]));
			if (!dates[0].equals("")) {
				do {
					System.out.print("End Date: ");
					dates[1] = sc.nextLine().trim();
				} while(!isValidEndDate(dates[0], dates[1]));
			}
			System.out.println("----AMENITIES FILTER----");
			do {
				System.out.print("Toilet Paper Included? (y/n or leave blank): ");
				amenity_vals[0] = sc.nextLine().trim();
				if(amenity_vals[0].equals("")) {
					break;
				}
				if(!amenity_vals[0].equalsIgnoreCase("y") && !amenity_vals[0].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("toilet_paper_included");
					filtered_vals_final.add(amenity_vals[0]);
				}
			} while(!amenity_vals[0].equalsIgnoreCase("y") && !amenity_vals[0].equalsIgnoreCase("n"));
			do {
				System.out.print("WiFi Included? (y/n or leave blank): ");
				amenity_vals[1] = sc.nextLine().trim();
				if(amenity_vals[1].equals("")) {
					break;
				}
				if(!amenity_vals[1].equalsIgnoreCase("y") && !amenity_vals[1].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("wifi_included");
					filtered_vals_final.add(amenity_vals[1]);
				}
			} while(!amenity_vals[1].equalsIgnoreCase("y") && !amenity_vals[1].equalsIgnoreCase("n"));
			do {
				System.out.print("Towels Included? (y/n or leave blank): ");
				amenity_vals[2] = sc.nextLine().trim();
				if(amenity_vals[2].equals("")) {
					break;
				}
				if(!amenity_vals[2].equalsIgnoreCase("y") && !amenity_vals[2].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("towels_included");
					filtered_vals_final.add(amenity_vals[2]);
				}
			} while(!amenity_vals[2].equalsIgnoreCase("y") && !amenity_vals[2].equalsIgnoreCase("n"));
			do {
				System.out.print("Iron Included? (y/n or leave blank): ");
				amenity_vals[3] = sc.nextLine().trim();
				if(amenity_vals[3].equals("")) {
					break;
				}
				if(!amenity_vals[3].equalsIgnoreCase("y") && !amenity_vals[3].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("iron_included");
					filtered_vals_final.add(amenity_vals[3]);
				}
			} while(!amenity_vals[3].equalsIgnoreCase("y") && !amenity_vals[3].equalsIgnoreCase("n"));
			do {
				System.out.print("Pool Included? (y/n or leave blank): ");
				amenity_vals[4] = sc.nextLine().trim();
				if(amenity_vals[4].equals("")) {
					break;
				}
				if(!amenity_vals[4].equalsIgnoreCase("y") && !amenity_vals[4].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("pool_included");
					filtered_vals_final.add(amenity_vals[4]);
				}
			} while(!amenity_vals[4].equalsIgnoreCase("y") && !amenity_vals[4].equalsIgnoreCase("n"));
			do {
				System.out.print("A/C Included? (y/n or leave blank): ");
				amenity_vals[5] = sc.nextLine().trim();
				if(amenity_vals[5].equals("")) {
					break;
				}
				if(!amenity_vals[5].equalsIgnoreCase("y") && !amenity_vals[5].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("ac_included");
					filtered_vals_final.add(amenity_vals[5]);
				}
			} while(!amenity_vals[5].equalsIgnoreCase("y") && !amenity_vals[5].equalsIgnoreCase("n"));
			do {
				System.out.print("Fireplace Included? (y/n or leave blank): ");
				amenity_vals[6] = sc.nextLine().trim();
				if(amenity_vals[6].equals("")) {
					break;
				}
				if(!amenity_vals[6].equalsIgnoreCase("y") && !amenity_vals[6].equalsIgnoreCase("n")) {
					invalidEntry();
				} else {
					filtered_columns_final.add("fireplace_included");
					filtered_vals_final.add(amenity_vals[6]);
				}
			} while(!amenity_vals[6].equalsIgnoreCase("y") && !amenity_vals[6].equalsIgnoreCase("n"));
		}
		String order;
		System.out.println("----SORT----");
		do {
			System.out.print("Sort by (1- Ascending, 2- Descending, leave blank for no ordering): ");
			order = sc.nextLine().trim();
			if (order.equals("")) {
				break;
			}
			if(!order.equals("1") && !order.equals("2")) {
				invalidEntry();
			}
		} while(!order.equals("1") && !order.equals("2"));
		if(dates[0].equals("")) {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date today = new Date();
			dates[0] = sdf.format(today);
			dates[1] = "";
		}
		filteredListings = sqlMngr.select("(SELECT * FROM availability NATURAL JOIN amenities NATURAL JOIN location) AS t", new String[] {"start_date", "end_date", "latitude", "longitude", "listing_num", "type", "house_num", "street_name", "city", "country", "suite_num", "cost_per_day"}, filtered_columns_final.toArray(new String[filtered_columns_final.size()]), filtered_vals_final.toArray(new String[filtered_vals_final.size()]));
		double lat_1 = 0;
		double long_1 = 0;
		double distance = 0;
		if(search.equals("1")) {
			lat_1 = Double.parseDouble(params[0]);
			long_1 = Double.parseDouble(params[1]);
			distance = Double.parseDouble(params[2]);
		}
		System.out.println("");
		System.out.println("Listings: ");
		Map<List<String>, Integer>fullyFilteredListingsCosts = new HashMap<List<String>, Integer>();
		Map<String, List<List<String>>>fullyFilteredListingsDates = new HashMap <String, List<List<String>>>();
		for(List<String> listing : filteredListings) {
			if(!fullyFilteredListingsCosts.containsKey(Arrays.asList(listing.get(4), listing.get(5), listing.get(6), listing.get(7), listing.get(8), listing.get(9), listing.get(10)))) {
				if((dates[1].equals("") && (listing.get(0).equals(dates[0]) || occursAfter(dates[0], listing.get(0)))) || (!dates[1].equals("") && (listing.get(0).equals(dates[0]) && listing.get(1).equals(dates[1])) || (listing.get(0).equals(dates[0]) && occursAfter(listing.get(1), dates[1])) || (listing.get(1).equals(dates[1]) && occursAfter(dates[0], listing.get(0))) || (occursAfter(dates[0], listing.get(0)) && occursAfter(listing.get(0), dates[1]) && occursAfter(dates[0], listing.get(1)) && occursAfter(listing.get(1), dates[1])))) {
					if(search.equals("1")) {
						double lat_2 = Double.parseDouble(listing.get(2));
						double long_2 = Double.parseDouble(listing.get(3));
						double latDiff = Math.toRadians(lat_2-lat_1);
						double longDiff = Math.toRadians(long_2-long_1);
						double a = Math.pow(Math.sin(latDiff/2), 2) +
						        Math.cos(Math.toRadians(lat_1)) * Math.cos(Math.toRadians(lat_2)) *
						        Math.pow(Math.sin(longDiff/2), 2);
						double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
						if ((6371000 * c)/1000 <= distance) {
							fullyFilteredListingsCosts.put(Arrays.asList(listing.get(4), listing.get(5), listing.get(6), listing.get(7), listing.get(8), listing.get(9), listing.get(10)), Integer.parseInt(listing.get(11)));
							List<List<String>> curr = new ArrayList<List<String>> ();
							curr.add(Arrays.asList(listing.get(0), listing.get(1)));
							fullyFilteredListingsDates.put(listing.get(4), curr);
						}
					} else {
						fullyFilteredListingsCosts.put(Arrays.asList(listing.get(4), listing.get(5), listing.get(6), listing.get(7), listing.get(8), listing.get(9), listing.get(10)), Integer.parseInt(listing.get(11)));
						List<List<String>> curr = new ArrayList<List<String>> ();
						curr.add(Arrays.asList(listing.get(0), listing.get(1)));
						fullyFilteredListingsDates.put(listing.get(4), curr);
					}
				}
			}  else {
				fullyFilteredListingsDates.get(listing.get(4)).add(Arrays.asList(listing.get(0), listing.get(1)));
			}
		}
		if(order.equals("1")) {
			fullyFilteredListingsCosts = fullyFilteredListingsCosts
			        .entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .collect(
			            Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
			                LinkedHashMap::new));
		} else if(order.equals("2")) {
			fullyFilteredListingsCosts = fullyFilteredListingsCosts
			        .entrySet()
			        .stream()
			        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
			        .collect(
			            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
			                LinkedHashMap::new));
		}
		fullyFilteredListingsCosts.entrySet().forEach(entry->{
			String listingInfo = "-> Listing #: " + entry.getKey().get(0) + "; Type: " + entry.getKey().get(1) + "; Address: " + entry.getKey().get(2) + " " + entry.getKey().get(3) + ", " + entry.getKey().get(4) + ", " + entry.getKey().get(5);
			if (entry.getKey().get(6) != null) {
				listingInfo += ", Suite No. " + entry.getKey().get(6);
			}
			listingInfo += "; Cost per Day: " + entry.getValue();
			for (List<String> listingDates : fullyFilteredListingsDates.get(entry.getKey().get(0))) {
				listingInfo += "; From " + listingDates.get(0) + " To: " + listingDates.get(1);
			}
			System.out.println(listingInfo);
		    });
	}

	private void bookings() throws ParseException, SQLException {

		int selection;
		System.out.println("Book by:");
		System.out.println("1: Date");
		System.out.println("2: Location");
		System.out.println("0: Back");
		selection = sc.nextInt();
		if(selection == 1) {
			bookByDate();
		}
		else if(selection ==2) {
			bookByPostalCode();
		}
		else if(selection == 0) {
			goBack();
		}
		else {
			System.out.println("incorrect slection, try again");
		}

	}

	private void bookByDate() throws ParseException, SQLException {


		String startDate;
		Date startInDate;
		String endDate;
		Date endInDate;
		System.out.println("Enter start date(dd/mm/yyyy)");
		startDate = sc.nextLine();

		startInDate = isValidDate(startDate);
		if(startInDate != null ) {
			System.out.println("Enter start date(dd/mm/yyyy)");
			endDate = sc.nextLine();
			endInDate = isValidDate(endDate);
			if (endInDate != null) {
			//	displayDates(startInDate, endInDate);
				startBookingByListingNumber();

			}
			else {
				System.out.println("The end date is not valid");
			}
		}
		else {
			System.out.println("The date you entered is not valid ");

		}

	}

	private void displayDates(String startDate, String endDate) {


	}

	private void bookByCity() throws ParseException, SQLException {



		String country;
		String city;
		System.out.println("Enter Country:");
		country = sc.nextLine().trim();
		System.out.println("Enter City");
		city = sc.nextLine().trim();
		//displayByCity(country, city);
		startBookingByListingNumber();

	}

	private void bookByPostalCode() throws ParseException, SQLException {
		String postalCode;
		System.out.println("Enter Postal Code:");
		sc.nextLine();
		postalCode = sc.nextLine().trim();
		if(displayByPostalCode(postalCode)) {
			startBookingByListingNumber();
		}
		
	}

	private void goBack() {

	}

	private boolean bookByListingNumber(String listingNumber, User CurrUser) throws ParseException, SQLException {
		boolean toReturn = false;
		double listingPerDayCost = 1;
		int numberOfDays = 1;
		String startDate;
		Date start;
		Date stop;
		boolean datesRight;
		String endDate;
		String[] dates = null;
		
		do {
			System.out.print("Please enter the date you want to book it from: ");
			startDate = sc.nextLine().trim();
		} while(!isValidStartDate(startDate));
		do {
			System.out.print("Please enter the date you want to end the booking at: ");
			endDate = sc.nextLine().trim() ;
		} while (!isValidEndDate(startDate, endDate));
		dates = checkIfFree(startDate, endDate, listingNumber);
		if(dates != null && dates.length > 0) {
			start = isValidDate(startDate);
			stop = isValidDate(endDate);
			//numberOfDays = ChronoUnit.DAYS.between(start, stop);
			invoice(user, numberOfDays, listingPerDayCost);

			updateAvailability(startDate, endDate, dates, listingNumber, listingPerDayCost);
			addBooking(listingNumber,startDate, endDate, listingPerDayCost );
			List<List<String>> bookingNums = sqlMngr.select("booking", new String[] {"booking_num","start_date"}, new String[] {"listing_num"}, new String[] {listingNumber});
			int i = 0;
			while(!(bookingNums.get(i).get(1).equals(startDate))) {
				i++;
			}
			String bookingNumber = bookingNums.get(i).get(0); 
			System.out.println("Your booking was made. Booking number is: " + bookingNumber);
			toReturn = true;
		}
		else {
			System.out.println("");
			System.out.println("The lisitng you chose is not free on the following dates.");
			System.out.println("");
		}
		return toReturn;
	}
	private void invoice(User currUser, int numbDays, double cost) {
		double total = cost*numbDays;
		System.out.print("Dear "+ user.getLastName() + " " + user.getFirstName() +"," + total + " has been deducted from your account");
	}
	private void startBookingByListingNumber() throws ParseException, SQLException {
		System.out.println("");
		System.out.println("=========BOOK=========");
		String listingNumber;
		do {
			System.out.print("Enter listing number to book: ");
			listingNumber = sc.nextLine();
		} while (!checkValidListingNum(listingNumber));
		if(bookByListingNumber(listingNumber, user)) {
			System.out.println("");
			System.out.println("Your booking has been made.");
			System.out.println("");
		}
		else {
			System.out.println("");
			System.out.println("This booking was not successful.");
			System.out.println("");
		}
	}
	
	private boolean checkValidListingNum(String listing_num) {
		List<List<String>> availableListings = sqlMngr.select("availability", new String[] {"listing_num"}, new String[] {"listing_num"}, new String[] {listing_num});
		boolean result = availableListings.size() > 0;
		if(!result) {
			System.out.println("");
			System.out.println("No such listing exists!");
			System.out.println("");
			return false;
		}
		return true;
	}
	
	private String[] checkIfFree(String startDate, String endDate, String listingnumber) throws ParseException {
		int i = 0;
		String currStart;
		String currStop;
		boolean exit = false;
		String[] toReturn = {};
		List<List<String>> allDates = sqlMngr.select("availability", new String[] {"start_date", "end_date"}, new String[] {"listing_num"}, new String[] {listingnumber});
		while(i < allDates.size() && !exit) {
				currStart = allDates.get(i).get(0);
				currStop = allDates.get(i).get(1);
				exit = (currStart.equals(startDate) || occursAfter(currStart, startDate)) && (endDate.equals(currStop) || occursAfter(endDate,currStop));
				i++;
			}
		i--;
		if(exit) {
			String[] temp = new String[] {allDates.get(i).get(0), allDates.get(i).get(1)};
			toReturn = temp;
			
		}
		
		return toReturn;

	}
	private void updateAvailability(String startDate,String endDate, String[] dates, String listingNumber, double cost) throws SQLException {
		if(startDate.equals(dates[0]) && endDate.equals(dates[1])) {
			sqlMngr.deleteRow("availability", new String[] { "listing_num","start_date"}, new String[] {listingNumber, dates[0]});
			
			
		}
		else if(startDate.equals(dates[0])) {
			sqlMngr.update("availability",new String[] { "listing_num", "start_date", "end_date"},new String[] {listingNumber,dates[0],dates[1]}, new String[] {"start_date"},new String[] {endDate} );
		}
		else if(endDate.equals(dates[1])) {
			sqlMngr.update("availability",new String[] { "listing_num", "start_date", "end_date"},new String[] {listingNumber, dates[0], dates[1]}, new String[] {"end_date"}, new String[] {dates[0]});
		}
		else {
		
			sqlMngr.update("availability",new String[] { "listing_num", "start_date", "end_date"},new String[] {listingNumber, dates[0], dates[1]}, new String[] {"end_date"}, new String[] {startDate});
			sqlMngr.insert("availability", availabilityColumns , new String[] {listingNumber,endDate,dates[1],String.valueOf(cost)});
		}
		}
	private boolean displayByPostalCode(String postal) {
		List<List<String>> listingCode = sqlMngr.select("location", locationColumns, new String[] {"postal_code"}, new String[] {postal});
		boolean toReturn = false;
		if(listingCode.size() > 0) {
			toReturn = true;
		}
		for(int i = 0; i < listingCode.size();i++) {
			List<List<String>> dates = sqlMngr.select("availability", new String[] {"start_date", "end_date"} , new String[] {"listing_num"}, new String[] {(listingCode.get(i)).get(0)});
			for(int j = 0; j < dates.size();j++) {
				for(int k = 0; k < listingCode.get(i).size();k++) {
					System.out.print(listingCode.get(i).get(k) + "\t");
				}
				System.out.println(dates.get(j).get(0) + "\t" + dates.get(j).get(1));
				
			}
		}
		if(!toReturn) {
			System.out.println("Postal Code does not Exist !");
		}
		return toReturn;
	}
	public void addBooking(String listing, String startDate, String endDate, double cost)  {
		sqlMngr.insert("booking", bookingColumns,new String[] {UUID.randomUUID().toString(), listing, startDate, endDate, String.valueOf(cost), user.getSin(),null,null,null,null,null,null});
	}


	private void feedBackByRenter(String bookingNum) {
		int selection;
		List<List<String>>sinCheck;
		sinCheck = sqlMngr.select("booking", new String[] {"sin"}, new String[] {"booking_num"}, new String[] {bookingNum});
		if(sinCheck.size() > 0 && sinCheck.get(0).get(0).equals(user.getSin())) {
			System.out.println("1. Comment On Listing");
			System.out.println("2. Rate Listing");
			System.out.println("3. Comment On the Host");
			System.out.println("4. Rate Host");
		
			selection = sc.nextInt();
			if(selection == 1 ) {
				commentOnListing(bookingNum);
			}
			else if(selection == 2 ) {
				rateListing(bookingNum);
			}
			else if(selection == 3 ) {
				commentOnHost(bookingNum);
			}
			else if(selection == 4 ) {
				rateHost(bookingNum);
			}
		}
		else {
			System.out.println("Sorry youare not associate with that booking, Try again");
		}
		
	}
	private void feedBackByHost(String bookingNum) {
		int selection;
		System.out.println("1. Comment On renter");
		System.out.println("2. Rate renter");
	
		selection = sc.nextInt();
		if(selection == 1 ) {
			commentOnRenter(bookingNum);
		}
		else if(selection == 2 ) {
			rateRenter(bookingNum);
		}
		
		
	}
	
	private void commentOnListing(String bookingNum) {
		String comment;
		System.out.println("Please enter your comment for the listing");
		sc.nextLine();
		comment = sc.nextLine();
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"renter_comment_on_listing"} , new String[] {comment});
	}
	private void commentOnHost(String bookingNum) {
		String comment;
		System.out.println("Please enter your comment for the Host:");
		sc.nextLine().trim();
		comment = sc.nextLine().trim();
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"renter_comment_on_host"} , new String[] {comment});
		System.out.println("Your comment was added. Thank you for your feedback");
	}
	private void commentOnRenter(String bookingNum) {
		String comment;
		System.out.println("Please enter your comment for the Renter");
		sc.nextLine().trim();
		comment = sc.nextLine().trim();
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"host_comment_on_renter"} , new String[] {comment});
	}
	private void rateListing(String bookingNum) {
		int comment;
		System.out.println("Please enter your rating for the listing(0-5)");
		do
		{
			comment = sc.nextInt();
			if(comment > 5) {
				System.out.println("Rating should be between 0-5");
			}
		}while(comment > 5);
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"listing_rating"} , new String[] {String.valueOf(comment)});
	}
	private void rateHost(String bookingNum) {
		int comment;
		System.out.println("Please enter your rating for the Host(0-5)");
		do
		{
			comment = sc.nextInt();
			if(comment > 5) {
				System.out.println("Rating should be between 0-5");
			}
		}while(comment > 5);
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"renter_rating"} , new String[] {String.valueOf(comment)});
	}
	
	private void rateRenter(String bookingNum) {
		int comment;
		System.out.println("Please enter your rating for the Host(0-5)");
		do
		{
			comment = sc.nextInt();
			if(comment > 5) {
				System.out.println("Rating should be between 0-5");
			}
		}while(comment > 5);
		
		sqlMngr.update("booking", new String[] {"booking_num"}, new String[] {bookingNum}, new String[] {"host_rating"} , new String[] {String.valueOf(comment)});
	}

	private void feedbackForRenter() {
		String bookingNumber;
		System.out.println("Please enter booking number to comment on");
		bookingNumber = sc.nextLine();
		feedBackByHost(bookingNumber);
		
		
	}

	private void feedbackForHost() {
		String bookingNumber;
		System.out.println("Please enter booking number to comment on");
		bookingNumber = sc.nextLine();
		feedBackByRenter(bookingNumber);
		
		
	}
	
	private void generateReports() throws SQLException, ParseException, IOException {
		String input = "";
		int choice = -1;
		do {
			System.out.println("");
			System.out.println("=========GENERATE REPORTS=========");
			System.out.println("0. Exit");
			System.out.println("1. Total number of bookings in date range by city");
			System.out.println("2. Total number of bookings by zip code within city");
			System.out.println("3. Total number of listings by country");
			System.out.println("4. Total number of listings by city within country");
			System.out.println("5. Total number of listings by zip code within city within country");
			System.out.println("6. Rank hosts by total number of listings within a country");
			System.out.println("7. Rank hosts by total number of listings within a city within a country");
			System.out.println("8. Commercial hosts");
			System.out.println("9. Rank renters by number of bookings in date range");
			System.out.println("10. Rank renters by number of bookings in date range in a city (at least 2 bookings)");
			System.out.println("11. Top 10 hosts and renters with largest number of cancellations within a year");
			System.out.println("12. Noun phrases");
			System.out.println("13. Main Menu");
			System.out.print("Choose one of the options [0-13]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) {
				case 0:
					break;
				case 1:
					report1Display();
					break;
				case 2:
					report2Display();
					break;
				case 3:
					report3Display();
					break;
				case 4:
					report4Display();
					break;
				case 5:
					report5Display();
					break;
				case 6:
					report6Display();
					break;
				case 7:
					report7Display();
					break;
				case 8:
					report8Display();
					break;
				case 9:
					report9Display();
					break;
				case 10:
					report10Display();
					break;
				case 11:
					report11Display();
					break;
				case 12:
					report12Display();
					break;
				case 13:
					mainMenu();
					break;
				default:
					invalidEntry();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
				invalidEntry();
			}
		} while (input.compareTo("0") != 0 && input.compareTo("13") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void report1Display() throws SQLException {
		System.out.println("");
		String[] dates = new String[2];
		do {
			System.out.print("From (dd/mm/yyy): ");
			dates[0] = sc.nextLine().trim();
		} while (isValidDate(dates[0]) == null);
		do {
			System.out.print("To (dd/mm/yyy): ");
			dates[1] = sc.nextLine().trim();
		} while (!isValidEndDate(dates[0], dates[1]));
		List<List<String>> bookingsWithLocations = sqlMngr.report1();
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for(List<String> booking : bookingsWithLocations) {
			if((booking.get(0).equals(dates[0]) && booking.get(1).equals(dates[1])) || (booking.get(0).equals(dates[0]) && occursAfter(booking.get(1), dates[1])) || (booking.get(1).equals(dates[1]) && occursAfter(dates[0], booking.get(0))) || (occursAfter(dates[0], booking.get(0)) && occursAfter(booking.get(0), dates[1]) && occursAfter(dates[0], booking.get(1)) && occursAfter(booking.get(1), dates[1]))) {
				if (counts.containsKey(booking.get(2))) {
					counts.put(booking.get(2), counts.get(booking.get(2)) + 1);
				} else {
					counts.put(booking.get(2), 1);
				}
			}
		}
		System.out.println("");
		System.out.println("=========REPORT=========");
		System.out.println("There are a total of:");
		counts.entrySet().forEach(entry->{
			    System.out.println(entry.getValue() + " bookings made in the city of " + entry.getKey() + ".");  
			 });
	}

	private void report2Display() throws SQLException {
		System.out.println("");
		String city;
		do {
			System.out.print("City: ");
			city = sc.nextLine().trim();
			if(city.equals("")) {
				invalidEntry();
			}
		} while (city.equals(""));
		Map<String, String> counts = sqlMngr.report2(city);
		System.out.println("");
		System.out.println("=========REPORT=========");
		System.out.println("In the city of " + city + " there are a total of:");
		counts.entrySet().forEach(entry->{
			    System.out.println(entry.getValue() + " bookings made in the area with zip code " + entry.getKey() + ".");  
			 });
	}
	
	private void report3Display() throws SQLException {
		Map<String, String> counts = sqlMngr.report3();
		System.out.println("");
		System.out.println("=========REPORT=========");
		System.out.println("There are a total of:");
		counts.entrySet().forEach(entry->{
			    System.out.println(entry.getValue() + " listings in the country of " + entry.getKey() + ".");  
			 });
	}
	
	private void report4Display() throws SQLException {
		Map<String, Map<String,String>> counts = sqlMngr.report4();
		System.out.println("");
		System.out.println("=========REPORT=========");
		counts.entrySet().forEach(entry->{
			System.out.println("In the country of " + entry.getKey() + " there are a total of:");
			entry.getValue().entrySet().forEach(subEntry->{
			    System.out.println("\t-> " + subEntry.getValue() + " listings in the city of " + subEntry.getKey() + ".");  
			 });});
	}

	private void report5Display() throws SQLException {
		Map<String, Map<String, Map<String,String>>> counts = sqlMngr.report5();
		System.out.println("");
		System.out.println("=========REPORT=========");
		counts.entrySet().forEach(entry->{
			System.out.println("In the country of " + entry.getKey() + ":");
			entry.getValue().entrySet().forEach(subEntry->{
			    System.out.println("\t-> In the city of " + subEntry.getKey() + " there are a total of:");
			    subEntry.getValue().entrySet().forEach(subSubEntry->{
				    System.out.println("\t\t* " + subSubEntry.getValue() + " listings in the area with zip code " + subSubEntry.getKey() + ".");
				 });
			 });});
	}
	
	private void report6Display() throws SQLException {	
		String order;
		System.out.println("");
		do {
			System.out.print("Order (1- Most listings, 2- Least listings): ");
			order = sc.nextLine().trim();
			if(order.equals("") || (!order.equals("1") && !order.equals("2"))) {
				invalidEntry();
			}
		} while (order.equals("") || (!order.equals("1") && !order.equals("2")));
		Map<String, List<List<String>>> counts;
		System.out.println("");
		System.out.println("=========REPORT=========");
		if(order.equals("1")) {
			counts = sqlMngr.report6(true);
			counts.entrySet().forEach(entry->{
				    System.out.println("The hosts with the most listings in the country of " + entry.getKey() + " are:");
				    for(List<String> host : entry.getValue()) {
				    	System.out.println("\t-> " + host.get(0) + " " + host.get(1) + " with " + host.get(2) + " listings.");
				    }
				 });
		} else {
			counts = sqlMngr.report6(false);
			counts.entrySet().forEach(entry->{
			    System.out.println("The hosts with the least listings in the country of " + entry.getKey() + " are:");
			    for(List<String> host : entry.getValue()) {
			    	System.out.println("\t-> " + host.get(0) + " " + host.get(1) + " with " + host.get(2) + " listings.");
			    }
			 });
		}
	}

	private void report7Display() throws SQLException {	
		String order;
		System.out.println("");
		do {
			System.out.print("Order (1- Most listings, 2- Least listings): ");
			order = sc.nextLine().trim();
			if(order.equals("") || (!order.equals("1") && !order.equals("2"))) {
				invalidEntry();
			}
		} while (order.equals("") || (!order.equals("1") && !order.equals("2")));
		Map<String, Map<String, List<List<String>>>> counts;
		System.out.println("");
		System.out.println("=========REPORT=========");
		if(order.equals("1")) {
			counts = sqlMngr.report7(true);
			counts.entrySet().forEach(entry->{
				    System.out.println("In the country of " + entry.getKey() + " the hosts with the most listings:");
				    entry.getValue().entrySet().forEach(subEntry->{
					    System.out.println("\t-> In the city of " + subEntry.getKey() + " are:");  
					    for(List<String> host : subEntry.getValue()) {
					    	System.out.println("\t\t* " + host.get(0) + " " + host.get(1) + " with " + host.get(2) + " listings.");
					    }
					 });});
		} else {
			counts = sqlMngr.report7(false);
			counts.entrySet().forEach(entry->{
			    System.out.println("In the country of " + entry.getKey() + " the hosts with the least listings:");
			    entry.getValue().entrySet().forEach(subEntry->{
				    System.out.println("\t-> In the city of " + subEntry.getKey() + " are:");  
				    for(List<String> host : subEntry.getValue()) {
				    	System.out.println("\t\t* " + host.get(0) + " " + host.get(1) + " with " + host.get(2) + " listings.");
				    }
				 });});
		}
	}
	private void report8Display() throws SQLException {
		System.out.println("1. Run report for Cities");
		System.out.println("2. Run report for Countries");
		int input = sc.nextInt();
		
		if(input == 1) {
			commercialReportByCity();
		}
		else if(input == 2){
			commercialReportByCountry();
		}
		
		
		
	}
	private void commercialReportByCity() throws SQLException {
		List<List<String>> cities;
		List<List<String>> listingNumbs;
		int numList = 0;
		List <String> exceedingUsers = new ArrayList<String>();
		List <String> atLoc = new ArrayList<String>();
		String sin;
		String[] empty = {};
		Map<String, Integer> map ;
		cities = sqlMngr.SelectDistinct("location", new String[] {"city"},empty,empty);
	
		for(int i =0; i< cities.size() ;i++) {
			map = new HashMap<String, Integer>();
			
			listingNumbs = sqlMngr.select("location", new String[] {"listing_num"}, new String[] {"city"}, new String[] {cities.get(i).get(0)});
			
			numList = listingNumbs.size();
			for(int j = 0; j < numList; j++) {
				sin = sqlMngr.select("host", new String[] {"sin"}, new String[] {"listing_num"}, new String[] {listingNumbs.get(j).get(0)}).get(0).get(0);
				if(map.containsKey(sin)) {
					map.put(sin, map.get(sin)+ 1);
				}
				else {
					map.put(sin, 1);
				}
				if((double)(map.get(sin)) > (double)numList/10) {
					exceedingUsers.add(sin);
					atLoc.add(cities.get(i).get(0));
					map.put(sin, -numList);
					
				
				}
			}
		}
		if(exceedingUsers.size() > 0) {
			
		System.out.println("Users to be flagged have the sin :");
			for(int i = 0; i < exceedingUsers.size(); i++) {
				System.out.print(exceedingUsers.get(i));
				System.out.println(" to be flagged in " + atLoc.get(i));
				
			}
		}

	}
	
	private void commercialReportByCountry() throws SQLException {
		List<List<String>> countries;
		List<List<String>> listingNumbs;
		int numList = 0;
		List <String> exceedingUsers = new ArrayList<String>();
		List <String> atLoc = new ArrayList<String>();
		String sin;
		String[] empty = {};
		Map<String, Integer> map ;
		countries = sqlMngr.SelectDistinct("location", new String[] {"country"},empty,empty);
	
		for(int i =0; i< countries.size() ;i++) {
			map = new HashMap<String, Integer>();
			
			listingNumbs = sqlMngr.select("location", new String[] {"listing_num"}, new String[] {"country"}, new String[] {countries.get(i).get(0)});
			
			numList = listingNumbs.size();
			for(int j = 0; j < numList; j++) {
				sin = sqlMngr.select("host", new String[] {"sin"}, new String[] {"listing_num"}, new String[] {listingNumbs.get(j).get(0)}).get(0).get(0);
				if(map.containsKey(sin)) {
					map.put(sin, map.get(sin)+ 1);
				}
				else {
					map.put(sin, 1);
				}
				if((double)(map.get(sin)) > (double)numList/10) {
					exceedingUsers.add(sin);
					atLoc.add(countries.get(i).get(0));
					map.put(sin, -numList);
					
				
				}
			}
		}
		if(exceedingUsers.size() > 0) {
			
		System.out.println("Users to be flagged have the sin :");
			for(int i = 0; i < exceedingUsers.size(); i++) {
				System.out.print(exceedingUsers.get(i));
				System.out.println(" to be flagged in " + atLoc.get(i));
				
			}
		}

	}
	
	private void report9Display() throws SQLException {
		String order;
		System.out.println("");
		String[] dates = new String[2];
		do {
			System.out.print("From (dd/mm/yyy): ");
			dates[0] = sc.nextLine().trim();
		} while (isValidDate(dates[0]) == null);
		do {
			System.out.print("To (dd/mm/yyy): ");
			dates[1] = sc.nextLine().trim();
		} while (!isValidEndDate(dates[0], dates[1]));
		do {
			System.out.print("Order (1- Most bookings, 2- Least bookings): ");
			order = sc.nextLine().trim();
			if(order.equals("") || (!order.equals("1") && !order.equals("2"))) {
				invalidEntry();
			}
		} while (order.equals("") || (!order.equals("1") && !order.equals("2")));
		Map<List<String>, List<List<String>>> renterInfos = sqlMngr.report9();
		Map<List<String>, Integer> counts = new HashMap<List<String>, Integer> ();
		renterInfos.entrySet().forEach(entry->{
			for(List<String> info : entry.getValue()) {
				if((info.get(0).equals(dates[0]) && info.get(1).equals(dates[1])) || (info.get(0).equals(dates[0]) && occursAfter(info.get(1), dates[1])) || (info.get(1).equals(dates[1]) && occursAfter(dates[0], info.get(0))) || (occursAfter(dates[0], info.get(0)) && occursAfter(info.get(0), dates[1]) && occursAfter(dates[0], info.get(1)) && occursAfter(info.get(1), dates[1]))) {
					if (counts.containsKey(entry.getKey())) {
						counts.put(entry.getKey(), counts.get(entry.getKey()) + 1);
					} else {
						counts.put(entry.getKey(), 1);
					}
				}
			}		 });
		Map<List<String>, Integer> sorted;
		System.out.println("");
		System.out.println("=========REPORT=========");
		if(order.equals("1")) {
			sorted = counts
			        .entrySet()
			        .stream()
			        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
			        .collect(
			            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
			                LinkedHashMap::new));
			System.out.println("The renters with the most bookings in the date range of " + dates[0] + " to " + dates[1] + " are:");
		} else {
			sorted = counts
			        .entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .collect(
			            Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
			                LinkedHashMap::new));
			System.out.println("The renters with the least bookings in the date range of " + dates[0] + " to " + dates[1] + " are:");
		}
		sorted.entrySet().forEach(entry->{
			    System.out.println("\t-> " + entry.getKey().get(1) + " " + entry.getKey().get(2) + " with " + entry.getValue() + " bookings.");  
			 });
	}
	
	private void report10Display() throws SQLException {
		String order;
		System.out.println("");
		String[] dates = new String[2];
		Map<List<String>, List<List<String>>> renterInfos = new HashMap <List<String>, List<List<String>>>();
		
		do {
			System.out.print("From (dd/mm/yyy): ");
			dates[0] = sc.nextLine().trim();
		} while (isValidDate(dates[0]) == null);
		do {
			System.out.print("To (dd/mm/yyy): ");
			dates[1] = sc.nextLine().trim();
		} while (!isValidEndDate(dates[0], dates[1]));
		do {
			System.out.print("Order (1- Most bookings, 2- Least bookings): ");
			order = sc.nextLine().trim();
			if(order.equals("") || (!order.equals("1") && !order.equals("2"))) {
				invalidEntry();
			}
		} while (order.equals("") || (!order.equals("1") && !order.equals("2")));
		String[] empty = {};
		Map<String, Integer> map ;
		List<List<String>> cities;
		System.out.println("=========REPORT=========");
		cities = sqlMngr.SelectDistinct("location", new String[] {"city"},empty,empty);
		for(int i = 0; i < cities.size();i++) {
			renterInfos = sqlMngr.report10(cities.get(i).get(0));
			Map<List<String>, Integer> counts = new HashMap<List<String>, Integer> ();
			renterInfos.entrySet().forEach(entry->{
				for(List<String> info : entry.getValue()) {
					if((info.get(0).equals(dates[0]) && info.get(1).equals(dates[1])) || (info.get(0).equals(dates[0]) && occursAfter(info.get(1), dates[1])) || (info.get(1).equals(dates[1]) && occursAfter(dates[0], info.get(0))) || (occursAfter(dates[0], info.get(0)) && occursAfter(info.get(0), dates[1]) && occursAfter(dates[0], info.get(1)) && occursAfter(info.get(1), dates[1]))) {
						if (counts.containsKey(entry.getKey())) {
							counts.put(entry.getKey(), counts.get(entry.getKey()) + 1);
						} else {
							counts.put(entry.getKey(), 1);
						}
					}
				}		 });
			Map<List<String>, Integer> sorted;
			System.out.println("");
			
			if(order.equals("1")) {
				sorted = counts
						.entrySet()
						.stream()
						.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
						.collect(
								Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
										LinkedHashMap::new));
				System.out.println("The renters with the most bookings in the date range of " + dates[0] + " to " + dates[1] + " in " + cities.get(i).get(0) + " are:");
			} else {
				sorted = counts
						.entrySet()
						.stream()
						.sorted(Map.Entry.comparingByValue())
						.collect(
								Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
										LinkedHashMap::new));
				System.out.println("The renters with the least bookings in the date range of " + dates[0] + " to " + dates[1] + " in " + cities.get(i).get(0) + " are:");
			}
			sorted.entrySet().forEach(entry->{
			    System.out.println("\t-> " + entry.getKey().get(1) + " " + entry.getKey().get(2) + " with " + entry.getValue() + " bookings.");  
			});
		}
	}
	private void report11Display() {
		int hosts = 0;
		String hostString ="";
		String renterString = "";
		int renters = 0;
		int counter = 0;
		
		
		List<List<String>> values = sqlMngr.report11();
		while((hosts < 10 || renters < 10)  && counter < values.size()) {
			List<List<String>> temp = sqlMngr.select("host", new String[] {"sin"},new String[] {"sin"} , new String[] {values.get(counter).get(0)});
			if(temp.size() > 0) {
				if(hosts < 10) {
					hosts++;
					hostString +=  hosts + "."+ values.get(counter).get(2) + " " + values.get(counter).get(3) + " " + values.get(counter).get(0) + " with " + values.get(counter).get(1) + " cancellations ! \n";
				}
			}
			else {
				if(renters < 10) {
					renters++;
					renterString +=  renters + "."+ values.get(counter).get(2) + " " + values.get(counter).get(3) + " " + values.get(counter).get(0) + " with " + values.get(counter).get(1) + "cancellations ! \n";
				}
			}
			counter++;
		}
		System.out.println("Top 10 cancellation hosts are : \n" + hostString);
		System.out.println("Top 10 cancellation renters are : \n" + renterString);
	}
	private void report12Display() throws SQLException, IOException {
		String[] empty = {};
		List <String> top = new ArrayList<String>();
		Map<String, List<String>> counts = new HashMap<String, List<String>> ();
		List<List<String>> listings = sqlMngr.SelectDistinct("booking",new String[] {"listing_num"} , empty, empty);
		for(int i =0; i < listings.size(); i++) {
			List<List<String>> commentChart = sqlMngr.select("booking", new String[] {"renter_comment_on_listing"}, new String[] {"listing_num"}, new String[] {listings.get(i).get(0)});
			String comment ="";
			for(int j = 0; j< commentChart.size(); j++) {
				comment += " " + commentChart.get(j).get(0);
			}
			top = getNouns(comment);
			System.out.println("top nouns in " + listings.get(i).get(0) + " are :");
			for(int k = 0;k < 5 && k < top.size() ; k++) {
				
				System.out.println((k+1)+"."+top.get(k));
			}
		}
	}
	private List<String> getNouns(String comment) throws IOException {
		Set<String> nouns = new HashSet<>();
		InputStream modelInParse = null;
		List <String> toReturn = new ArrayList<String>();
		modelInParse = new FileInputStream("from http://opennlp.sourceforge.net/models-1.5/en-parser-chunking.bin"); //from http://opennlp.sourceforge.net/models-1.5/
		ParserModel model = new ParserModel(modelInParse);
		
		//create parse tree
		Parser parser = ParserFactory.create(model);
		Parse topParses[] = ParserTool.parseLine(comment, parser, 1);
		
		//call subroutine to extract noun phrases
		for (Parse p : topParses)
			getNounPhrases(nouns,p);
		
		//print noun phrases
		for (String s : nouns) {
			toReturn.add(s);
		}
		    return toReturn;
	}
	private void getNounPhrases(Set<String> nouns, Parse p) {
		
	    if (p.getType().equals("NP")) { //NP=noun phrase
	         nouns.add(p.getCoveredText());
	    }
	    for (Parse child : p.getChildren())
	         getNounPhrases(nouns,child);
	}
	private boolean occursAfter(String earlier, String later) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    sdf.setLenient(false);
	    Date earlierDate;
	    Date laterDate;
		try {
	        earlierDate = sdf.parse(earlier);
	        laterDate = sdf.parse(later);
	    } catch (ParseException e) {
	        return false;
	    }
        return laterDate.after(earlierDate);
	}

}
