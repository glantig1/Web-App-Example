function MakeWorldListR_CGF() {

    var someWorldList = [
        {worldName  : "Not Earth", worldID: "1",  webUserID : "222", worldDescription : "The world we don't live in. This is an example of  world description. This could be far longer depending on the user input.", imgURL : "https://images.nightcafe.studio/jobs/IUcp6p6hy1arm39o8Tdj/IUcp6p6hy1arm39o8Tdj--1--bk37e_15.625x.jpg?tr:w-1600,c-at_max"}];
     
    return (
        <div>
            <MakeWorldListR worldList={someWorldList} profile = "World List"/>
            <MakeWorldListR/>
        </div>
    )
}