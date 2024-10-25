package model.world;

import dbUtils.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class DbMods {
    /*
     * Returns a "StringData" object that is full of field level validation
     * error messages (or "" for any field that passes validation).
     */
    private static StringData validate(StringData inputData) {

        StringData errorMsgs = new StringData();

        
        // Validation
        
        errorMsgs.worldId = Validate.integerMsg(inputData.worldId, true);
        errorMsgs.worldName = Validate.stringMsg(inputData.worldName, 45, true);
        errorMsgs.worldDescription = Validate.stringMsg(inputData.worldDescription, 300, true);

        errorMsgs.worldIcon = Validate.stringMsg(inputData.worldIcon, 300, false);
        errorMsgs.worldMagicSystem = Validate.stringMsg(inputData.worldMagicSystem, 300, false);
        errorMsgs.worldInspiration = Validate.stringMsg(inputData.worldInspiration, 300, false);
        errorMsgs.worldDonations = Validate.decimalMsg(inputData.worldDonations, false);
        errorMsgs.worldEnteredOrUpdated = Validate.dateMsg(inputData.worldEnteredOrUpdated, false);
        
        errorMsgs.webUserId = Validate.integerMsg(inputData.webUserId, true);
        
        

        return errorMsgs;
    } // validate

    public static StringData getById(DbConn dbc, String id) {
        StringData sd = new StringData();
        // This case already tested in the controller, but ("belt and suspenders")
        // we are double checking here as well.
        if (id == null) {
            sd.errorMsg = "Cannot getById (user): id is null";
            return sd;
        }

        Integer intId;
        try {
            intId = Integer.valueOf(id);
        } catch (Exception e) {
            sd.errorMsg = "Cannot getById (user): URL parameter 'id' can't be converted to an Integer.";
            return sd;
        }
        try {
            String sql = "SELECT world_id, world.web_user_id, world_name, world_description, world_icon, world_inspiration, "
                    + "world_magic_system, world_donations, world_entered_or_updated, web_user.user_email "
                    + "FROM world, web_user WHERE world.web_user_id = web_user.web_user_id  "
                    + "AND world_id = ?";
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

            // Encode the id (that the user typed in) into the select statement, into the
            // the first (and only) ?
            stmt.setInt(1, intId);

            ResultSet results = stmt.executeQuery();
            if (results.next()) { // id is unique, one or zero records expected in result set

                // plainInteger returns integer converted to string with no commas.
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

            } else {
                sd.errorMsg = "World Not Found.";
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.world.DbMods.getById(): " + e.getMessage();
        }
        return sd;
    } // getById

    public static StringData insert(StringData inputData, DbConn dbc) {

        StringData errorMsgs = new StringData();
        errorMsgs = validate(inputData);
        if (errorMsgs.characterCount() > 0) { // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /*
             * String sql =
             * "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "+
             * "web_user.user_role_id, user_role_type "+
             * "FROM web_user, user_role where web_user.user_role_id = user_role.user_role_id "
             * +
             * "ORDER BY web_user_id ";
             */
            // Start preparing SQL statement
            String sql = "INSERT INTO world (world_id, world_name, world_description, world_icon, world_magic_system, " +
                    "world_inspiration, world_donations, world_entered_or_updated, web_user_id) values (?,?,?,?,?,?,?,?,?)";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setInt(1, Validate.convertInteger(inputData.worldId)); // string type is simple
            pStatement.setString(2, inputData.worldName);
            pStatement.setString(3, inputData.worldDescription);
            pStatement.setString(4, inputData.worldIcon);
            pStatement.setString(5, inputData.worldMagicSystem);
            pStatement.setString(6, inputData.worldInspiration);
            pStatement.setBigDecimal(7, Validate.convertDecimal(inputData.worldDonations));
            pStatement.setDate(8, Validate.convertDate(inputData.worldEnteredOrUpdated));
            pStatement.setInt(9, Validate.convertInteger(inputData.webUserId));
            

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to
                                             // the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk
                    // sql update.
                    errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                }
            } else if (errorMsgs.errorMsg.contains("foreign key")) {
                errorMsgs.errorMsg = "Invalid Web Id - " + errorMsgs.errorMsg;
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That World Name is already taken - " + errorMsgs.errorMsg;
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // insert

    public static StringData update(StringData updateData, DbConn dbc) {

        StringData errorMsgs = new StringData();
        errorMsgs = validate(updateData);

        // For update, we also need to check that webUserId has been supplied by the user...
        errorMsgs.webUserId = Validate.integerMsg(updateData.webUserId, true);

        if (errorMsgs.characterCount() > 0) { // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /* Useful to know the exact field names in the database... 
             * String sql =
             * "SELECT web_user_id, user_email, user_password, user_image, membership_fee, "
             * "birthday, web_user.user_role_id, user_role_type "+
             * "FROM web_user, user_role where web_user.user_role_id = user_role.user_role_id "
             * "ORDER BY web_user_id ";
             */

            String sql = "UPDATE world SET world_name = ?, world_description = ?, world_icon = ?, world_inspiration = ?, world_magic_system = ?, world_donations = ?, world_entered_or_updated = ? "
                        + "WHERE world_id = ?";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            
            pStatement.setString(1, updateData.worldName);
            pStatement.setString(2, updateData.worldDescription);
            pStatement.setString(3, updateData.worldIcon);
            pStatement.setString(4, updateData.worldMagicSystem);
            pStatement.setString(5, updateData.worldInspiration);
            pStatement.setBigDecimal(6, Validate.convertDecimal(updateData.worldDonations));
            pStatement.setDate(7, Validate.convertDate(updateData.worldEnteredOrUpdated));
            pStatement.setInt(8, Validate.convertInteger(updateData.worldId)); 

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to
                                             // the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk
                    // sql update OR the web User id (supplied by the client side) does not exist.
                    errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                }
            } else if (errorMsgs.errorMsg.contains("foreign key")) {
                errorMsgs.errorMsg = "Invalid Id - " + errorMsgs.errorMsg;
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That entry is already taken - " + errorMsgs.errorMsg; // shouldn't be seen due to how the world DB works?
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // update

    public static StringData delete(DbConn dbc, String userId) {

        StringData sd = new StringData();


        sd.errorMsg = dbc.getErr();
        if (sd.errorMsg.length() > 0) { // cannot proceed, db error
            return sd;
        }

        try {

            String sql = "DELETE FROM world WHERE world_id = ?";

            // Compile the SQL (checking for syntax errors against the connected DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, userId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                sd.errorMsg = "Record not deleted - there was no record with world_id " + userId;
            } else if (numRowsDeleted > 1) {
                sd.errorMsg = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
        }

        return sd;
    } // delete

}
