package model.webUser;

import dbUtils.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class DbMods {

    private static StringData validate(StringData inputData) {
        //System.out.println("Validate: user input data is "+inputData.toString());


        StringData errorMsgs = new StringData();

        /*
         * Useful to copy field names from StringData as a reference
         * public String webUserId = "";
         * public String userEmail = "";
         * public String userPassword = "";
         * public String userPassword2 = "";
         * public String userImage = "";
         * public String birthday = "";
         * public String membershipFee = "";
         * public String userRoleId = ""; // Foreign Key
         * public String userRoleType = ""; // joined from user_role table
         */
        // Validation
        errorMsgs.userEmail = Validate.stringMsg(inputData.userEmail, 45, true);
        errorMsgs.userPassword = Validate.stringMsg(inputData.userPassword, 45, true);

        if (inputData.userPassword.compareTo(inputData.userPassword2) != 0) { // case sensative comparison
            errorMsgs.userPassword2 = "Both passwords must match";
        }

        errorMsgs.userImage = Validate.stringMsg(inputData.userImage, 300, false);

        errorMsgs.birthday = Validate.dateMsg(inputData.birthday, false);
        errorMsgs.membershipFee = Validate.decimalMsg(inputData.membershipFee, false);
        errorMsgs.userRoleId = Validate.integerMsg(inputData.userRoleId, true);

        //System.out.println("Validate: error Msg object is "+errorMsgs.toString());

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

        sd.errorMsg = dbc.getErr(); 
        if (sd.errorMsg.length() > 0) { // cant proceed, database error
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
            String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "
                    + "user_image, web_user.user_role_id, user_role_type "
                    + "FROM web_user, user_role WHERE web_user.user_role_id = user_role.user_role_id "
                    + "AND web_user_id = ?";
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

            // Encode the id (that the user typed in) into the select statement, into the
            // the first (and only) ?
            stmt.setInt(1, intId);

            ResultSet results = stmt.executeQuery();
            if (results.next()) { // id is unique, one or zero records expected in result set

                // plainInteger returns integer converted to string with no commas.
                sd.webUserId = Format.fmtInteger(results.getObject("web_user_id"));
                sd.userEmail = Format.fmtString(results.getObject("user_email"));
                sd.userPassword = Format.fmtString(results.getObject("user_password"));
                sd.userImage = Format.fmtString(results.getObject("user_image"));
                sd.birthday = Format.fmtDate(results.getObject("birthday"));
                sd.membershipFee = Format.fmtDollar(results.getObject("membership_fee"));
                sd.userRoleId = Format.fmtInteger(results.getObject("web_user.user_role_id"));
                sd.userRoleType = Format.fmtString(results.getObject("user_role_type"));

            } else {
                sd.errorMsg = "Web User Not Found.";
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.webUser.DbMods.getById(): " + e.getMessage();
        }
        return sd;
    } // getById

    public static StringData getMisc(DbConn dbc, String minMemberFee, String maxBirthday) {

        StringData sd = new StringData();
    
        sd.errorMsg = dbc.getErr();

    
        // fixed 3/14/24. before was trying to pass maxBirthday into convertDecimal
        BigDecimal bdMinMemberFee = Validate.convertDecimal(minMemberFee);
        if (bdMinMemberFee == null) {
            sd.errorMsg += "Cannot get Misc user: can't convert minMemberFee to a BigDecimal.";
        }
    
        java.sql.Date dtMaxBirthday = Validate.convertDate(maxBirthday);
        if (dtMaxBirthday == null) {
            sd.errorMsg += "  Cannot get Misc User: can't convert maxBirthday to a Date.";
        }
    
        if (sd.errorMsg.length() > 0) {
            return sd; // db error or user inputs cannot be converted to correct types.
        }
        
        // you fill in the code.
        try {
            String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "
                    + "user_image, web_user.user_role_id, user_role_type "
                    + "FROM web_user, user_role WHERE web_user.user_role_id = user_role.user_role_id "
                    + "AND membership_fee >= ? " 
                    + "AND birthday <= ?";
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

            // Encode the id (that the user typed in) into the select statement, into the
            // the first (and only) ?
            stmt.setBigDecimal(1, bdMinMemberFee);// replaces the first question mark
            stmt.setDate(2, dtMaxBirthday);// replaces the second question mark      

            ResultSet results = stmt.executeQuery();
            if (results.next()) { // id is unique, one or zero records expected in result set

                // plainInteger returns integer converted to string with no commas.
                sd.webUserId = Format.fmtInteger(results.getObject("web_user_id"));
                sd.userEmail = Format.fmtString(results.getObject("user_email"));
                sd.userPassword = Format.fmtString(results.getObject("user_password"));
                sd.userImage = Format.fmtString(results.getObject("user_image"));
                sd.birthday = Format.fmtDate(results.getObject("birthday"));
                sd.membershipFee = Format.fmtDollar(results.getObject("membership_fee"));
                sd.userRoleId = Format.fmtInteger(results.getObject("web_user.user_role_id"));
                sd.userRoleType = Format.fmtString(results.getObject("user_role_type"));

            } else {
                sd.errorMsg = "Web User Not Found.";
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.webUser.DbMods.getMisc(): " + e.getMessage();
        }
        return sd;
    }

    public static StringData getlogon(DbConn dbc, String userEmail, String userPassword) {

        StringData sd = new StringData();
    
        sd.errorMsg = dbc.getErr();
    
        if (sd.errorMsg.length() > 0) {
            return sd; // db error or user inputs cannot be converted to correct types.
        }
        
        // you fill in the code.
        try {
            String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "
                    + "user_image, web_user.user_role_id, user_role_type "
                    + "FROM web_user, user_role WHERE web_user.user_role_id = user_role.user_role_id "
                    + "AND user_email = ? " 
                    + "AND user_password = ?";
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

            // Encode the id (that the user typed in) into the select statement, into the
            // the first (and only) ?
            stmt.setString(1, userEmail);// replaces the first question mark
            stmt.setString(2, userPassword);// replaces the second question mark      

            ResultSet results = stmt.executeQuery();
            if (results.next()) { // id is unique, one or zero records expected in result set

                // plainInteger returns integer converted to string with no commas.
                sd.webUserId = Format.fmtInteger(results.getObject("web_user_id"));
                sd.userEmail = Format.fmtString(results.getObject("user_email"));
                sd.userPassword = Format.fmtString(results.getObject("user_password"));
                sd.userImage = Format.fmtString(results.getObject("user_image"));
                sd.birthday = Format.fmtDate(results.getObject("birthday"));
                sd.membershipFee = Format.fmtDollar(results.getObject("membership_fee"));
                sd.userRoleId = Format.fmtInteger(results.getObject("web_user.user_role_id"));
                sd.userRoleType = Format.fmtString(results.getObject("user_role_type"));

            } else {
                sd.errorMsg = "Web User Not Found.";
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.webUser.DbMods.getMisc(): " + e.getMessage();
        }
        return sd;
    }
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

            String sql = "UPDATE web_user SET user_email = ?, user_password = ?, user_image = ?, "+
                    "membership_fee = ?, birthday = ?, user_role_id = ? WHERE web_user_id = ?";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setString(1, updateData.userEmail); // string type is simple
            pStatement.setString(2, updateData.userPassword);
            pStatement.setString(3, updateData.userImage);
            pStatement.setBigDecimal(4, Validate.convertDecimal(updateData.membershipFee));
            pStatement.setDate(5, Validate.convertDate(updateData.birthday));
            pStatement.setInt(6, Validate.convertInteger(updateData.userRoleId));
            pStatement.setInt(7, Validate.convertInteger(updateData.webUserId));

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
                errorMsgs.errorMsg = "Invalid User Role Id - " + errorMsgs.errorMsg;
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That email address is already taken - " + errorMsgs.errorMsg;
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // update

    public static StringData delete(DbConn dbc, String userId) {

        StringData sd = new StringData();

        // if (userId == null) {
        //     sd.errorMsg = "modelwebUser.DbMods.delete: " +
        //             "cannot delete web_user record because 'userId' is null";
        //     return sd;
        // }

        sd.errorMsg = dbc.getErr();
        if (sd.errorMsg.length() > 0) { // cannot proceed, db error
            return sd;
        }

        try {

            String sql = "DELETE FROM web_user WHERE web_user_id = ?";

            // Compile the SQL (checking for syntax errors against the connected DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, userId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                sd.errorMsg = "Record not deleted - there was no record with web_user_id " + userId;
            } else if (numRowsDeleted > 1) {
                sd.errorMsg = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            sd.errorMsg = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
        }

        return sd;
    } // delete

} // class