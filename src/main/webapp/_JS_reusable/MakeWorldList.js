"use strict"; // prevent browser from globally auto-declaring variables

function MakeWorldList ({worldList = [{}], profile = "Unknown List"}) {

    function MakeWorld({worldName  = "unknown world", worldDescription = "unknown description", worldID = "00", webUserID = "00", imgURL = "icons/delete.png"}) {


        var worldObj = document.createElement("div");
        worldObj.classList.add("world");
        worldObj.someWorldName = worldName;
        worldObj.someWorldDesctiption= worldDescription;
        worldObj.someWorldId = worldID
        worldObj.someWebUserId = webUserID
        worldObj.someImg = imgURL;
    
        worldObj.setWorldName = function (newWorldName) {
          worldObj.someWorldName = newWorldName;
          display(); 
        };
        
        worldObj.setWorldDescription = function (newWorldName) {    
          worldObj.someWorldDesctiption = newWorldName;
          display();
        };
    
        worldObj.innerHTML = `
          <div class='worldInfoClass'></div>
          <button class='worldNameButtonClass'>Change World Name to: </button> <br/>
          <input class='newWorldNameInputClass'/> <br/>
          <button class='worldDescriptionClass'>Change World Description to: </button> <br/>
          <input class='newWorldInputClass'/> <br/>
          `;
    
        // Create variable references for all DOM elements (above) that we need to programatically access. 
        var worldInfo = worldObj.getElementsByClassName("worldInfoClass")[0];
        var worldNameButton = worldObj.getElementsByClassName("worldNameButtonClass")[0];
        var newWorldNameInput = worldObj.getElementsByClassName("newWorldNameInputClass")[0];
        var worldDescriptionButton = worldObj.getElementsByClassName("worldDescriptionClass")[0];
        var newWorldDescriptionInput = worldObj.getElementsByClassName("newWorldInputClass")[0];
        
        var display = function () {
            worldInfo.innerHTML = `
              <p>
                 World Name: ${worldObj.someWorldName}<br/>
                 World ID: ${worldObj.someWorldId} <br/>
                 Web User ID: ${worldObj.someWebUserId} <br/>
                 World Description: ${worldObj.someWorldDesctiption} <br/>
                 <img src = ${worldObj.someImg}>
              </p>
            `;
        };
    
        display();
        worldNameButton.onclick = function () {
          worldObj.setWorldName(newWorldNameInput.value);
        };
        worldDescriptionButton.onclick = function () {
          worldObj.setWorldDescription(newWorldDescriptionInput.value);
        };
    
    
        return worldObj;
    } 
    var worldListComp = document.createElement("div");
    worldListComp.classList.add("worldList");
    worldListComp.innerHTML = `<h2>${profile}</h2>`;
    
    for (var worldObj of worldList) {
        worldListComp.appendChild(MakeWorld(worldObj));
    }
    return worldListComp;
}
