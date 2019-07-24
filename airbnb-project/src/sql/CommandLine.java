package sql;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import users.*;

public class CommandLine {

    // 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
    // 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	private static final String[] usersColumns = new String [] {"email", "first_name", "last_name", "dob", "address", "occupation", "sin", "password", "cc"};
	
	//Public functions - CommandLine State Functions
	
    /* Function used for initializing an istance of current
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
		System.out.println("");
		System.out.println("=========LOGIN=========");
		String[] cred = new String[2];
		do {
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
			System.out.print("Password: ");
			cred[1] = sc.nextLine();
		} while (!checkLoginCredentials(cred[0], cred[1]));
		List<String> userInfo = sqlMngr.getUserInfo(cred[0]);
		User user;
		if (isHost(userInfo)) {
			user = new Host(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7));
		} else {
			user = new Renter(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6), userInfo.get(7), userInfo.get(8));
		}
		home(user);
	}
	
	private void home(User user) {
		System.out.println("");
		System.out.println("=========HOME=========");
		System.out.println("Welcome " + user.getFirstName() + "!");
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
		} while (checkExistingAccount("users", "email", cred[0], false));
		System.out.print("First Name: ");
		cred[1] = sc.nextLine().trim();
		System.out.print("Last Name: ");
		cred[2] = sc.nextLine().trim();
		do {
			System.out.print("DOB (dd/mm/yyyy): ");
			cred[3] = sc.nextLine();
		} while(!isValidDate(cred[3]));
		System.out.print("Address: ");
		cred[4] = sc.nextLine().trim();
		System.out.print("Occupation: ");
		cred[5] = sc.nextLine().trim();
		do {
			System.out.print("SIN (9 digits): ");
			cred[6] = sc.nextLine().trim();
		} while (!isValidSin(cred[6]) || checkExistingAccount("users", "sin", cred[6], false));
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
		sqlMngr.insert("users", usersColumns, cred);
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
		} while (!checkExistingAccount("users", "email", email, true));
		sqlMngr.deleteUser(email);
		System.out.println("");
		System.out.println("User with email '" + email + "' has been deleted.");
		mainMenu();
	}

	//Print menu options
	private void invalidOption() {
		System.out.println("");
		System.out.println("Not a valid option! Please try again.");
	}

	public boolean isValidDate(String date) {
		if (date.equals("")) {
	    	System.out.println("");
	    	System.out.println("Date does not match required format. Please enter again.");
	    	System.out.println("");
		    return false;
		} else {
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    sdf.setLenient(false);
		    try {
		        Date dob = sdf.parse(date); 
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
		    } catch (ParseException e) {
		    	System.out.println("");
		    	System.out.println("Date does not match required format. Please enter again.");
		    	System.out.println("");
		        return false;
		    }
		}
	}

	public boolean isValidSin(String s) {
		boolean result = s.length() == 9 && isInteger(s,10); 
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid SIN.");
	    	System.out.println("");
		}
		return result;
	}
	
	public boolean isValidCC(String s) {
		boolean result = s.length() >= 13 && s.length() <= 19 && isInteger(s,10); 
		if (!result) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid CC.");
	    	System.out.println("");
		}
		return result;
	}
	
	public boolean isInteger(String s, int rad) {
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
		if (sqlMngr.select(table, column, column, value).size() > 0) {
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
		List<String> vals = sqlMngr.select("users", "password", "email", email);
		boolean userExists = vals.size() == 1 && vals.get(0).equals(password);
		if (!userExists) {
			System.out.println("");
			System.out.println("Invalid email or password.");
			System.out.println("");
		}
		return userExists;
	}
	
	private boolean isHost(List<String> userInfo) {
		return userInfo.get(userInfo.size() - 1) == null;
	}

}
