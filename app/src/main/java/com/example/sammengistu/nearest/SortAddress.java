package com.example.sammengistu.nearest;

import com.example.sammengistu.nearest.models.Address;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SortAddress {

    public static final int FIRST_ITEM_NUMBER = 0;
    private static final int DAY_IN_MIN = 1440;
    private static final int HOUR_IN_MIN = 60;
    private static final int WORD_IN_FRONT = 1;

    public static final String TAG = "SortAddress";

    public static List<Address> sortAddresses(final List<Address> addressList,
                                              final boolean sortDistance) {
        List<Address> addressesSorted = new ArrayList<>(addressList);

        for (Address address : addressList) {
            Log.i(TAG, "address = " + address.getFullAddress());
            Log.i(TAG, "Length of commute in min = "
                + convertCommuteTimeToNumber(address.getCommuteTime().split("\\s")));
        }

        Collections.sort(addressesSorted, new Comparator<Address>() {
            @Override
            public int compare(Address lhs, Address rhs) {
                String firstAddress;
                String secondAddress;

                if (sortDistance) {
                    firstAddress = lhs.getDistance().split("\\s")[FIRST_ITEM_NUMBER];
                    secondAddress = rhs.getDistance().split("\\s")[FIRST_ITEM_NUMBER];
                } else {

                    firstAddress = convertCommuteTimeToNumber(lhs.getCommuteTime().split("\\s"));
                    secondAddress = convertCommuteTimeToNumber(rhs.getCommuteTime().split("\\s"));

                }

                firstAddress = firstAddress.replaceAll(",", "");
                secondAddress = secondAddress.replaceAll(",", "");

                Double doub = Double.parseDouble(firstAddress);
                Double double2 = Double.parseDouble(secondAddress);

                return doub.compareTo(double2);
            }
        });

        return addressesSorted;
    }

    private static String convertCommuteTimeToNumber(String[] commuteInfoInWords) {

        List<Integer> integerList = new ArrayList<>();

        for (int i = 0; i < commuteInfoInWords.length; i++) {
            if (isInteger(commuteInfoInWords[i])) {
                if (!commuteInfoInWords[i + WORD_IN_FRONT].equals("mins") ||
                    !commuteInfoInWords[i + WORD_IN_FRONT].equals("mins")) {

                    int integerOfTime = Integer.parseInt(commuteInfoInWords[i]);
                    int wordIntValue = convertTimeValueToMin(commuteInfoInWords[i + 1]);

                    integerList.add(integerOfTime * wordIntValue);
                } else {
                    integerList.add(Integer.parseInt(commuteInfoInWords[i]));
                }
            }
        }

        int commuteTimeInMin = 0;
        for (Integer value : integerList) {
            commuteTimeInMin += value;
        }
        return Integer.toString(commuteTimeInMin);
    }

    private static boolean isInteger(String checkIfNumber) {
        try {
            Integer.parseInt(checkIfNumber);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static int convertTimeValueToMin(String timeValue) {

        switch (timeValue) {
            case "day":
                return DAY_IN_MIN;
            case "days":
                return DAY_IN_MIN;
            case "hour":
                return HOUR_IN_MIN;
            case "hours":
                return HOUR_IN_MIN;
            default:
                return 0;
        }
    }
}
