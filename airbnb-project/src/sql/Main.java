package sql;

import java.sql.SQLException;
import java.text.ParseException;

public class Main {
	public static void main(String[] args) throws ParseException {
		CommandLine commandLine = new CommandLine();
		if (commandLine.startSession()) {
			try {
				commandLine.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
