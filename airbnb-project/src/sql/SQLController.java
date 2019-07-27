package sql;

import java.security.interfaces.RSAKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class acts as the medium between our CommandLine interface
 * and the SQL Backend. It is a controller class.
 */
public class SQLController {
	
	private static final String dbClassName = "com.mysql.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/airbnb";
	private static final String sqlUser = "root";
	private static final String sqlPass = "irw1rgi5";
    //Object that establishes and keeps the state of our application's
    //connection with the MySQL backend.
	private Connection conn = null;
    //Object which communicates with the SQL backend delivering to it the
    //desired query from our application and returning the results of this
    //execution the same way that are received from the SQL backend.
	private Statement st = null;
	
    // Initialize current instance of this class.
	public boolean connect() throws ClassNotFoundException {
		Class.forName(dbClassName);
		boolean success = true;
		try {
			conn = DriverManager.getConnection(CONNECTION, sqlUser, sqlPass);
			st = conn.createStatement();
		} catch (SQLException e) {
			success = false;
			System.err.println("Connection could not be established!");
			e.printStackTrace();
		}
		return success;
	}

    // Destroy the private objects/fields of current instance of this class.
    // Acts like a destructor.
	public void disconnect() {
		try {
			st.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("Exception occured while disconnecting!");
			e.printStackTrace();
		} finally {
			st = null;
			conn = null;
		}
	}
	
    // Controls the execution of functionality: "3. Print schema."
	public ArrayList<String> getSchema() {
		ArrayList<String> output = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet schemas = meta.getTables(null,null,"%",null);
			//ResultSet catalogs = meta.getCatalogs();
			while (schemas.next()) {
				output.add(schemas.getString("TABLE_NAME"));
			}
			schemas.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Schema Info failed!");
			e.printStackTrace();
			output.clear();
		}
		return output;
	}
	
    // Controls the execution of functionality: "4. Print table schema."
	public ArrayList<String> colValues(String tableName, String columnName) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, tableName, columnName);
			while(rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Table Info failed!");
			e.printStackTrace();
			result.clear();
		}
		return result;
	}
	
    //Controls the execution of a select query.
    //Functionality: "2. Select a record."
	public List<List<String>> select(String table, String[] resultColumns, String checkColumn, String[] values) {
		String query = "SELECT ";
		for (int counter = 0; counter < resultColumns.length; counter ++) {
			query = query.concat(resultColumns[counter]);
			if (counter < resultColumns.length - 1) {
				query = query.concat(",");
			}
		}
		query = query.concat(" FROM " + table + " WHERE " + checkColumn + " IN (");
		for (int counter = 0; counter < values.length; counter++) {
			query = query.concat("'" + values[counter] + "'");
			if (counter < values.length - 1) {
				query = query.concat(",");
			} else {
				query = query.concat(");");
			}
		}
		List<List<String>> result = new ArrayList<List<String>>();
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				List<String> curr = new ArrayList<String>();
				for (int counter = 0; counter < resultColumns.length; counter ++) {
					curr.add(rs.getString(resultColumns[counter]));
				}
				result.add(curr);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during select execution!");
			e.printStackTrace();
		}
		return result;
	}
	
    //Controls the execution of an insert query.
    //Functionality: "1. Insert a record."
	public void insert(String table, String[] columns, String[] vals) {
		String query = "INSERT INTO " + table + "("; 
		for (int counter = 0; counter < columns.length; counter++) {
			query = query.concat(columns[counter]);
			if (counter < columns.length - 1) {
				query = query.concat(",");
			}
		}
		query = query.concat(") VALUES(");
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
		try {
			st.executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Exception triggered during insert execution!");
			e.printStackTrace();
		}
	}
	
	public List<String> getUserInfo(String email) {
		String query = "SELECT * FROM user WHERE email IN ('" + email + "');";
		List<String> result = new ArrayList<String>();
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				result.add(rs.getString("email"));
				result.add(rs.getString("first_name"));
				result.add(rs.getString("last_name"));
				result.add(rs.getString("dob"));
				result.add(rs.getString("address"));
				result.add(rs.getString("occupation"));
				result.add(rs.getString("sin"));
				result.add(rs.getString("password"));
				result.add(rs.getString("cc"));
				result.add(rs.getString("num_cancellations"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during select execution!");
			e.printStackTrace();
		}
		return result;
	}

	public void delete(String column, String value) {
		List<String> deleteQueries = deleteQueryGenerator(column, value);
		try {
			for(String query : deleteQueries) {
				st.executeUpdate(query);
			}
		} catch (SQLException e) {
			System.err.println("Exception triggered during delete execution!");
			e.printStackTrace();
		}
	}

	public List<String> deleteQueryGenerator(String column, String value) {
		String query = "SELECT CONCAT(\"DELETE FROM \",TABLE_NAME,\" WHERE " + column + " = '" + value + "';\") comd FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'airbnb' AND COLUMN_NAME ='" + column + "';";
		List<String> deleteQueries = new ArrayList<String>();
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				deleteQueries.add(rs.getString("comd"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered!");
			e.printStackTrace();
		}
		return deleteQueries;
	}

	public void update(String table,String[] identifyField, String[] identify,String[] fields, String[] newValues){
		int i = 0;
		String query = "UPDATE" + table +"\n SET";
		while(i < fields.length) {
			
			query = query + "\t" + fields[i] + " = " + newValues[i] + ",\n";
			i++;
		}
		i = 0;
		query = query + " [ WHERE + \n ";
		while(i + 1< identifyField.length) {
			query = query + "\t"+ identifyField[i] + " = " + identify[i] + " , \n";
			
			i++;
			
			
		}
		query = query + "\t"+ identifyField[i] + " = " + identify[i] + " ];";
		try {
			st.executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Exception triggered during update execution!");
			e.printStackTrace();
		}
	}

	public List<List<String>> report1() {
		String query = "SELECT booking.start_date, booking.end_date, location.city\n" + 
				"FROM booking\n" + 
				"NATURAL JOIN location;";
		List<List<String>> result = new ArrayList<List<String>>();
		try {
			ResultSet rs = st.executeQuery(query);
			List<String> curr = new ArrayList<String>();
			while(rs.next()) {
				curr.add(rs.getString("start_date"));
				curr.add(rs.getString("end_date"));
				curr.add(rs.getString("city"));
				result.add(curr);
				curr = new ArrayList<String>();
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during select execution!");
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String, String> report2(String city) {
		String query = "SELECT location.city, location.postal_code, COUNT(postal_code) AS count\n" + 
				"FROM booking\n" + 
				"NATURAL JOIN location\n" + 
				"WHERE city = '" + city + "'\n" + 
				"GROUP BY postal_code;";
		Map<String, String> result = new HashMap<String, String>();
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				result.put(rs.getString("postal_code"), rs.getString("count"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during select execution!");
			e.printStackTrace();
		}
		return result;
	}

}
