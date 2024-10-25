"use strict"; // prevent browser from globally auto-declaring variables


function MakeWorldListR({worldList = [{}], profile = "Unknown List"}) {
    
    function MakeWorldR({worldName  = "unknown world", worldID = "00", webUserID = "00", worldDescription = "unknown description",imgURL = "icons/delete.png"}){
        const [someWorldName, setWorldName] = React.useState(worldName);
        const [worldNameInput, setWorldNameInput] = React.useState("");
        const [someWorldDescription, setWorldDescription] = React.useState(worldDescription);
        const [worldDescriptionInput, setWorldDescriptionInput] = React.useState("");
        function changeWorldName () {
            setWorldName(worldNameInput);
        }
    
        function changeWorldDescription () {
            setWorldDescription(worldDescriptionInput); 
        }
        return (
            <div className="world">
              World Name: {someWorldName} <br/>
              World ID: {worldID} <br/>
              Web User ID: {webUserID} <br/>
              <img src={imgURL}/><br/>
              World Description: {someWorldDescription} <br/>
              <button onClick={changeWorldName}> Change World Name to: </button> <br/>
              <input value={worldNameInput} onChange={e=>setWorldNameInput(e.target.value)} /> <br/>
              <button onClick={changeWorldDescription}> Change World Description to: </button> <br/>
              <input value={worldDescriptionInput} onChange={e=>setWorldDescriptionInput(e.target.value)} /> <br/>
            </div>
        );
    }

    console.log("worldList on next line")
    return (
        <div className="worldList">
            <h2>{profile}</h2>
            {
                worldList.map(world =>
                    <MakeWorldR key={world.worldID} webUserID={world.webUserID} worldName={world.worldName} worldDescription={world.worldDescription} imgURL={world.imgURL} />
                )
            }
        </div>
    );
    
}