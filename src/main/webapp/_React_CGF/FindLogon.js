function FindLogon() {
    const [isLoading, setIsLoading] = React.useState(false);
    const [userEmailInput, setuserEmailInput] = React.useState("");
    const [userPassInput, setuserPassInput] = React.useState("");
    const [msg, setMsg] = React.useState("Please Log On");


    React.useEffect(() => {
        ajax_alt(
            "profile",
            function (obj) {
                console.log("Ajax Success - got object (see next line).");
                console.log(obj);

                if (obj.errorMsg == "Sorry but their is no profile") {
                    setMsg(<strong>{"Please Log In"}</strong>);
                } 
                else {
                    setMsg(
                        <div>
                            <h2>Welcome Web User {obj.webUserId} </h2>
                            Birthday: {obj.birthday} <br />
                            MembershipFee: {obj.membershipFee} <br />
                            User Role: {obj.userRoleId} {obj.userRoleType} <br />
                            <p> <img src={obj.userImage} /> </p>
                        </div>
                    );
                }
                setIsLoading(false);
            },
            function (errorMsg) {
                console.log("AJAX error. Here's the message: " + errMsg);
                setMsg("ajax failure: " + errorMsg);
                setIsLoading(false);
            }
        );
    }, []);

    function findClick() {
        setIsLoading(true);

        // You have to encodeURI user input before putting it into a URL for an AJAX call.
        // Otherwise, the web server may reject your AJAX call (for security reasons).
        var url = "logon?userEmail=" + encodeURI(userEmailInput) +"&userPassword="+encodeURI(userPassInput);;

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
                if (obj.errorMsg.length > 0) {
                    setMsg(<strong>{obj.errorMsg}</strong>);
                } else {
                    setMsg(
                        <div>
                            <h2>Welcome Web User {obj.webUserId} </h2>

                            Birthday: {obj.birthday} <br />
                            MembershipFee: {obj.membershipFee} <br />
                            User Role: {obj.userRoleId} {obj.userRoleType} <br />
                            <p> <img src={obj.userImage} /> </p>
                        </div>
                    );
                }
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
            <h2>React Find UI</h2>
            Enter Email: &nbsp;
            <br></br>
            <input value={userEmailInput} onChange={(e) => setuserEmailInput(e.target.value)} />
            <br></br>
            Enter Password: &nbsp;
            <br></br>
            <input value={userPassInput} type="password" onChange={(e) => setuserPassInput(e.target.value)} />
            <br></br>
            <button onClick={findClick}>Log In</button>
            <div>{msg}</div>
        </div>
    );
}
