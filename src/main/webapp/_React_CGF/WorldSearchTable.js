"use strict";

const WorldSearchTable = () => {
    const [isLoading, setIsLoading] = React.useState(true);
    const [dbList, setDbList] = React.useState([]);
    const [error, setError] = React.useState(null);
    const [filterInput, setFilterInput] = React.useState("");
    const [filteredList, setFilteredList] = React.useState([]);
    console.log("WorldSearchTable running!!");
    React.useEffect(() => {

        ajax_alt(

            "world/getAll", 
            function (dbList) {   
                if (dbList.dbError.length > 0) {
                    console.log("Database error was " + dbList.dbError);
                    setError(dbList.dbError);
                } else {
                    console.log("Data was read from the DB. See next line,");
                    console.log(dbList.worldList);
                    setDbList(dbList.worldList);
                    setFilteredList(dbList.worldList);
                }
                setIsLoading(false); 
            },
            function (msg) {
                console.log("Ajax error encountered: " + msg);
                setError(msg);
                setIsLoading(false); 
            }
        );
    }, []);
    function callWorldInsert() {
        window.location.hash = "#/worldInsert";
    }

    function deleteListEle(theList, indx) {
        let newList = [];
        for (var i = 0; i < theList.length; i++) {
            if (i !== indx) {
                newList.push(theList[i]);
            }
        }
        console.log("here is list after deleting element " + indx);
        console.log(newList);
        return newList;
    }

    function deleteWorld(worldObj, indx) {

        console.log("To delete world " + worldObj.worldName + "?");

        if (confirm("Do you really want to delete " + worldObj.worldName + "? ")) {
            
            // var messageDOM = document.getElementById("message");
            // messageDOM.innerHTML = ""; // blank out any old message
            var idToDelete = worldObj.worldId;

            // parameter properties needed for ajax call: url, successFn, and errorId
            ajax_alt(

                "world/delete?worldId=" + idToDelete, // URL for AJAX call to invoke
    
                // success function (anonymous)
                function (dbList) {   // success function gets obj from ajax_alt
                    if (dbList.errorMsg.length === 0) {
                        setFilteredList(deleteListEle(filteredList, indx));
                        console.log("Database error was " + dbList.dbError);
                        setError(dbList.dbError);
                    } else {
                        console.log("Data was read from the DB. See next line,");
                        console.log(dbList.worldList);
                        setDbList(dbList.worldList);
                        setFilteredList(dbList.worldList);
                    }
                    setIsLoading(false); // allow the component to be rendered
                },
    
                // failure function (also anonymous)
                function (msg) {       // failure function gets error message from ajax_alt
                    console.log("Ajax error encountered: " + msg);
                    setError(msg);
                    setIsLoading(false); // allow the component to be rendered
                }
            ); 
        }
    } // deleteWorld

    function sortByProp(propName, sortType) {
        jsSort(filteredList, propName, sortType);
        jsSort(dbList, propName, sortType);
        console.log("Sorted list is below");
        console.log(dbList);

        
        let listCopy = JSON.parse(JSON.stringify(dbList)); 
        let filteredListCopy = JSON.parse(JSON.stringify(filteredList));
        setDbList(listCopy);
        setFilteredList(filteredListCopy);
    }

    const doFilter = (filterInputVal) => {
        let newList = filterObjList(dbList, filterInputVal);
        console.log("function doFilter. filterInputVal is: " + filterInputVal +
            ". See filtered list on next line:");
        console.log(newList);
        setFilteredList(newList);
    };

    const clearFilter = () => {
        setFilterInput("");
        doFilter("");
    }

    if (isLoading) {
        console.log("initial rendering, Data not ready yet...");
        return <div> Loading... </div>
    }

    if (error) {
        console.log(`there must have been an ajax error (e.g., bad URL), 
        or database error (e.g., connection error because not tunnelled in)...`);
        return <div>Error: {error} </div>;
    }

    return (
        <div className="clickSort">
            <h3>
            Filterable World List <img src="icons/insert.png" onClick={callWorldInsert}/> &nbsp;
            </h3>
            <h3>
                <input value={filterInput} onChange={(e) => setFilterInput(e.target.value)}/> &nbsp; 
                <button onClick={() => doFilter(filterInput)}>Search</button> &nbsp; 
                <button onClick={clearFilter}>Clear</button>
            </h3>

            <table>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th onClick={() => sortByProp("worldName", "text")}
                            className="textAlignLeft" >
                            <img src="icons/sortUpDown16.png" />World Name
                        </th>
                        <th onClick={() => sortByProp("userEmail", "text")}
                            className="textAlignLeft" >
                            <img src="icons/sortUpDown16.png" />Email
                        </th>
                        <th onClick={() => sortByProp("worldDescription", "text")}
                            className="textAlignCenter" >
                            <img src="icons/blackSort.png" />Description
                        </th>
                        <th onClick={() => sortByProp("worldInspiration", "text")}
                            className="textAlignLeft" >
                            <img src="icons/sortUpDown16.png" />Inspiration
                        </th>
                        <th onClick={() => sortByProp("worldMagicSystem", "text")}
                            className="textAlignLeft" >
                            <img src="icons/sortUpDown16.png" />Magic System
                        </th>
                        <th onClick={() => sortByProp("worldDonations", "number")}
                            className="textAlignRight">
                            <img src="icons/sortUpDown16.png" />Donations
                        </th>
                        <th onClick={() => sortByProp("worldEnteredOrUpdated", "date")}
                            className="textAlignRight">
                            <img src="icons/sortUpDown16.png" />Date Updated
                        </th>
                        <th>Error</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        filteredList.map((listObj, index) =>
                            <tr key={listObj.worldId}>
                                <td>
                                    <a href={'#/worldUpdate/:'+listObj.worldId}><img src="icons/update.png" className="clickLink"/></a>
                                </td>
                                <td className="textAlignCenter" onClick={() => deleteWorld(listObj, index)}   >
                                    <img src="icons/delete.png" />
                                </td>
                                <td className="textAlignLeft">{listObj.worldName}<br/><img src={listObj.worldIcon} /></td> 
                                <td className="textAlignLeft">{listObj.userEmail}<br/></td> 
                                <td className="textAlignLeft">{listObj.worldDescription}</td>
                                <td className="textAlignLeft">{listObj.worldInspiration}</td>
                                <td className="textAlignLeft">{listObj.worldMagicSystem}</td>
                                <td className="textAlignRight">{listObj.worldDonations}</td>
                                <td className="textAlignRight">{listObj.worldEnteredOrUpdated}</td>
                                <td>{listObj.errorMsg}</td>
                            </tr>
                        )
                    }
                </tbody>
            </table>
        </div>
    );
};