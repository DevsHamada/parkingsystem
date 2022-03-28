package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        //TODO: MZ1603

        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        long duration = outHour.getTime() - inHour.getTime();
        double parkingTime = TimeUnit.MILLISECONDS.toMinutes(duration);
        double parkingTimeHours =  (parkingTime / 60);
        if (ticket.isDiscount()) {
            parkingTimeHours = (parkingTimeHours * Fare.DISCOUNT_VEHICLE);
        }
        if (parkingTimeHours < 0.5) {
            ticket.setPrice(0.0);
        }
        else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(parkingTimeHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(parkingTimeHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}