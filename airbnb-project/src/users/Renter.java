package users;

public class Renter extends User {
	
	private String cc;

	public Renter (String email, String firstName, String lastName, String dob, String address, String occupation, String sin, String password, String cc, String cancellations) {
		super(email, firstName, lastName, dob, address, occupation, sin, password, cancellations);
		this.cc = cc;
	}
	
	public void setCC(String cc) {
		this.cc = cc;
	}
	
	public String getCC() {
		return cc;
	}

}
