package com.sample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.webUser.*;
import dbUtils.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import view.WebUserView;

@RestController
public class WebUserController {

    @RequestMapping(value = "/webUser/getAll", produces = "application/json")
    public String allUsers() {

        StringDataList list = new StringDataList(); // dbError empty, list empty
        DbConn dbc = new DbConn();
        list = WebUserView.getAllUsers(dbc);

        dbc.close(); // EVERY code path that opens a db connection must close it
                     // (or else you have a database connection leak).

        return Json.toJson(list); // convert sdl obj to JSON Format and return that.
    }
    @RequestMapping(value = "/webUser/getById", params = {
            "userId" }, produces = "application/json")
    public String getById(@RequestParam("userId") String userId) {
        StringData sd = new StringData();
        if (userId == null) {
            sd.errorMsg = "Error: URL must be user/getById/xx " +
                    "where xx is the web_user_id of the desired web_user record.";
        } else {
            DbConn dbc = new DbConn();
            sd.errorMsg = dbc.getErr();
            if (sd.errorMsg.length() == 0) {
                System.out.println("*** Ready to call DbMods.getById");
                sd = DbMods.getById(dbc, userId);
            }
            dbc.close(); // EVERY code path that opens a db connection must close it
            // (or else you have a database connection leak).
        }
        return Json.toJson(sd);
    }
    @RequestMapping(value = "/webUser/getMisc", params = { "minMemberFee",
            "maxBirthday" }, produces = "application/json")
    public String getByMisc(
            @RequestParam("minMemberFee") String minMemberFee,
            @RequestParam("maxBirthday") String maxBirthday) {

        StringData sd = new StringData();
        if (minMemberFee == null || maxBirthday == null) {
            sd.errorMsg = "Error: URL must be user/getMisc?minMemberFee=xx&maxBirthday=xx " +
                    "where xx and xx are the fields of the desired records.";
        } else {
            DbConn dbc = new DbConn();
            sd = DbMods.getMisc(dbc, minMemberFee, maxBirthday);
            dbc.close(); // EVERY code path that opens a db connection must close it
            // (or else you have a database connection leak).
        }
        return Json.toJson(sd);
    
    
    }
    @RequestMapping(value = "/logon", params = { "userEmail", "userPassword" }, produces = "application/json")
    public String getSomelogon(
        @RequestParam("userEmail") String userEmail,
        @RequestParam("userPassword") String userPassword,
        HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        StringData sd = new StringData();
        try {
            sd = (StringData) session.getAttribute("myProfile");
            if (sd != null) {
                sd.errorMsg = "Above data was read from the session";
            } else {
                sd = new StringData();
                sd.errorMsg = "Sorry but their is no profile";
            }
        } catch (Exception e) {
            System.out.println("/profile error: " + e.getMessage());
            sd.errorMsg += ". " + e.getMessage();
        }

        if (userEmail == null || userPassword == null) {
            sd.errorMsg = "Error: URL must be user/logon?userEmail=xx&userPassword=xx " +
                    "where xx and xx are the fields of the desired records.";
        } else {
            DbConn dbc = new DbConn();
            sd = DbMods.getlogon(dbc, userEmail, userPassword);
            try {
                session.setAttribute("myProfile", sd); // write object to JSP session object
            } catch (Exception e) {
                System.out.println("/logon error: " + e.getMessage());
                sd.errorMsg += ". " + e.getMessage();
            }
            
            dbc.close(); // EVERY code path that opens a db connection must close it
            // (or else you have a database connection leak).
        }
        return Json.toJson(sd);
    
    }

    @RequestMapping(value = "/profile", produces = "application/json")
    public String getProfile(
        HttpServletRequest request) {

        HttpSession session = request.getSession();
        StringData sd = new StringData();

        try {
            sd = (StringData) session.getAttribute("myProfile");
            if (sd != null) {
                sd.errorMsg = "Above data was read from the session";
            } else {
                sd = new StringData();
                sd.errorMsg = "Sorry but their is no profile";
            }
        } catch (Exception e) {
            System.out.println("/profile error: " + e.getMessage());
            sd.errorMsg += ". " + e.getMessage();
        }
        return Json.toJson(sd);
    }

    @RequestMapping(value = "/logoff", produces = "application/json")
    public String logoffController(
        HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        StringData sd = new StringData(); // all fields now set to ""

        try {
            session.invalidate();
            sd.errorMsg = "Session Logged Out";
        } catch (Exception e) {
            System.out.println("/logout  error: " + e.getMessage());
            sd.errorMsg += ". " + e.getMessage();
        }

        return Json.toJson(sd);
    }
    
    @RequestMapping(value = "/webUser/insert", params = { "jsonData" }, produces = "application/json")
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

                    
                    errorMsgs.userEmail = Validate.stringMsg(insertData.userEmail, 45, true);
                    errorMsgs.userPassword = Validate.stringMsg(insertData.userPassword, 45, true);

                    if (insertData.userPassword.compareTo(insertData.userPassword2) != 0) { // case sensative comparison
                        errorMsgs.userPassword2 = "Both passwords must match";
                    }

                    errorMsgs.userImage = Validate.stringMsg(insertData.userImage, 300, true);
                    errorMsgs.birthday = Validate.dateMsg(insertData.birthday, false);
                    errorMsgs.membershipFee = Validate.decimalMsg(insertData.membershipFee, false);
                    errorMsgs.userRoleId = Validate.integerMsg(insertData.userRoleId, true);

                    if (errorMsgs.characterCount() > 0) { // at least one field has an error, don't go any further.
                        errorMsgs.errorMsg = "Please try again";

                    } else { // all fields passed validation

                        // Start preparing SQL statement
                        String sql = "INSERT INTO web_user (user_email, user_password, user_image, membership_fee, birthday, user_role_id) "
                                + "values (?,?,?,?,?,?)";

                        // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
                        // Only difference is that Sally's class takes care of encoding null
                        // when necessary. And it also System.out.prints exception error messages.
                        PrepStatement pStatement = new PrepStatement(dbc, sql);

                        // Encode string values into the prepared statement (wrapper class).
                        pStatement.setString(1, insertData.userEmail); // string type is simple
                        pStatement.setString(2, insertData.userPassword);
                        pStatement.setString(3, insertData.userImage);
                        pStatement.setBigDecimal(4, Validate.convertDecimal(insertData.membershipFee));
                        pStatement.setDate(5, Validate.convertDate(insertData.birthday));
                        pStatement.setInt(6, Validate.convertInteger(insertData.userRoleId));

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
                            errorMsgs.errorMsg = "That email address is already taken";
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
    @RequestMapping(value = "/webUser/update", params = { "jsonData" }, produces = "application/json")
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
                String msg = "Unexpected error in controller for 'webUser/insert'... " +
                        e.getMessage();
                System.out.println(msg);
                errorData.errorMsg = msg;
            }
        }
        return Json.toJson(errorData);
    }

    @RequestMapping(value = "/webUser/delete", params = {"userId" }, produces = "application/json")
    public String delete(@RequestParam("userId") String deleteUserId) {
        StringData sd = new StringData();
        //if (deleteUserId == null) {
        //    sd.errorMsg = "Error: URL must be user/delete?userId=xx, where " +
        //            "xx is the web_user_id of the web_user record to be deleted.";
        //} 
        //else {
            DbConn dbc = new DbConn();
            sd = DbMods.delete(dbc, deleteUserId);
            dbc.close(); // EVERY code path that opens a db connection must close it
            // (or else you have a database connection leak).
        //}
        return Json.toJson(sd);
    }


    
}