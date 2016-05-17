/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.assignment.pkg4.concurrency;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

;

/**
 *
 * @author Steffen
 */
public class Reservation {

    public enum BookingResult {
        Success(0), NotReserved(-1), NotReservedByCustomerId(-2), ReservationTimeOut(-3), Taken(-4), Error(-5);

        private int value;

        private BookingResult(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    };

    private Connection conn = null;

    public Reservation(String user, String pw)
    {
        try
        {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            String URL = "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:DAT";
            Properties info = new Properties();
            info.put("user", user);
            info.put("password", pw);
            conn = DriverManager.getConnection(URL, info);
            conn.setAutoCommit(false);

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            this.closeConn();
        }
    }

    public String reserve(String plane_no, long id)
    {
        ResultSet result = null;
        String sql = "SELECT * FROM Seat where PLANE_NO =?";
        String sql1 = "UPDATE Seat SET Reserved =?, Booking_Time=?  WHERE Seat_No=? and PLANE_NO=?";

        try
        {
            
            PreparedStatement statement = conn.prepareStatement(sql);
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            String seatNo = null;

            statement.setString(1, plane_no);
            result = statement.executeQuery();

            int count = 0;

            while (result.next())
            {
                seatNo = result.getString("Seat_No");
                long booked = result.getLong("booked");
                long bookingTime = result.getLong("Booking_Time");
                long bookingTimelimit = bookingTime + 7L;
                if (bookingTime == 0 || Instant.now().getEpochSecond() > bookingTimelimit)
                {
                    
                    if (booked == 0)
                    {
                        statement1.setLong(1, id);
                        statement1.setLong(2, Instant.now().getEpochSecond());
                        statement1.setString(3, seatNo);
                        statement1.setString(4, plane_no);

                        int rowsUpdated = statement1.executeUpdate();
                        if ((rowsUpdated > 1))
                        {
                            result.close();
                            conn.rollback();
                            return null;
                        }
                        break;
                    }

                }

                count++;
            }
            if (count > 96)
            {
                
                result.close();
                conn.rollback();
                return null;
            }
            conn.commit();
            return seatNo;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        try
        {
            
            result.close();
            conn.rollback();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    //enum instead of int ?     
    public int book(String plane_no, String seat_no, long id)
    {
        String sql = "SELECT * FROM Seat where PLANE_NO =? and Seat_No=?";
        String sql1 = "UPDATE Seat SET BOOKED =?  WHERE PLANE_NO =? and Seat_No=?";

        ResultSet result = null;
        try
        {

            PreparedStatement statement = conn.prepareStatement(sql);
            PreparedStatement statement1 = conn.prepareStatement(sql1);

            statement.setString(1, plane_no);
            statement.setString(2, seat_no);
            result = statement.executeQuery();
            if (result.next())
            {
                long reserved = result.getLong("Reserved");
                long booked = result.getLong("booked");
                long bookingTime = result.getLong("Booking_Time");
                long bookingTimelimit = bookingTime + 7L;
                if (booked == 0)
                {
                    if (reserved == id)
                    {
                        if (!(Instant.now().getEpochSecond() < bookingTimelimit))
                        {
                            return BookingResult.ReservationTimeOut.getValue();
                        }

                        statement1.setLong(1, id);
                        statement1.setString(2, plane_no);
                        statement1.setString(3, seat_no);

                        int rowsUpdated = statement1.executeUpdate();
                        if ((rowsUpdated > 0))
                        {
                            conn.commit();
                            result.close();
                            return BookingResult.Success.getValue();
                        }
                    }
                    else if (reserved == 0)
                    {
                        result.close();
                        conn.rollback();
                        return BookingResult.NotReserved.getValue();
                    }
                    else
                    {
                        result.close();
                        conn.rollback();
                        return BookingResult.NotReservedByCustomerId.getValue();
                    }

                }
                else
                {
                    result.close();
                    conn.rollback();
                    return BookingResult.Taken.getValue();
                }

            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        try
        {
            conn.rollback();
            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return BookingResult.Error.getValue();
    }

    public void bookAll(String plane_no)
    {
//        try
//        {
//            String sql = "SELECT * FROM Seat where PLANE_NO =?";
//            PreparedStatement statement = conn.prepareStatement(sql);
//            statement.setString(1, plane_no);
//            ResultSet result = statement.executeQuery();
//            long id = 1;
//            while (result.next())
//            {
        for (int i = 1; i < 97; i++)
        {
            this.book(plane_no, this.reserve(plane_no, i), i);
        }
//            }
//        }
//        catch (SQLException ex)
//        {
//            ex.printStackTrace();
//        }
    }

    public void clearAllBookings(String plane_no)
    {
        try
        {
            String sql = "SELECT * FROM Seat where PLANE_NO =?";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, plane_no);
            ResultSet result = statement.executeQuery();
            String sql1 = "UPDATE Seat SET Booked =? ,Reserved =? ,Booking_Time =?    WHERE PLANE_NO =? and Seat_No=?";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            while (result.next())
            {

                statement1.setNull(1, java.sql.Types.NUMERIC);
                statement1.setNull(2, java.sql.Types.NUMERIC);
                statement1.setNull(3, java.sql.Types.NUMERIC);
                statement1.setString(4, plane_no);
                statement1.setString(5, result.getString("Seat_No"));

                statement1.executeUpdate();

            }
            conn.commit();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            try
            {
                conn.rollback();
            }
            catch (SQLException ex1)
            {
                ex.printStackTrace();
            }
            this.closeConn();
        }
        
        this.closeConn();
    }

    public boolean isAllBooked(String plane_no)
    {
        try
        {
            String sql = "SELECT BOOKED FROM Seat where PLANE_NO =? and BOOKED is null";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, plane_no);
            ResultSet result = statement.executeQuery();
            int count = 0;
            while (result.next())
            {
                if (result.getLong("Booked") == 0)
                {
                    break;
                }
                count++;
            }
            if (count == 0)
            {
                return true;
            }
            return false;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return false;

    }

    public void closeConn()
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                conn.close();
            }
        }
        catch (SQLException ex1)
        {
            ex1.printStackTrace();
        }
    }

    public boolean isAllReserved(String plane_no)
    {
        try
        {
            String sql = "SELECT RESERVED FROM Seat where PLANE_NO =? and RESERVED is null";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, plane_no);
            ResultSet result = statement.executeQuery();
            int count = 0;
            while (result.next())
            {
                if (result.getLong("RESERVED") == 0)
                {
                    break;
                }
                count++;
            }
            if (count == 96)
            {
                return true;
            }
            return false;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return true;
        }
    }
}
