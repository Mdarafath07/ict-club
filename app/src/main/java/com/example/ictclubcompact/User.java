package com.example.ictclubcompact;

public class User {
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String userId;
    private String dob;
    private String bloodGroup;
    private String semester;
    private String department;
    private String session;
    private boolean isAdmin; // Added admin field

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String name, String email, String phone, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = formatPhoneNumber(phone);
        this.profileImageUrl = profileImageUrl;

    }

    // Add this getter and setter for admin status
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    // Keep all your existing getters and setters exactly as they are
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = formatPhoneNumber(phone);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    // Keep your existing helper method
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        String digits = phone.replaceAll("[^0-9]", "");

        if (digits.startsWith("880")) {
            return "+" + digits;
        } else if (digits.startsWith("01") && digits.length() == 11) {
            return "+880" + digits.substring(1);
        } else if (digits.length() == 10) {
            return "+880" + digits;
        }

        return phone;
    }

    // Update toString() to include admin status
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", dob='" + dob + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", semester='" + semester + '\'' +
                ", department='" + department + '\'' +
                ", session='" + session + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}