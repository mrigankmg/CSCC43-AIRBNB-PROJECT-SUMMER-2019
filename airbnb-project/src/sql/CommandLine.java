package sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CommandLine {

    // 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
    // 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	
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
			
			String input = "";
			int choice = -1;
			do {
				System.out.println("");
				System.out.println("=========LOGIN/SIGN-UP=========");
				System.out.println("0. Exit");
				System.out.println("1. Login");
				System.out.println("2. Sign-Up");
				System.out.print("Choose one of the options [0-2]: ");
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
		} else {
			System.out.println("");
			System.out.println("Connection could not been established!");
			System.out.println("");
		}
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
	}

	//Print menu options
	private void invalidOption() {
		System.out.println("");
		System.out.println("Not a valid option! Please try again.");
	}
	
    // Called during the initialization of an instance of the current class
    // in order to retrieve from the user the credentials with which our program
    // is going to establish a connection with MySQL
	private void signUpForm(boolean renterSignUp) throws SQLException {
		System.out.println("");
		System.out.println("=========SIGN UP FORM=========");
		String[] cred = new String[9];
		do {
			System.out.print("Email: ");
			cred[0] = sc.nextLine().trim();
		} while (checkExistingAccount("Users", "email", cred[0]));
		System.out.print("First Name: ");
		cred[1] = sc.nextLine().trim();
		System.out.print("Last Name: ");
		cred[2] = sc.nextLine().trim();
		do {
			System.out.print("DOB (dd/mm/yyyy): ");
			cred[3] = sc.nextLine();
		} while(!isValidDateFormat(cred[3]));
		System.out.print("Address: ");
		cred[4] = sc.nextLine().trim();
		System.out.print("Occupation: ");
		cred[5] = sc.nextLine().trim();
		do {
			System.out.print("SIN: ");
			cred[6] = sc.nextLine().trim();
		} while (!isInteger(cred[6], 10) && checkExistingAccount("Users", "sin", cred[6]));
		System.out.print("Password: ");
		cred[7] = sc.nextLine();
		if (renterSignUp) {
			do {
				System.out.print("Credit Card No.: ");
				cred[8] = sc.nextLine().trim();
			} while (!isInteger(cred[8], 10));
			//String[] rentersArray = new String[] {cred[5], cred[7]};
			//insert("Renters", rentersArray);
		} else {
			cred[8] = null;
		}
		insertUser("Users", cred);
		System.out.println("");
		System.out.println("You have successfully signed up!");
		signUpMenu();
	}
	
	public boolean isValidDateFormat(String value) {
	    LocalDateTime ldt = null;
	    DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
	    try {
	        ldt = LocalDateTime.parse(value, fomatter);
	        String result = ldt.format(fomatter);
	        return result.equals(value);
	    } catch (DateTimeParseException e) {
	        try {
	            LocalDate ld = LocalDate.parse(value, fomatter);
	            String result = ld.format(fomatter);
	            if (!result.equals(value)) {
	            	System.out.println("");
	            	System.out.println("Date does not match required format. Please enter again.");
	            	System.out.println("");
	            	return false;
	            }
	            else {
	            	return true;
	            }
	        } catch (DateTimeParseException exp) {
	            try {
	                LocalTime lt = LocalTime.parse(value, fomatter);
	                String result = lt.format(fomatter);
		            if (!result.equals(value)) {
		            	System.out.println("");
		            	System.out.println("Date does not match required format. Please enter again.");
		            	System.out.println("");
		            	return false;
		            }
		            else {
		            	return true;
		            }
	            } catch (DateTimeParseException e2) {
	                // Debugging purposes
	                //e2.printStackTrace();
	            }
	        }
	    }
    	System.out.println("");
    	System.out.println("Date does not match required format. Please enter again.");
    	System.out.println("");
	    return false;
	}
	
	public boolean isInteger(String s, int rad) {
	    if(s.isEmpty()) {
	    	System.out.println("");
	    	System.out.println("Please enter a valid number.");
	    	System.out.println("");
	    	return false;
	    }
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) {
	    	    	System.out.println("");
	    	    	System.out.println("Please enter a valid number.");
	    	    	System.out.println("");
	            	return false;
	            } else {
	            	continue;
	            }
	        }
	        if(Character.digit(s.charAt(i),rad) < 0) {
		    	System.out.println("");
		    	System.out.println("Please enter a valid number.");
		    	System.out.println("");
	        	return false;
	        }
	    }
	    return true;
	}
	
    // Function that handles the feature: "3. Print schema."
	private void printSchema() {
		ArrayList<String> schema = sqlMngr.getSchema();
		
		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of tables: " + schema.size());
		for (int i = 0; i < schema.size(); i++) {
			System.out.println("Table: " + schema.get(i));
		}
		System.out.println("------------");
		System.out.println("");
	}
	
//    // Function that handles the feature: "4. Print table schema."
//	private void printColSchema() {
//		System.out.print("Table Name: ");
//		String tableName = sc.nextLine();
//		ArrayList<String> result = sqlMngr.colSchema(tableName);
//		System.out.println("");
//		System.out.println("------------");
//		System.out.println("Total number of fields: " + result.size()/2);
//		for (int i = 0; i < result.size(); i+=2) {
//			System.out.println("-");
//			System.out.println("Field Name: " + result.get(i));
//			System.out.println("Field Type: " + result.get(i+1));
//		}
//		System.out.println("------------");
//		System.out.println("");
//	}

	private boolean checkExistingAccount(String table, String column, String value) throws SQLException {
		if (select(table, column, column, value).size() > 0) {
			System.out.println("");
			System.out.println("Account with this " + column + " already exists.");
			System.out.println("");
			return true;
		}
		return false;
	}
	
	private boolean checkLoginCredentials(String email, String password) throws SQLException {
		ArrayList<String> vals = select("Users", "password", "email", email);
		boolean userExists = vals.size() == 1 && vals.get(0).equals(password);
		if (!userExists) {
			System.out.println("");
			System.out.println("Invalid email or password.");
			System.out.println("");
		}
		return userExists;
	}

    // Function that handles the feature: "2. Select a record."
	private ArrayList<String> select(String table, String resultColumn, String checkColumn, String value) throws SQLException {
		String query = "SELECT " + resultColumn + " FROM " + table + " WHERE " + checkColumn + " IN ('" + value + "');";
		ArrayList<String> result = new ArrayList<String>();
		ResultSet rs = sqlMngr.selectOp(query);
		while(rs.next()) {
			result.add(rs.getString(resultColumn));
		}
		return result;
	}

    // Function that handles the feature: "1. Insert a record."
	private void insertUser(String table, String[] vals) {
		String query = "";
		query = "INSERT INTO Users (email, first_name, last_name, dob, address, occupation, sin, password, cc) VALUES("; 
		for (int counter = 0; counter < vals.length; counter++) {
			if (vals[counter] == null) {
				query = query.concat("NULL");
			} else {
				query = query.concat("'" + vals[counter] + "'");
			}
			if (counter < vals.length - 1) {
				query = query.concat(",");
			} else {
				query = query.concat(");");
			}
		}
		sqlMngr.insertOp(query);
	}

}
