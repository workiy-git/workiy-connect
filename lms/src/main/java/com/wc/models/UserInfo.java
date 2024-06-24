package com.wc.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "userinfo")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "E-mail cannot be empty!")
    @Email(message = "Please provide a valid email!")
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "Password cannot be empty!")
    @Length(min = 5, message = "Choose atleast five characters for password!")
    @Column(name = "password")
    private String password;

    @NotEmpty(message = "Please provide a role!")
    @Column(name = "role")
    private String role;

    @NotEmpty(message = "Please provide first name!")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Please provide last name!")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "active")
    private boolean active;
    
    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_creation_time")
    private LocalDateTime resetTokenCreationTime;
    
    @Column(name = "leave_balance")
    private int leaveBalance;
    
   
    @Length(min = 8, max = 10, message = "Phone number must be between 8 and 10 characters long!")
    @Column(name = "phone_number")
    private String phoneNumber;
    
  
    @Length(min = 8, max = 10, message = "Phone number must be between 8 and 10 characters long!")
    @Column(name = "emergency_phone_number")
    private String emergencyPhoneNumber;
    

    @Column(name = "blood_group")
    private String bloodGroup;
    

    @Column(name = "address")
    private String address;
    

    @Column(name = "temporary_address")
    private String temporaryAddress;
    

    @Length(max = 10, message = "zip_code must be 10 characters long!")
    @Column(name = "zip")
    private String zip;

 
    @Column(name = "city")
    private String city;

    
    @Column(name = "state")
    private String state;


    @Column(name = "country")
    private String country;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    
    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;
    
    private String profilePictureType; // e.g., "jpeg", "jpg", "png", "gif"
   
//    @NotEmpty(message = "Please provide a time zone!")
//    @Column(name = "time_zone")
//    private String timeZone; // New timeZone field

    public int getId() {
	return id;	
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
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

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean active) {
	this.active = active;
    }
    
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
        this.resetTokenCreationTime = LocalDateTime.now();
    }

    public LocalDateTime getResetTokenCreationTime() {
        return resetTokenCreationTime;
    }
    
    public int getLeaveBalance() {
        return leaveBalance;
    }

    public void setLeaveBalance(int leaveBalance) {
        this.leaveBalance = leaveBalance;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmergencyPhoneNumber() {
        return emergencyPhoneNumber;
    }

    public void setEmergencyPhoneNumber(String emergencyPhoneNumber) {
        this.emergencyPhoneNumber = emergencyPhoneNumber;
    }
    
    public String getTemporaryAddress() {
        return temporaryAddress;
    }

    public void setTemporaryAddress(String temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    // Getter and Setter for city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getProfilePictureType() {
        return profilePictureType;
    }

    public void setProfilePictureType(String profilePictureType) {
        this.profilePictureType = profilePictureType;
    }
//    public String getTimeZone() {
//        return timeZone;
//    }
//
//    public void setTimeZone(String timeZone) {
//        this.timeZone = timeZone;
//    }
}	

//    public void setResetToken(String resetToken) {
//        this.resetToken = resetToken;
//        this.resetTokenCreationTime1 = LocalDateTime.now(); // Set the creation time when the token is set
//    }
//    
//    public LocalDateTime getResetTokenCreationTime() {
//        return resetTokenCreationTime1;
//    }
//}
