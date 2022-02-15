package com.ripple.cloudshare;

public class ApplicationConstants {

    //validation regex
    public static final String EMAIL_VALIDATION_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

    public static final String MOBILE_VALIDATION_REGEX = "^[2-9]{2}\\d{8}$";

    public static final String USER_TYPE_VALIDATION_REGEX = "NON_ADMIN|ADMIN";
    public static final String OPERATING_SYSTEM_VALIDATION_REGEX = "LINUX|MAC|WINDOWS";

    //Validation messages
    public static final String INVALID_USER_TYPE_MESSAGE = "The value of user_type must be from: " + USER_TYPE_VALIDATION_REGEX;
    public static final String INVALID_OPERATING_SYSTEM_MESSAGE = "The value of operating_system must be from: " + OPERATING_SYSTEM_VALIDATION_REGEX;

    //Exception messages
    public static final String SELF_DESTRUCT_NOT_ALLOWED = "Suicide is not allowed at this platform";
    public static final String ADMIN_CAN_NOT_DELETE_OTHER_ADMIN = "Can not delete admin user with given id";
    public static final String NO_USER_WITH_GIVEN_ID = "No user exists with given id";
    public static final String NO_USER_WITH_GIVEN_EMAIL = "No user exists with given email";
    public static final String EMAIL_OR_MOBILE_ALREADY_IN_USE = "email or mobile already in use";

    public static final String NO_MACHINE_WITH_ID = "No live machine found with given id";
    public static final String NO_MACHINE_WITH_ID_FOR_USER = "No live machine found with given id for the user";
    public static final String NO_CAPACITY_AVAILABLE = "All servers are tightly occupied, please try later";
    public static final String COULD_NOT_REMOVE_VM = "Could not remove requested VM to delete";

    public static final String SOMETHING_WENT_WRONG = "Something went wrong, please retry after some time";

    //Other constants
    public static final String TOP_LEVEL_DOMAIN = "cloud.ripple.com";
}
