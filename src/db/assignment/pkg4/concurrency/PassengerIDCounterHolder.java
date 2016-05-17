/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.assignment.pkg4.concurrency;

/**
 *
 * @author Steffen
 */
public class PassengerIDCounterHolder {
    private int _idCount;
    public PassengerIDCounterHolder()
    {
        _idCount =1;
    }

    /**
     * @return the Next Passenger ID available
     */
    public int getIdCount()
    {
       
        return _idCount++;
    }    
}
