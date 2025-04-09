package com.example.ictclubcompact;

public class User {
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String userId;  // Added user ID field

    // Required empty constructor for Firebase
    public User() {
        // Default constructor required for Firebase DataSnapshot.getValue(User.class)
    }

    // Constructor with parameters for easy object creation
    public User(String userId, String name, String email, String phone, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters
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
        // Ensure phone number is stored with proper formatting
        this.phone = formatPhoneNumber(phone);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Helper method to format phone numbers consistently
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        // Remove all non-digit characters
        String digits = phone.replaceAll("[^0-9]", "");

        // Format as international number if not already
        if (digits.startsWith("880")) {
            return "+" + digits;
        } else if (digits.startsWith("01") && digits.length() == 11) {
            return "+880" + digits.substring(1);
        } else if (digits.length() == 10) {
            return "+880" + digits;
        }

        return phone; // Return original if format unrecognized
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}