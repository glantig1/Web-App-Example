const AjaxUsers = (url) => {

    console.log("AjaxUsers running");

    // Tell React that 'items' (an array of objects) is a state variable 
    // that (when changed) should redisplay this component.
    // Set its initial value to [], an empty array.
    const [items, setItems] = React.useState([]);

    // Tell React that "error" is a state variable that (when changed) 
    // should redisplay this component. Set its initial value to null.
    const [error, setError] = React.useState(null);

    const [isLoading, setIsLoading] = React.useState(true);

    // useEffect 2nd parameter is an array of elements that 
    // (if any of those state variables change) should trigger the function specified 
    // as the 1st useEffect parameter. 
    // RUN ONCE PATTERN: If you put [] as 2nd param, it runs the 1st param (fn) once. 
    React.useEffect(() => {

        // ajax_alt takes three parameters: the URL to read, Success Fn, Failure Fn.
        ajax_alt(
            url, // URL for AJAX call to invoke
            //"json/users.json", // URL for AJAX call to invoke
            //"webUser/getAll", // URL for AJAX call to invoke

            // success function (anonymous)
            function (dbList) {   // success function gets obj from ajax_alt
                if (dbList.dbError.length > 0) {
                    setError(dbList.dbError);
                } else {
                    console.log("in AjaxUsers, here is web user list (on the next line):");
                    console.log(dbList.webUserList);
                    setItems(dbList.webUserList);
                }
                setIsLoading(false); // set isLoading last to prevent premature rendering. 
            },

            // failure function (also anonymous)
            function (msg) {       // failure function gets error message from ajax_alt
                setError(msg);
                setIsLoading(false); // set isLoading last to prevent premature rendering.
            }
        );
    },
        []);

    if (isLoading) {
        console.log("Is Loading...");
        return <div> Loading... </div>
    }

    if (error) {
        console.log("Error...");
        return <div>Error: {error} </div>;
    }

    console.log("items for UserTable on next line");
    console.log(items);
    return (
        <div className="clickSort">
            <h3>
                Web User List
            </h3>
            <table>
                <thead>
                    <tr>
                        <th>Email</th>
                        <th className="textAlignCenter">Image</th>
                        <th className="textAlignCenter">Birthday</th>
                        <th className="textAlignRight">Membership Fee</th>
                        <th>Role</th>
                        <th>Error</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        items.map((item, index) =>
                            <tr key={item.webUserId}>
                                <td>{item.userEmail}</td>
                                <td className="shadowImage textAlignCenter"><img src={item.userImage} /></td>
                                <td className="textAlignCenter">{item.birthday}</td>
                                <td className="textAlignRight">{item.membershipFee}</td>
                                <td className="nowrap">{item.userRoleId} {item.userRoleType}</td>
                                <td>{item.errorMsg}</td>
                            </tr>
                        )
                    }
                </tbody>
            </table>
        </div>
    );

}; // function AjaxUsers 