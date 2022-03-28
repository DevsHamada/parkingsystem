package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private Ticket ticket;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseConfig dataBaseConfig;

    @BeforeAll
    private static void setUp () {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBaseConfig = new DataBaseTestConfig();
        dataBasePrepareService = new DataBasePrepareService();
        dataBaseTestConfig = new DataBaseTestConfig();
    }

    @BeforeEach
    private void setUpPerTest (){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void saveTicketValid () {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,true);
        ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (40*60*1000)));

        assertTrue(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void saveTicketError () {
       // NULL for objet ticket
        ticket = new Ticket();

        assertFalse(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void getTicketFirst() {

        saveTicketValid();
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");

        assertEquals(1, ticketOut.getId());
    }
    @Test
    public void getTicketError() {
        Ticket ticketOut = ticketDAO.getTicket("ABCDEF");

        assertNull(ticketOut);
    }
    @Test
    public void updateTicketOk() {

        saveTicketValid();
        ticket.setPrice(1.5);
        ticket.setOutTime(new Date(System.currentTimeMillis()));

        assertTrue(ticketDAO.updateTicket(ticket));
    }
    @Test
    public void updateTicketError() {
        ticket = new Ticket();
        assertFalse(ticketDAO.updateTicket(ticket));
    }
    @Test
    public void getVehicleRecurringOk() {
        saveTicketValid();

        assertTrue(ticketDAO.getVehicleRecurringIn("ABCDEF"));
    }
    @Test
    public void getVehicleRecurringInError() {
        saveTicketValid();
        assertFalse(ticketDAO.getVehicleRecurringIn("DDDD"));
    }
    @Test
    public void getVehicleRecurringOutInOk() {
        saveTicketValid();

        assertEquals(1, ticketDAO.getVehicleRecurringOut("ABCDEF"));
    }
    @Test
    public void getVehicleRecurringOutError() {
        assertEquals(0, ticketDAO.getVehicleRecurringOut("DDDD"));

    }
}