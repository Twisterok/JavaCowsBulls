/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.utils;

/**
 *
 * @author Alexander
 */
public class Helper {
    
    public static int countBulls(String basic_number, String turn_number)
    {
        int count = 0;
        if (basic_number.length() != turn_number.length())  return -1;
        for (int i = 0; i < basic_number.length(); ++i)
        {
            if (basic_number.charAt(i) == turn_number.charAt(i))    count++;
        }
        return count;        
    }
    
    public static int countCows(String basic_number, String turn_number)
    {
        int count = 0;
        if (basic_number.length() != turn_number.length())  return -1;
        for (int i = 0; i < turn_number.length(); ++i)
        {
            if (    basic_number.charAt(i) != turn_number.charAt(i) &&
                    basic_number.indexOf(turn_number.charAt(i)) >= 0)
            {
                count++;
            }
        }
        return count;        
    }
}
