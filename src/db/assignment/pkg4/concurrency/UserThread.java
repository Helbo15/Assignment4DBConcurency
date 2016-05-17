/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.assignment.pkg4.concurrency;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen
 */
public class UserThread implements Runnable {

    private Reservation _objReservation;
    private PassengerIDCounterHolder _count;

    public UserThread()
    {
        this(new PassengerIDCounterHolder());
    }

    UserThread(PassengerIDCounterHolder ids)
    {
        _count = ids;
        _objReservation = new Reservation("db_036", "db2016");
    }

    @Override
    public void run()
    {

        while (!_objReservation.isAllReserved("CR9"))
        {
            int currentPassengerID = _count.getIdCount();
            String seatNo = _objReservation.reserve("CR9", currentPassengerID);
            int waittime = ThreadLocalRandom.current().nextInt(1, 11);
            try
            {
                Thread.sleep(waittime * 1000);                 //1000 milliseconds is one second.
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            _objReservation.book("CR9", seatNo, currentPassengerID);
        }
        // _objReservation.closeConn();
    }
}
