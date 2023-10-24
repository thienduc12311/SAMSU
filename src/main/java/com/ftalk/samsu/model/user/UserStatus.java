package com.ftalk.samsu.model.user;

public class UserStatus {
    public static String getStatus(Short roleValue){
        switch (roleValue){
            case 0:
                return "DISABLE";
            case 1:
                return "ENABLE";
        }
        return null;
    }

    public static Short getStatusValue(String role){
        switch (role){
            case "DISABLE":
                return (short) 0;
            case "ENABLE":
                return (short) 1;
        }
        return (short) -1;
    }
}
