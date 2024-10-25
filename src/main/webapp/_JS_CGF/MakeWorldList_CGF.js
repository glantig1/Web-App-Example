function MakeWorldList_CGF() {

    var ele = document.createElement("div");

    var myWorldList =[
        { 
            worldName: "Not Earth",
            worldID: "1", 
            webUserID: "222", 
            worldDescription: "The world we don't live in.",  
            imgURL: "https://images.nightcafe.studio/jobs/IUcp6p6hy1arm39o8Tdj/IUcp6p6hy1arm39o8Tdj--1--bk37e_15.625x.jpg?tr=w-1600,c-at_max" 
        }
    ];

    var myWorldComp = MakeWorldList({
        worldList: myWorldList,
        profile: "World List"
    });
    ele.appendChild(myWorldComp);

    var yourWorld = MakeWorldList({},{});
    ele.appendChild(yourWorld);

    return ele;
}