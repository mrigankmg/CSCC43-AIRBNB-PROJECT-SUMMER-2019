package users;

public class Renter extends User {
	
	private String cc;

	public Renter (String email, String firstName, String lastName, String dob, String address, String occupation, String sin, String password, String cc) {
		super(email, firstName, lastName, dob, address, occupation, sin, password);
		this.cc = cc;
	}
	
	public void setCC(String cc) {
		this.cc = cc;
	}
	
	public String getCC() {
		return cc;
	}

}
