package com.ripple.cloudshare;

public class ApplicationConstants {

    //validation regex
    public static final String EMAIL_VALIDATION_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

    public static final String MOBILE_VALIDATION_REGEX = "^[2-9]{2}\\d{8}$";

    public static final String USER_TYPE_VALIDATION_REGEX = "NON_ADMIN|ADMIN";

    //Validation messages
    public static final String INVALID_USER_TYPE_MESSAGE = "The value of user_type must be from: " + USER_TYPE_VALIDATION_REGEX;

}
