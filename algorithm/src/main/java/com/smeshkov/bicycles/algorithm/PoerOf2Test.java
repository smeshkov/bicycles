package com.smeshkov.bicycles.algorithm;

/**
 * @author smeshkov
 * @since 15/03/15
 */
public class PoerOf2Test {

    public static void main(String[] args) {
        System.out.println(isPowerOfTwo(8));
    }

    /*
     * checking if number is power of 2 using bit shift operator in java
     * e.g. 4 in binary format is "0000 0000 0000 0000 0000 0000 0000 0100";
     * and -4 is                  "1111 1111 1111 1111 1111 1111 1111 1100";
     * and 4&-4 will be           "0000 0000 0000 0000 0000 0000 0000 0100"
     */
    public static boolean isPowerOfTwo(int number) {
        if(number <=0){
            throw new IllegalArgumentException("number: " + number);
        }
        if ((number & -number) == number) {
            return true;
        }
        return false;
    }

    /*
     * checking if number is power of 2 using brute force
     * starts with 1, multiplying with 2 it will eventually be same as original number
     */
    public static boolean powerOfTwo(int number){
        int square = 1;
        while(number >= square){
            if(number == square){
                return true;
            }
            square = square*2;
        }
        return false;
    }

    /*
     * find if an integer number is power of 2 or not using bit shift operator
     */

    public static boolean checkPowerOfTwo(int number){
        if(number <=0){
            throw new IllegalArgumentException("number: " + number);
        }
        return ((number & (number -1)) == 0);
    }

}
