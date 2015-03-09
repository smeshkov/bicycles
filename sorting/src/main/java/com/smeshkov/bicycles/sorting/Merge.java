package com.smeshkov.bicycles.sorting;

import static com.smeshkov.bicycles.sorting.SortHelper.*;

/*************************************************************************
 *  Compilation:  javac MergeX.java
 *  Execution:    java MergeX < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/22mergesort/tiny.txt
 *                http://algs4.cs.princeton.edu/22mergesort/words3.txt
 *
 *  Sorts a sequence of strings from standard input using an
 *  optimized version of mergesort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java MergeX < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java MergeX < words3.txt
 *  all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 *************************************************************************/

/**
 *  The <tt>MergeX</tt> class provides static methods for sorting an
 *  array using an optimized version of mergesort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/22mergesort">Section 2.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Merge {

    private static final int CUTOFF = 7;  // cutoff to insertion sort

    // This class should not be instantiated.
    private Merge() {}

    private static void merge(Comparable[] src, Comparable[] dst, int lo, int mid, int hi) {

        // precondition: src[lo .. mid] and src[mid+1 .. hi] are sorted subarrays
        assert isSorted(src, lo, mid);
        assert isSorted(src, mid+1, hi);

        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              dst[k] = src[j++];
            else if (j > hi)               dst[k] = src[i++];
            else if (less(src[j], src[i])) dst[k] = src[j++];   // to ensure stability
            else                           dst[k] = src[i++];
        }

        // postcondition: dst[lo .. hi] is sorted subarray
        assert isSorted(dst, lo, hi);
    }

    private static void sort(Comparable[] src, Comparable[] dst, int lo, int hi) {
        // if (hi <= lo) return;
        if (hi <= lo + CUTOFF) {
            insertionSort(dst, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(dst, src, lo, mid);
        sort(dst, src, mid+1, hi);

        // if (!less(src[mid+1], src[mid])) {
        //    for (int i = lo; i <= hi; i++) dst[i] = src[i];
        //    return;
        // }

        // using System.arraycopy() is a bit faster than the above loop
        if (!less(src[mid+1], src[mid])) {
            System.arraycopy(src, lo, dst, lo, hi - lo + 1);
            return;
        }

        merge(src, dst, lo, mid, hi);
    }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a) {
        Comparable[] aux = a.clone();
        sort(aux, a, 0, a.length-1);
        assert isSorted(a);
    }


    // sort from a[lo] to a[hi] using insertion sort
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j-1]); j--)
                exch(a, j, j-1);
    }

}
