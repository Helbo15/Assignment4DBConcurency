/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.assignment.pkg4.concurrency;

import db.assignment.pkg4.concurrency.Reservation.BookingResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Steffen
 */
public class ReservationTest {

    private List<String> ExpectedS = new ArrayList<String>() {
        {
            add("A1");
            add("C1");
            add("D1");
            add("F1");
            add("A2");
            add("C2");
            add("D2");
            add("F2");
            add("A3");
            add("C3");
            add("D3");
            add("F3");
            add("A4");
            add("C4");
            add("D4");
            add("F4");
            add("A5");
            add("C5");
            add("D5");
            add("F5");
            add("A6");
            add("C6");
            add("D6");
            add("F6");
            add("A7");
            add("C7");
            add("D7");
            add("F7");
            add("A8");
            add("C8");
            add("D8");
            add("F8");
            add("A9");
            add("C9");
            add("D9");
            add("F9");
            add("A10");
            add("C10");
            add("D10");
            add("F10");
            add("A11");
            add("C11");
            add("D11");
            add("F11");
            add("A12");
            add("C12");
            add("D12");
            add("F12");
            add("A13");
            add("C13");
            add("D13");
            add("F13");
            add("A14");
            add("C14");
            add("D14");
            add("F14");
            add("A15");
            add("C15");
            add("D15");
            add("F15");
            add("A16");
            add("C16");
            add("D16");
            add("F16");
            add("A17");
            add("C17");
            add("D17");
            add("F17");
            add("A18");
            add("C18");
            add("D18");
            add("F18");
            add("A19");
            add("C19");
            add("D19");
            add("F19");
            add("A20");
            add("C20");
            add("D20");
            add("F20");
            add("A21");
            add("C21");
            add("D21");
            add("F21");
            add("A22");
            add("C22");
            add("D22");
            add("F22");
            add("A23");
            add("C23");
            add("D23");
            add("F23");
            add("A24");
            add("C24");
            add("D24");
            add("F24");
        }
    };
    private Reservation instance;
    private final String plane_no = "CR9";

    public ReservationTest()
    {

    }

    @BeforeClass
    public static void setUpClass()
    {
         
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
       //to clean up the DB in case the main multithreaded program actually have ran since last time I ran this test
       instance = new Reservation("db_036", "db2016");
       instance.clearAllBookings(plane_no);
       
       instance = new Reservation("db_036", "db2016");
    }

    @After
    public void tearDown()
    {
        instance.clearAllBookings(plane_no);
    }

    @Test
    public void testReserve()
    {
        System.out.println("reserve");
        long id = 1L;
        String result = instance.reserve(plane_no, id);
        assertTrue(ExpectedS.contains(result));
    }

    @Test
    public void testBook()
    {
        System.out.println("book");
        long id = 1L;
        String seat_no = instance.reserve(plane_no, id);
        int expResult = BookingResult.Success.getValue();
        int result = instance.book(plane_no, seat_no, id);
        assertEquals(expResult, result);
    }

}
