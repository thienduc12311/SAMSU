package com.ftalk.samsu;
//
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptExample {
    public static void main(String[] args) {
        // Chuỗi bạn muốn mã hóa
        String password = "123456";

        // Để mã hóa chuỗi sử dụng BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.println("Mã hash bcrypt: " + hashedPassword);
    }
}