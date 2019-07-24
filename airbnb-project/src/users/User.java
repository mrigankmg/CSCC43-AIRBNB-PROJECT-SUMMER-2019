package users;

public abstract class User {

	String email;
	String firstName;
	String lastName;
	String dob;
	String address;
	String occupation;
	String sin;
	String password;

	public User(String email, String firstName, String lastName, String dob, String address, String occupation, String sin, String password) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.address = address;
		this.occupation = occupation;
		this.sin = sin;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getSin() {
		return sin;
	}

	public void setSin(String sin) {
		this.sin = sin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
