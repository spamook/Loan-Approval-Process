package com.example.lap.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class IsikukoodParser {

    public LocalDate getBirthDate(String personalCode) {
        if (personalCode == null || !personalCode.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("Invalid personal code format");
        }
        
        int centuryIndicator = Character.getNumericValue(personalCode.charAt(0));
        int yearOffset = centuryIndicator <= 2 ? 1800 :
                         centuryIndicator <= 4 ? 1900 :
                         centuryIndicator <= 6 ? 2000 : 2100;
                         
        int year = yearOffset + Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));
        
        return LocalDate.of(year, month, day);
    }
    
    public int getAge(String personalCode) {
        return Period.between(getBirthDate(personalCode), LocalDate.now()).getYears();
    }
}
