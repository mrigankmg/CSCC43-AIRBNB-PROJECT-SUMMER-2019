package sql;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) {
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
