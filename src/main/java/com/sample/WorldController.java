package com.sample;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.world.*;
import dbUtils.*;
import view.WorldView;

@RestController
public class WorldController {

    @RequestMapping(value = "/world/getAll", produces = "application/json")
    public String allUsers() {

        StringDataList list = new StringDataList(); // dbError empty, list empty
        DbConn dbc = new DbConn();
        list = WorldView.getAllWorld(dbc);
    
        dbc.close(); // EVERY code path that opens a db connection must close it
                     // (or else you have a database connection leak).

        return Json.toJson(list); // convert sdl obj to JSON Format and return that.
    }

    @RequestMapping(value = "/world/insert", params = { "jsonData" }, produces = "application/json")
    public String insert(@RequestParam("jsonData") String jsonInsertData) {

        StringData errorMsgs = new StringData();

        if ((jsonInsertData == null) || jsonInsertData.length() == 0) {
            errorMsgs.errorMsg = "Cannot insert. No user data was provided in JSON format";
        } else {
            System.out.println("user data for insert (JSON): " + jsonInsertData);
            try {
                ObjectMapper mapper = new ObjectMapper();
                StringData insertData = mapper.readValue(jsonInsertData, StringData.class);
                System.out.println("user data for insert (java obj): " + insertData.toString());

                DbConn dbc = new DbConn();
                errorMsgs.errorMsg = dbc.getErr();
                if (errorMsgs.errorMsg.length() == 0) { // means db connection OK

                    
                    
                    errorMsgs.worldName = Validate.stringMsg(insertData.worldName, 45, true);
                    errorMsgs.worldDescription = Validate.stringMsg(insertData.worldDescription, 300, true);
                    errorMsgs.worldIcon = Validate.stringMsg(insertData.worldIcon, 300, false);
                    errorMsgs.worldMagicSystem = Validate.stringMsg(insertData.worldMagicSystem, 300, false);
                    errorMsgs.worldInspiration = Validate.stringMsg(insertData.worldInspiration, 300, false);
                    errorMsgs.worldDonations = Validate.decimalMsg(insertData.worldDonations, false);
                    errorMsgs.worldEnteredOrUpdated = Validate.dateMsg(insertData.worldEnteredOrUpdated, false);
        
                    errorMsgs.webUserId = Validate.integerMsg(insertData.webUserId, true);
                    

                    if (errorMsgs.characterCount() > 0) { // at least one field has an error, don't go any further.
                        errorMsgs.errorMsg = "Please try again";

                    } else { // all fields passed validation

                        // Start preparing SQL statement
                        String sql = "INSERT INTO world (world_name, world_description, world_icon, world_magic_system, " +
                                    "world_inspiration, world_donations, world_entered_or_updated, web_user_id) values (?,?,?,?,?,?,?,?)";

                        // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
                        // Only difference is that Sally's class takes care of encoding null
                        // when necessary. And it also System.out.prints exception error messages.
                        PrepStatement pStatement = new PrepStatement(dbc, sql);

                        // Encode string values into the prepared statement (wrapper class).
                        
                        pStatement.setString(1, insertData.worldName);
                        pStatement.setString(2, insertData.worldDescription);
                        pStatement.setString(3, insertData.worldIcon);
                        pStatement.setString(4, insertData.worldMagicSystem);
                        pStatement.setString(5, insertData.worldInspiration);
                        pStatement.setBigDecimal(6, Validate.convertDecimal(insertData.worldDonations));
                        pStatement.setDate(7, Validate.convertDate(insertData.worldEnteredOrUpdated));
                        pStatement.setInt(8, Validate.convertInteger(insertData.webUserId));
                        

                        // here the SQL statement is actually executed
                        int numRows = pStatement.executeUpdate();

                        // This will return empty string if all went well, else all error messages.
                        errorMsgs.errorMsg = pStatement.getErrorMsg();
                        if (errorMsgs.errorMsg.length() == 0) {
                            if (numRows == 1) {
                                errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to
                                                         // tell this to the user.
                            } else {
                                // probably never get here unless you forgot your WHERE clause and did a bulk
                                // sql update.
                                errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                            }
                        } else if (errorMsgs.errorMsg.contains("foreign key")) {
                            errorMsgs.errorMsg = "Invalid User Role Id";
                        } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                            errorMsgs.errorMsg = "That key is already taken";
                        }

                    } // all fields passed validation

                } // DB connection OK
                dbc.close();
            } catch (Exception e) {
                String msg = "Could not convert jsonData to model.webUser.StringData obj: "+
                jsonInsertData+ " - or other error in controller for 'user/insert': " +
                        e.getMessage();
                System.out.println(msg);
                errorMsgs.errorMsg += ". " + msg;
            }
        }
        return Json.toJson(errorMsgs);
    }
    @RequestMapping(value = "/world/getById", params = {"worldId" }, produces = "application/json")
    public String getById(@RequestParam("worldId") String worldId) {
        StringData sd = new StringData();
        if (worldId == null) {
            sd.errorMsg = "Error: URL must be user/getById/xx " +
                    "where xx is the web_user_id of the desired web_user record.";
        } else {
            DbConn dbc = new DbConn();
            sd.errorMsg = dbc.getErr();
            if (sd.errorMsg.length() == 0) {
                System.out.println("*** Ready to call DbMods.getById");
                sd = DbMods.getById(dbc, worldId);
            }
            dbc.close(); // EVERY code path that opens a db connection must close it
            // (or else you have a database connection leak).
        }
        return Json.toJson(sd);
    }
    @RequestMapping(value = "/world/update", params = { "jsonData" }, produces = "application/json")
    public String update(@RequestParam("jsonData") String jsonInsertData) {

        StringData errorData = new StringData();

        if ((jsonInsertData == null) || jsonInsertData.length() == 0) {
            errorData.errorMsg = "Cannot update. No user data was provided in JSON format";
        } else {
            System.out.println("user data for update (JSON): " + jsonInsertData);
            try {
                ObjectMapper mapper = new ObjectMapper();
                StringData updateData = mapper.readValue(jsonInsertData, StringData.class);
                System.out.println("user data for update (java obj): " + updateData.toString());

                // The next 3 statements handle their own exceptions (so should not throw any
                // exception).
                DbConn dbc = new DbConn();
                errorData = DbMods.update(updateData, dbc);
                dbc.close();
            } catch (Exception e) {
                String msg = "Unexpected error in controller for 'world/update'... " +
                        e.getMessage();
                System.out.println(msg);
                errorData.errorMsg = msg;
            }
        }
        return Json.toJson(errorData);
    }

    @RequestMapping(value = "/world/delete", params = {"worldId" }, produces = "application/json")
    public String delete(@RequestParam("worldId") String deleteWorldId) {
        StringData sd = new StringData();
        
        DbConn dbc = new DbConn();
        sd = DbMods.delete(dbc, deleteWorldId);
        dbc.close(); // EVERY code path that opens a db connection must close it
         
        return Json.toJson(sd);
    }
}