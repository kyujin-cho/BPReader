package com.hanyang.bpreader;
/**
 * Created by kyujin on 2016-08-18.
 */
public class ParseDataEnum {
      // Descriptions about parsed data
    static final String[] DESC = {
            "Format Code",
            "PASS",
            "Name",
            "PASS",
            "Ticket PNR Code",
            "Departure Airport",
            "Arrival Airport",
            "Operating Carrier",
            "Flight Number",
            "Flight Date",
            "Seat Class",
            "Seat No.",
            "Check-In Seq. Number",
            "Passenger Status",
            "PASS",
            "PASS",
            "PASS",
            "PASS",
            "Passenger Desc.",
            "Source of Check-In",
            "Source of BP Insurance",
            "Date of Issue of BP",
            "Document Type",
            "Airline Designator of BP Issuer",
            "Baggage Tag License Plate Number(s)",
            "PASS",
            "Airline Numeric Code",
            "Document Form/Serial Number",
            "Selectee Indicator",
            "International Documentation Verification",
            "Marketing Carrier",
            "Frequent Flier Carrier",
            "FF Number",
            "ID/AD Indicator",
            "Free Baggage Allowance"
    };
    // Locations of each datas defined above
    static final int[] CURSOR = {1,1,20,1,7,3,3,3,5,3,1,4,5,1,2,1,1,2,1,1,1,4,1,3,13,2,3,10,1,1,3,3,16,1,3};
}
