/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.assignment.pkg4.concurrency;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen
 */
public class MasterThread {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        Reservation objResevation = new Reservation("db_036", "db2016");
        PassengerIDCounterHolder ids = new PassengerIDCounterHolder();
            for(int i = 0;i<9;i++)
            {
                executor.execute(new UserThread(ids));
            }
        while(!objResevation.isAllBooked("CR9"))
        {
            try
            {
                //wait untill all is booked
                Thread.sleep(1000*5);                 //1000 milliseconds is one second.
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
        
        
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        try
        {
            // Wait until all threads are finish
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
        objResevation.closeConn();
        System.out.println("Finished all threads");

    }
}
