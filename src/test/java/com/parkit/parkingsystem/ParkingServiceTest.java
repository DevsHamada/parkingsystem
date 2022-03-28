package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processIncomingVehicleTest() throws Exception {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        // choose car
        when(inputReaderUtil.readSelection()).thenReturn(2);
        //  ticket record
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        // retrieve the next available place in the database
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        parkingService.processIncomingVehicle();
        // the parking is updated & ticket is created
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    public void processExitingVehicleTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //  ticket record
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        // retrieve the ticket
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        // Update parking is OK
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        // Exit car
        parkingService.processExitingVehicle();

        // the parking is updated & ticket is updated
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }
    @Test
    public void throwParkingServiceException_processIncomingVehicleKO() throws Exception {

        // error choose (1 : car / 2 : bike /<> error)
        when(inputReaderUtil.readSelection()).thenReturn(3);

        parkingService.processIncomingVehicle();

        // the parking is not updated & ticket is  not created
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    public void throwParkingServiceException_processExitingVehicle_ParkingNumberKO() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception());

        parkingService.processExitingVehicle();

        // the parking is not updated & ticket is  not updated
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }
    @Test
    public void getNextParkingNumberIfAvailableKO () throws Exception {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //Parkingspot is null
        assertNull(parkingSpot);
    }
    @Test
    public void getNextParkingNumberIfAvailableException  () throws Exception {

        when(inputReaderUtil.readSelection()).thenReturn(3);


        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //Parkingspot is null
        assertNull(parkingSpot);
    }
    @Test
    public void processExitingVehicleErrorMsg  () throws Exception {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //  ticket record is not ok
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        // retrieve the ticket
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        // Exit car

        parkingService.processExitingVehicle();

        // ERROR MSG "Unable to update ticket information. Error occurred"
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));

    }

}
