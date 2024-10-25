"use strict"; // not sure if this is needed in react...

const WorldInsertOrUpdate = (props) => {

    // See if this is an Insert or an Updat by checking the path that invoked this component.
    // If the path has a : in it, then its update, else its insert.
    // If update, extract (from the path) the id of the webUser record that is to be updated. 
    // console.log("props for worldInsertOrUpdate on next line");
    // console.log(props);

    var action = "insert"; // exact spelling has to match web API @RequestMapping
    var id = "";
    var url = props.location.pathname;
    console.log("url that invoked worldInsertOrUpdate is " + url);
    if (url.search(":") > -1) {
        const url_list = url.split(":");
        id = url_list[url_list.length - 1];
        console.log("to update id " + id);
        action = "update";
    } else {
        console.log("to insert");
    }

    // Set initial values of state variables and receive (from React) a setter function for each one.
    // In React, you create State Variables for anything that (if changed) needs to re-render the UI. 

    // Object (State Variable) that holds all the world entered data. Each object 
    // is linked with a textbox for world input. 
    const [worldData, setWorldData] = React.useState(
        {
            "worldId": "",     
            "worldName": "",     
            "worldDescription": "",
            "worldIcon": "",
            "worldMagicSystem": "",
            "worldInspiration": "",  
            "worldDonations": "",
            "worldEnteredOrUpdated": "",
            "webUserId": "",
            "errorMsg": ""
        }
    );

    const [webUserList, setWebUserList] = React.useState([]);
    

    // Object (State Variable) that holds all the error messages - field level 
    // and form/record level (errorMsg).
    const [errorObj, setErrorObj] = React.useState(
        {
            "worldId": "",     
            "worldName": "",     
            "worldDescription": "",
            "worldIcon": "",
            "worldMagicSystem": "",
            "worldInspiration": "",  
            "worldDonations": "",
            "worldEnteredOrUpdated": "",
            "webUserId": "",
            "errorMsg": ""
        }
    );

    // This state variable helps control when insertworldAPI should be run
    // (whenever the world clicks submit but NOT initially).
    const [submitCount, setSubmitCount] = React.useState(0);

    // By having this boolean state variable, we avoid rendering the component 
    // before we are ready to do so. 
    const [isLoading, setIsLoading] = React.useState(true);

    const encodeUserInput = () => {
        var worldInputObj = {
            "worldId": worldData.worldId,     
            "worldName": worldData.worldName,     
            "worldDescription": worldData.worldDescription,
            "worldIcon": worldData.worldIcon,
            "worldMagicSystem": worldData.worldMagicSystem,
            "worldInspiration": worldData.worldInspiration,  
            "worldDonations": worldData.worldDonations,
            "worldEnteredOrUpdated": worldData.worldEnteredOrUpdated,
            "webUserId": worldData.webUserId,
        };
        console.log("worldInputObj on next line");
        console.log(worldInputObj);
        // turn the world input object into JSON then run that through 
        // a URI encoder (needed for security on server side, prevents 
        // server from hacks). 
        //return encodeURIComponent(JSON.stringify(worldInputObj));
        return encodeURI(JSON.stringify(worldInputObj));
    };

    // If you just change the value of a State object's property, then React does not 
    // know that the object has been changed (and thus does re-render the UI). 
    // To get around this, I wrote function setProp that clones the object, changes 
    // the desired property, then returns the clone. THEN React knows that the object 
    // has been changed (and re-renders the UI). 
    const setProp = (obj, propName, propValue) => {
        var o = Object.assign({}, obj); // makes a copy of the object
        o[propName] = propValue; // changes the property of the copy
        // console.log("setProp orig object is");
        // console.log(obj);
        // console.log("after changing " + propName + " to " + propValue + " the new obj is");
        // console.log(o);
        return o; // returns the object copy with the property's value changed.
    };

    // useEffect second parameter is an array of "watch elements" that 
    // (if they change) should trigger the function specified 
    // as the first useEffect parameter.

    // This code should execute just once at initial page render because 
    // the array of watch elements (second parameter to useEffect) is empty.
    React.useEffect(() => {

            console.log("AJAX call for user list");
            ajax_alt("webUser/getAll",

                function (obj) { // obj holds user list from AJAX call
                    if (obj.dbError.length > 0) {  // db error trying to read user list
                        setErrorObj(setProp(errorObj, "webUserId", obj.dbError));
                    } else {
                        // role fields (from role/getAll): webUserId, userEmail. 
                        // sort alphabetically by email (not by id)
                        obj.webUserList.sort(function (a, b) {
                            if (a.userEmail > b.userEmail) {
                                return 1
                            } else {
                                return -1;
                            }
                            return 0;
                        });
                        console.log('sorted user list on next line');
                        console.log(obj.webUserList);
                        setWebUserList(obj.webUserList);
                        

                        if (action === "update") { //this is update, not insert, get webUser by the id
                            console.log("Now getting webUser record " + id + " for the update");
                            ajax_alt("world/getById?worldId=" + id,
                            function (obj) {
                                if (obj.errorMsg.length > 0) { // obj.errorMsg holds error, e.g., db error
                                    console.log("DB error trying to get the webUser record for udpate");
                                    setErrorObj(setProp(errorObj, "errorMsg", obj.errorMsg));
                                    //setProp = (obj, propName, propValue)

                                } else { // obj holds the webUser record of the given id
                                    console.log("got the web user record for update (on next line)");
                                    console.log(obj);
                                    setWorldData(obj); // prepopulate user data since this is update.
                                }
                            },
                            function (msg) { // AJAX Error Msg from trying to read the webUser to be updated.
                                setErrorObj(setProp(errorObj, "errorMsg", msg));
                            }
                        );
                        }

                    }
                },
                function (ajaxErrorMsg) { // AJAX Error Msg from trying to read the user list.
                    // setWebUserError(msg);
                    setErrorObj(setProp(errorObj, "errorMsg", ajaxErrorMsg));
                }
            );
            setIsLoading(false);
        }, []);


    // This code should execute every time the submit BUTTON is clicked, 
    // but NOT on initial rendering (it checks submitCount). 
    React.useEffect(() => {

        console.log("Here is the user data that will be sent to the insert/update API");
        console.log(worldData);

        console.log("SubmitCount has changed, value is " + submitCount);
        if (submitCount > 0) { // dont make the AJAX call on initial render

            // action was set to insert or update above (must match web API @RequestMapping). 
            ajax_alt("world/" + action + "?jsonData=" + encodeUserInput(),

                function (obj) { // obj holds field level error messages
                    console.log("These are the error messages (next line)");
                    console.log(obj);

                    if (obj.errorMsg.length === 0) {
                        // errorMsg = "" means no error, record was inserted (or updated). 
                        obj.errorMsg = "Record Saved !";
                    }

                    setErrorObj(obj); // show the field level error messages (will all be "" if record was inserted)

                },
                function (ajaxErrorMsg) { // AJAX error msg trying to call the insert or update API
                    setErrorObj(setProp(errorObj, "errorMsg", ajaxErrorMsg));
                }
            );
        }
    }, [submitCount]);

    const validate = () => {
        // In this function, we just change the value of state variable submitCount 
        // so that the React.useEffect (that's watching for changes in submitCount)
        // will run, making the AJAX call.  
        console.log("Validate, should kick off AJAX call");
        setSubmitCount(submitCount + 1);
    };

    if (isLoading) {
        return <div> ... Loading ... </div>;
    }

    return (
        <div className="insertArea">
            <tbody>
                <tr>
                    <td>Id</td>
                    <td>
                        <input value={worldData.worldId} disabled />
                    </td>
                    <td className="error">
                        {errorObj.worldId}
                    </td>
                </tr>
                <tr>
                    <td>World Name</td>
                    <td>
                        <input value={worldData.worldName} onChange=
                            {e => setWorldData(setProp(worldData, "worldName", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldName}
                    </td>
                </tr>
                <tr>
                    <td>Description</td>
                    <td>
                        <input value={worldData.worldDescription} onChange=
                            {e => setWorldData(setProp(worldData, "worldDescription", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldDescription}
                    </td>
                </tr>
                <tr>
                    <td>Magic System</td>
                    <td>
                        <input value={worldData.worldMagicSystem} onChange=
                            {e => setWorldData(setProp(worldData, "worldMagicSystem", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldMagicSystem}
                    </td>
                </tr>
                <tr>
                    <td>Image</td>
                    <td>
                        <input value={worldData.worldIcon} onChange=
                            {e => setWorldData(setProp(worldData, "worldIcon", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldIcon}
                    </td>
                </tr>
                <tr>
                    <td>Inspiration</td>
                    <td>
                        <input value={worldData.worldInspiration} onChange=
                            {e => setWorldData(setProp(worldData, "worldInspiration", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldInspiration}
                    </td>
                </tr>
                <tr>
                    <td>Donations</td>
                    <td>
                        <input value={worldData.worldDonations} onChange=
                            {e => setWorldData(setProp(worldData, "worldDonations", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldDonations}
                    </td>
                </tr>
                <tr>
                    <td>Entered Or Updated</td>
                    <td>
                        <input value={worldData.worldEnteredOrUpdated} onChange=
                            {e => setWorldData(setProp(worldData, "worldEnteredOrUpdated", e.target.value))}
                        />
                    </td>
                    <td className="error">
                        {errorObj.worldEnteredOrUpdated}
                    </td>
                </tr>
                <tr>
                    <td>Email</td>
                    <td>
                        <select onChange=
                            {e => setWorldData(setProp(worldData, "webUserId", e.target.value))}
                            value={worldData.webUserId}
                        >
                            {
                                webUserList.map(user =>
                                    <option key={user.webUserId} value={user.webUserId} >
                                        {user.userEmail}
                                    </option>
                                )
                            }
                        </select>
                    </td>
                    <td className="error">
                        {errorObj.webUserId}
                    </td>
                </tr>
                <tr>
                    <td>
                        <br />
                        <button type="button" onClick={validate}>Save</button>
                    </td>
                    <td className="error" colSpan="2">
                        <br />
                        {errorObj.errorMsg}
                    </td>
                </tr>
            </tbody>
        </div>

    ); // ends the return statement

}; // end of function/component
