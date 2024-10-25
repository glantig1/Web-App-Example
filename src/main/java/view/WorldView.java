package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.world.*;
import dbUtils.*;

public class WorldView {

    public static StringDataList getAllWorld(DbConn dbc) {

        // sdl will be an empty array and DbError with "" 
        StringDataList sdl = new StringDataList(); 

        sdl.dbError = dbc.getErr(); // returns "" if connection is good, else db error msg.
        if (sdl.dbError.length() > 0) {
            return sdl; // cannot proceed, db error (and that's been recorded in return object).
        }
        
        // sd will have all of it's fields initialized to ""
        StringData sd = new StringData();
        
        try {
            String sql = "SELECT world_id, world.web_user_id, world_name, world_description, world_icon, world_inspiration, world_magic_system, world_donations, world_entered_or_updated, web_user.user_email "
                    + "FROM world, web_user "
                    + "WHERE world.web_user_id = web_user.web_user_id "
                    + "ORDER BY world.web_user_id";  // always order by something, not just random order.
            
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);
            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                
                sd = new StringData();
                
                // the Format methods do not throw exceptions. If they find illegal data (like you 
                // tried to format a date as an integer), they return an error message (instead of 
                // returning the formatted value). So, you'll see these error messages right in the 
                // API output (JSON data) and/or you'll see it on the page in the UI.

                sd.worldId = Format.fmtInteger(results.getObject("world_id"));
                sd.worldName = Format.fmtString(results.getObject("world_name"));
                sd.worldDescription = Format.fmtString(results.getObject("world_description"));
                sd.worldIcon = Format.fmtString(results.getObject("world_icon"));
                sd.worldInspiration = Format.fmtString(results.getObject("world_inspiration"));
                sd.worldMagicSystem = Format.fmtString(results.getObject("world_magic_system"));
                sd.worldDonations = Format.fmtDollar(results.getObject("world_donations"));
                sd.worldEnteredOrUpdated = Format.fmtDate(results.getObject("world_entered_or_updated"));
                sd.webUserId = Format.fmtInteger(results.getObject("world.web_user_id"));
                sd.userEmail = Format.fmtString(results.getObject("web_user.user_email"));

                sdl.add(sd);
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in WorldView.getAllWorld(): " + e.getMessage();
            sdl.add(sd);
        }
        return sdl;
    }
}
