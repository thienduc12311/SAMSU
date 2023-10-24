package com.ftalk.samsu.model.user;

public class UserRole {
    public static String getRole(Short roleValue){
        switch (roleValue){
            case 0:
                return "ROLE_ADMIN";
            case 1:
                return "ROLE_MANAGER";
            case 2:
                return "ROLE_STAFF";
            case 3:
                return "ROLE_STUDENT";
        }
        return null;
    }

    public static Short getRoleValue(String role){
        switch (role){
            case "ROLE_ADMIN":
                return (short) 0;
            case "ROLE_MANAGER":
                return (short) 1;
            case "ROLE_STAFF":
                return (short) 2;
            case "ROLE_STUDENT":
                return (short) 3;
        }
        return (short) -1;
    }
}
