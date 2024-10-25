function FindLogoff() {
    const [isLoading, setIsLoading] = React.useState(false);
    const [msg, setMsg] = React.useState("Not Logged In");

    function findSomeClick() {
        setIsLoading(true);

        // You have to encodeURI user input before putting it into a URL for an AJAX call.
        // Otherwise, the web server may reject your AJAX call (for security reasons).
        var url = "logoff";;

        console.log("onclick function will call ajax_alt with url: " + url);

        // for testing, this shows findClick was called, but if everything works, 
        // this test message will be overwritten by the success or failure fn below.
        setMsg("findClick was called (testing)"); 

        // ajax_alt takes three parameters:
        //   1. url to call
        //   2. success function (input param is object converted from json page)
        //   3. failure function (input param is error message string)
        ajax_alt(
            url,
            function (obj) {
                console.log("Ajax Success - got object (see next line).");
                console.log(obj);
                    setMsg(
                        <div>
                            <h2> Logged Off </h2>
                        </div>
                    );
                
                setIsLoading(false);
            },
            function (errorMsg) {
                console.log("AJAX error. Here's the message: " + errMsg);
                setMsg("ajax failure: " + errorMsg);
                setIsLoading(false);
            }
        );

    }  // function findClick

    if (isLoading) {
        return (
            <div>
                <h1>... Loading ....</h1>
            </div>
        );
    }

    return (
        <div className="find">
            <h2>Log Off?</h2>
             <button onClick={findSomeClick}>Log Off</button>
    </div>
    );
}



