package sql;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import users.*;

public class CommandLine {

    // 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
    // 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	private static final String[] userColumns = new String [] {"email", "first_name", "last_name", "dob", "address", "occupation", "sin", "password", "cc"};
	private static final String[] locationColumns = new String [] {"listing_num", "suite_num","house_num", "street_name","postal_code", "city", "country", "latitude", "longitude", "type"};
	private static final String[] availabilityColumns = new String [] {"listing_num", "start_date", "end_date", "cost_per_day"};
	private static final String[] hostColumns = new String [] {"listing_num", "sin"};
	private static final String[] bookingColumns = new String[] {"listing_num", "startDate", "endDate", "cost_per_day","sin", "renter_comment_on_listing","renter_comment_on_host","host_comment_on_renter","listing_rating","host_rating", "renter_rating"};
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
	public void execute() throws SQLException {
		if (sc != null && sqlMngr != null) {
			System.out.println("");
			System.out.println("****************************");
			System.out.println("***CONNECTION ESTABLISHED***");
			System.out.println("****************************");
			mainMenu();

		} else {
			System.out.println("");
			System.out.println("Connection could not been established!");
			System.out.println("");
		}
	}

	private void mainMenu() throws SQLException {
		user = null;
		String input = "";
		int choice = -1;
		do {
			System.out.println("");
			System.out.println("=========LOGIN/SIGN-UP=========");
			System.out.println("0. Exit");
			System.out.println("1. Login");
			System.out.println("2. Sign-Up");
			System.out.println("3. Delete User");
			System.out.print("Choose one of the options [0-3]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { //Activate the desired functionality
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
				default:
					invalidOption();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void login() throws SQLException {
		String[] cred = new String[2];
		do {
			System.out.println("");
			System.out.println("=========LOGIN=========");
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
			System.out.print("Password: ");
			cred[1] = sc.nextLine();
		} while (!checkLoginCredentials(cred[0], cred[1]));
		List<String> userInfo = sqlMngr.getUserInfo(cred[0]);
		if (isHost(userInfo)) {
			user = new Host(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7));
			System.out.println("");
			System.out.println("Welcome " + user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase() + "!");
			hostHome();
		} else {
			user = new Renter(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7), userInfo.get(8));
			System.out.println("");
			System.out.println("Welcome " + user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase() + "!");
			renterHome();
		}
	}

	private void hostHome() throws SQLException {
		String input = "";
		int choice = -1;
		do {
			System.out.println("");
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
				switch (choice) { //Activate the desired functionality
				case 0:
					break;
				case 1:
					createListingForm();
					break;
				case 2:
					break;
				case 3:
					deleteListing();
					break;
				case 4:
					cancelBooking(true);
					break;
				case 5:
					break;
				case 6:
					mainMenu();
					break;
				default:
					invalidOption();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0 && input.compareTo("5") != 0 && input.compareTo("6") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void renterHome() throws SQLException {
		String input = "";
		int choice = -1;
		do {
			System.out.println("");
			System.out.println("=========HOME=========");
			System.out.println("0. Exit");
			System.out.println("1. Make Booking");
			System.out.println("2. Cancel Booking");
			System.out.println("3. Comment/Rate Listing & Host");
			System.out.println("4. Log Out");
			System.out.print("Choose one of the options [0-4]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { //Activate the desired functionality
				case 0:
					break;
				case 1:
					break;
				case 2:
					cancelBooking(false);
					break;
				case 3:
					break;
				case 4:
					mainMenu();
					break;
				default:
					invalidOption();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0 && input.compareTo("3") != 0 && input.compareTo("4") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void createListingForm() throws SQLException {
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
		} while(!location_vals[9].equals("1") && !location_vals[9].equals("2") && !location_vals[9].equals("3"));
		location_vals[9] = locationTypeOptionMap.get(location_vals[9]);
		do {
			System.out.print("House Number: ");
			location_vals[2] = sc.nextLine().trim();
		} while(location_vals[2].equals(""));
		if(location_vals[9].equals("2")) {
			location_vals[1] = null;
		} else {
			do {
				System.out.print("Suite Number: ");
				location_vals[1] = sc.nextLine().trim();
			} while(location_vals[1].equals("") && location_vals[1].equals("1"));
			if (location_vals[1].equals("")) {
				location_vals[1] = null;
			}
		}
		do {
			System.out.print("Street Name: ");
			location_vals[3] = sc.nextLine().trim();
		} while(location_vals[3].equals(""));
		do {
			System.out.print("Postal Code: ");
			location_vals[4] = sc.nextLine().trim();
		} while(location_vals[4].equals(""));
		do {
			System.out.print("City: ");
			location_vals[5] = sc.nextLine().trim();
		} while(location_vals[5].equals(""));
		do {
			System.out.print("Country: ");
			location_vals[6] = sc.nextLine().trim();
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
		} while(!isValidStartDate(availability_vals[1]) || !isNotOverlap(availability_vals[1], availability_vals[1], location_vals[1], location_vals[2], location_vals[7], location_vals[8]));
		do {
			System.out.print("Availability End Date (dd/mm/yyyy): ");
			availability_vals[2] = sc.nextLine().trim();
		} while(!isValidEndDate(availability_vals[1], availability_vals[2]) || !isNotOverlap(availability_vals[1], availability_vals[2], location_vals[1], location_vals[2], location_vals[7], location_vals[8]));
		do {
			System.out.print("Toilet Paper Included? (y/n): ");
			amenity_vals[1] = sc.nextLine().trim();
		} while(!amenity_vals[1].equalsIgnoreCase("y") && !amenity_vals[1].equalsIgnoreCase("n"));
		do {
			System.out.print("WiFi Included? (y/n): ");
			amenity_vals[2] = sc.nextLine().trim();
		} while(!amenity_vals[2].equalsIgnoreCase("y") && !amenity_vals[2].equalsIgnoreCase("n"));
		do {
			System.out.print("Towels Included? (y/n): ");
			amenity_vals[3] = sc.nextLine().trim();
		} while(!amenity_vals[3].equalsIgnoreCase("y") && !amenity_vals[3].equalsIgnoreCase("n"));
		do {
			System.out.print("Iron Included? (y/n): ");
			amenity_vals[4] = sc.nextLine().trim();
		} while(!amenity_vals[4].equalsIgnoreCase("y") && !amenity_vals[4].equalsIgnoreCase("n"));
		do {
			System.out.print("Pool Included? (y/n): ");
			amenity_vals[5] = sc.nextLine().trim();
		} while(!amenity_vals[5].equalsIgnoreCase("y") && !amenity_vals[5].equalsIgnoreCase("n"));
		do {
			System.out.print("A/C Included? (y/n): ");
			amenity_vals[6] = sc.nextLine().trim();
		} while(!amenity_vals[6].equalsIgnoreCase("y") && !amenity_vals[6].equalsIgnoreCase("n"));
		do {
			System.out.print("Fireplace Included? (y/n): ");
			amenity_vals[7] = sc.nextLine().trim();
		} while(!amenity_vals[7].equalsIgnoreCase("y") && !amenity_vals[7].equalsIgnoreCase("n"));
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

	private void deleteListing() throws SQLException {
		System.out.println("");
		System.out.println("=========DELETE LISTING=========");
		String listing_num;
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, "sin", new String [] {user.getSin()});
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
			if (listing.get(0).equals(listing_num)) {
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
		List<String> availInfo = sqlMngr.select("booking", new String[] {"listing_num", "start_date", "end_date", "cost_per_day"}, "booking_num", new String [] {booking_num}).get(0);
		sqlMngr.delete("booking_num", booking_num);
		sqlMngr.insert("availability", availabilityColumns, new String[] {availInfo.get(0), availInfo.get(1), availInfo.get(2), availInfo.get(3)});
	}
	
	private boolean isValidBookingNumForRenter(String booking_num) {
		List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num"}, "sin", new String [] {user.getSin()});
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
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, "sin", new String [] {user.getSin()});
		for(List<String> listing : allListings) {
			List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num"}, "listing_num", new String [] {listing.get(0)});
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

	private void signUpMenu() throws SQLException {
		String input = "";
		int choice = -1;
		do {
			System.out.println("");
			System.out.println("=========SIGN UP=========");
			System.out.println("0. Exit");
			System.out.println("1. Host");
			System.out.println("2. Renter");
			System.out.print("Choose to sign up either as a host or a renter [0-2]: ");
			input = sc.nextLine();
			try {
				choice = Integer.parseInt(input);
				switch (choice) { //Activate the desired functionality
				case 0:
					break;
				case 1:
					signUpForm(false);
					break;
				case 2:
					signUpForm(true);
					break;
				default:
					invalidOption();
					break;
				}
			} catch (NumberFormatException e) {
				input = "-1";
			}
		} while (input.compareTo("0") != 0 && input.compareTo("1") != 0 && input.compareTo("2") != 0);
		if (input.compareTo("0") == 0) {
			endSession();
		}
	}

	private void signUpForm(boolean renterSignUp) throws SQLException {
		System.out.println("");
		System.out.println("=========SIGN UP FORM=========");
		String[] cred = new String[9];
		do {
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
		} while (checkExistingAccount("user", "email", cred[0], false) || cred[0].equals(""));
		do {
			System.out.print("First Name: ");
			cred[1] = sc.nextLine().trim();
		} while (cred[1].equals(""));
		do {
			System.out.print("Last Name: ");
			cred[2] = sc.nextLine().trim();
		} while (cred[2].equals(""));
		do {
			System.out.print("DOB (dd/mm/yyyy): ");
			cred[3] = sc.nextLine();
		} while(!isAdult(cred[3]));
		do {
			System.out.print("Address: ");
			cred[4] = sc.nextLine().trim();
		} while (cred[4].equals(""));
		do {
			System.out.print("Occupation: ");
			cred[5] = sc.nextLine().trim();
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
		sqlMngr.insert("user", userColumns, cred);
		System.out.println("");
		System.out.println("You have successfully signed up!");
		mainMenu();
	}

	private void deleteUser() throws SQLException {
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
			List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, "sin", new String [] {userInfo.get(6)});
			for(List<String> listing : allListings) {
				sqlMngr.delete("listing_num", listing.get(0));
			}
		} else {
			List<List<String>> allBookings = sqlMngr.select("booking", new String[] {"booking_num", "listing_num", "start_date", "end_date", "cost_per_day"}, "sin", new String [] {userInfo.get(6)});
			for(List<String> booking : allBookings) {
				sqlMngr.delete("booking_num", booking.get(0));
				sqlMngr.insert("availability", availabilityColumns, new String[] {booking.get(1), booking.get(2), booking.get(3), booking.get(4)});
			}
		}
		System.out.println("");
		System.out.println("User with email '" + email + "' has been deleted.");
		mainMenu();
	}

	//Print menu options
	private void invalidOption() {
		System.out.println("");
		System.out.println("Not a valid option! Please try again.");
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
			Calendar cStart = Calendar.getInstance();
	        cStart.setTime(start);
	        int yStart = cStart.get(Calendar.YEAR);
	        int mStart = cStart.get(Calendar.MONTH) + 1;
	        int dStart = cStart.get(Calendar.DATE);
	        LocalDate ldStart = LocalDate.of(yStart, mStart, dStart);
			Calendar cEnd = Calendar.getInstance();
	        cEnd.setTime(end);
	        int yEnd = cEnd.get(Calendar.YEAR);
	        int mEnd = cEnd.get(Calendar.MONTH) + 1;
	        int dEnd = cEnd.get(Calendar.DATE);
	        LocalDate ldEnd = LocalDate.of(yEnd, mEnd, dEnd);
	        if(ldEnd.isAfter(ldStart)) {
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

	private Date isValidDate(String date) {
		if (date.equals("")) {
	    	System.out.println("");
	    	System.out.println("Date does not match required format. Please enter again.");
	    	System.out.println("");
		    return null;
		} else {
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    sdf.setLenient(false);
		    try {
		        Date dob = sdf.parse(date);
		        return dob;
		    } catch (ParseException e) {
		    	System.out.println("");
		    	System.out.println("Date does not match required format. Please enter again.");
		    	System.out.println("");
		        return null;
		    }
		}
	}

	private boolean isValidSin(String s) {
		boolean result = s.length() == 9 && isInteger(s,10);
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid SIN.");
	    	System.out.println("");
		}
		return result;
	}

	private boolean isValidCC(String s) {
		boolean result = s.length() >= 13 && s.length() <= 19 && isInteger(s,10);
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid CC.");
	    	System.out.println("");
		}
		return result;
	}

	private boolean isInteger(String s, int rad) {
	    if(s.isEmpty()) {
	    	return false;
	    }
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) {
	            	return false;
	            } else {
	            	continue;
	            }
	        }
	        if(Character.digit(s.charAt(i),rad) < 0) {
	        	return false;
	        }
	    }
	    return true;
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

    // Function that handles the feature: "3. Print schema."
	private void printSchema() {
		List<String> schema = sqlMngr.getSchema();

		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of tables: " + schema.size());
		for (int i = 0; i < schema.size(); i++) {
			System.out.println("Table: " + schema.get(i));
		}
		System.out.println("------------");
		System.out.println("");
	}

	private boolean checkExistingAccount(String table, String column, String value, boolean forDelete) throws SQLException {
		if (sqlMngr.select(table, new String[] {column}, column, new String[] {value}).size() > 0) {
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
		List<List<String>> vals = sqlMngr.select("user", new String[] {"password"}, "email", new String[] {email});
		boolean userExists = vals.size() == 1 && vals.get(0).get(0).equals(password);
		if (!userExists) {
			System.out.println("");
			System.out.println("Invalid email or password.");
		}
		return userExists;
	}

	private boolean isHost(List<String> userInfo) {
		return userInfo.get(userInfo.size() - 1) == null;
	}

	private boolean isNotOverlap(String startDateToCheck, String endDateToCheck, String suite_num, String house_num, String latitude, String longitude) {
		List<List<String>> allListings = sqlMngr.select("host", new String[] {"listing_num"}, "sin", new String [] {user.getSin()});
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		for (List<String> listing : allListings) {
			List<String> listingLocation = sqlMngr.select("location", new String[] {"suite_num", "house_num", "latitude", "longitude"}, "listing_num", new String [] {listing.get(0)}).get(0);
			if (listingLocation.get(0).equalsIgnoreCase(suite_num) && listingLocation.get(1).equalsIgnoreCase(house_num) && listingLocation.get(2).equalsIgnoreCase(latitude) && listingLocation.get(3).equalsIgnoreCase(longitude)) {
				List<List<String>> listingAvailabilities = sqlMngr.select("availability", new String[] {"start_date", "end_date"}, "listing_num", new String [] {listing.get(0)});
				for(List<String> availability : listingAvailabilities) {
					String startDateStr = availability.get(0);
					String endDateStr = availability.get(1);
					try {
						Date startDateToCheckDate = sdf.parse(startDateToCheck);
						Date endDateToCheckDate = sdf.parse(endDateToCheck);
				        Date startDate = sdf.parse(startDateStr);
				        Date endDate = sdf.parse(endDateStr);
				        Calendar cStart = Calendar.getInstance();
				        cStart.setTime(startDate);
				        int yStart = cStart.get(Calendar.YEAR);
				        int mStart = cStart.get(Calendar.MONTH) + 1;
				        int dStart = cStart.get(Calendar.DATE);
				        LocalDate ldStart = LocalDate.of(yStart, mStart, dStart);
						Calendar cEnd = Calendar.getInstance();
				        cEnd.setTime(endDate);
				        int yEnd = cEnd.get(Calendar.YEAR);
				        int mEnd = cEnd.get(Calendar.MONTH) + 1;
				        int dEnd = cEnd.get(Calendar.DATE);
				        LocalDate ldEnd = LocalDate.of(yEnd, mEnd, dEnd);
				        Calendar cStartCheck = Calendar.getInstance();
				        cStartCheck.setTime(startDateToCheckDate);
				        int yStartCheck = cStartCheck.get(Calendar.YEAR);
				        int mStartCheck = cStartCheck.get(Calendar.MONTH) + 1;
				        int dStartCheck = cStartCheck.get(Calendar.DATE);
				        LocalDate ldStartCheck = LocalDate.of(yStartCheck, mStartCheck, dStartCheck);
				        Calendar cEndCheck = Calendar.getInstance();
				        cEndCheck.setTime(endDateToCheckDate);
				        int yEndCheck = cEndCheck.get(Calendar.YEAR);
				        int mEndCheck = cEndCheck.get(Calendar.MONTH) + 1;
				        int dEndCheck = cEndCheck.get(Calendar.DATE);
				        LocalDate ldEndCheck = LocalDate.of(yEndCheck, mEndCheck, dEndCheck);
				        if (ldStartCheck.isAfter(ldStart) && ldStartCheck.isBefore(ldEnd) || ldEndCheck.isAfter(ldStart) && ldEndCheck.isBefore(ldEnd)|| ldStartCheck.compareTo(ldStart) == 0 || ldEndCheck.compareTo(ldStart) == 0 || ldStartCheck.compareTo(ldEnd) == 0 || ldEndCheck.compareTo(ldEnd) == 0 || ldStart.isBefore(ldEndCheck) && ldEnd.isAfter(ldStartCheck)) {
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
		return true;
	}
	private void bookings() {
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
			bookByCity();
		}
		else if(selection == 0) {
			goBack();
		}
		else {
			System.out.println("incorrect slection, try again");
		}

	}
	private void bookByDate() {
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
	private void bookByCity() {
		String country;
		String city;
		System.out.println("Enter Country:");
		country = sc.nextLine();
		System.out.println("Enter City");
		city = sc.nextLine();
		//displayByCity(country, city);
		startBookingByListingNumber();

	}

	private void bookByPostalCode() {
		String postalCode;
		System.out.println("Enter Postal Code:");
		postalCode = sc.nextLine();
		//displayByPostalCode(postalCode);
		startBookingByListingNumber();
	}
	private void goBack() {

	}
	private boolean bookByListingNumber(String listingNumber, User CurrUser) {

		boolean toReturn = false;
		double listingPerDayCost = 1;
		int numberOfDays = 1;
		String startDate;
		Date start;
		Date stop;
		String endDate;

		System.out.println("Please enter the date you want to book it from");
		startDate = sc.nextLine();
		System.out.println("Please enter the date you want to end the booking at");
		endDate = sc.nextLine() ;
		if(checkIfFree(startDate, endDate, listingNumber)) {
			start = isValidDate(startDate);
			stop = isValidDate(endDate);
			//numberOfDays = ChronoUnit.DAYS.between(start, stop);
			invoice(user, numberOfDays, listingPerDayCost);

			updateAvailability(startDate, endDate, listingNumber);
			addBooking(listingNumber,startDate, endDate, listingPerDayCost );
			toReturn = true;
		}
		else {
			System.out.println("The lisitng you chose is not free on the following dates");

		}
		return toReturn;
	}
	private void invoice(User currUser, int numbDays, double cost) {
		double total = cost*numbDays;
		System.out.print("Dear "+ user.getLastName() + " " + user.getFirstName() +"," + total + " has been deducted from your account");
	}
	private void startBookingByListingNumber() {
		String listingNumber;
		System.out.println("Enter Listing number to book");
		listingNumber = sc.nextLine();
		if(bookByListingNumber(listingNumber, user)) {
			System.out.println("Your booking has been made");
		}
		else {
			System.out.println("This booking was not successful.");
		}
	}
	private boolean checkIfFree(String startDate, String endDate, String listingnumber) {
		int i = 0;
		String currStart;
		String currStop;
		boolean toReturn = false;
		List<List<String>> allDates = sqlMngr.select("availability", new String[] {"startDate", "endDate"}, "listing_num", new String[] {listingnumber});
		while(i < allDates.size() && !toReturn) {
				currStart = allDates.get(i).get(0);
				currStop = allDates.get(i).get(0);
				toReturn = occursAfter(currStart, startDate) && occursAfter(endDate,currStop);
			}
			
		
		return toReturn;

	}
	private void updateAvailability(String startDate, String endDate, String listingNumber) {

	}
	public void addBooking(String listing, String startDate, String endDate, double cost)  {
		sqlMngr.insert("booking", bookingColumns,new String[] {UUID.randomUUID().toString(), listing, startDate, endDate, String.valueOf(cost), user.getSin(),null,null,null,null,null,null});
	}
	private boolean occursAfter(String first, String second) {
		return true;
	}


}
