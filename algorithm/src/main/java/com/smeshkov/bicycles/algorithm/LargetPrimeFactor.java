package com.smeshkov.bicycles.algorithm;

/**
 * @author smeshkov
 * @since 15/03/15
 */
public class LargetPrimeFactor {

    /** * @return largest prime factor of a number */
    public static int largestPrimeFactor(long number) {
        int i;
        long copyOfInput = number;
        for (i = 2; i <= copyOfInput; i++) {
            if (copyOfInput % i == 0) {
                copyOfInput /= i;
                i--;
            }
        }
        return i;
    }

}
