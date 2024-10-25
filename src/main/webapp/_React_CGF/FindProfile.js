const FindProfile = () => {
    const [isLoading, setIsLoading] = React.useState(true);
    const [msg, setMsg] = React.useState("Not Logged In");
    
    React.useEffect(() => {
        ajax_alt(
            "profile",
            function (obj) {
                console.log("Ajax Success - got object (see next line).");
                console.log(obj);

                if (obj.errorMsg == "Sorry but their is no profile") {
                    setMsg(<strong>{obj.errorMsg}</strong>);
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

    if (isLoading) {
        return (
            <div>
                <h1>... Loading ....</h1>
            </div>
        );
    }

    return (
    <div className="find">
        <div>{msg}</div>
    </div>
    );
}



