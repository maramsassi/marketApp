package com.example.maram.marketinpoket;

/**
 * Created by root on 06/08/17.
 */

public class Validation {


    // phone number sould contain 10 digits and starts with 091|092|094
    static boolean validatePhoneNumber(String phoneNo) {



        if (phoneNo.matches("^(?:((?=\\(.*\\).*\\-)|"
                + "(?!.*\\()(?!.*\\)))"
                + "\\(?|091|092|094\\)?"
                + "(((?<=[)])|[\\-\\s])"
                + "(?=.*\\-)|\\.(?=.*\\.)"
                + "|(?<=^)|(?=[0-9]+$)))"
                + "?[0-9]{3}[\\s.-]?[0-9]{4}$"))

            return true;
        else


            return false ;


    }


    // Validate the username with these rules:
    //Username shuold start with alphanumeric characters
    //5 ≤ |Username| ≤ 15
    //Valid characters: a-z, A-Z, 0-9, points, dashes and underscores.
    static boolean validateUserName(String userNAme) {



        if (userNAme.matches("^[A-Za-z_][a-zA-Z0-9._-]{5,15}$"))

            return true;
        else


            return false ;


    }


    // Validate the market name with these rules:
    //markwt name c alphanumeric characters
    //5 ≤ |marketname| ≤ 15
    //Valid characters: a-z, A-Z, 0-9 and spaces.
    static boolean validateMarketName(String marketName) {



        if (
                (marketName .length()>= 5 &&
                        marketName.length() <= 15))
//
//        if (marketName.trim().matches("^[A-Za-z].*[a-zA-Z0-9._-]|[\\s].*") &&
//                (marketName .length()>= 5 &&
//                        marketName.length() <= 15))

            return true;
        else


            return false ;


    }
}
