package model.world;
public class StringData {

    public String worldId = "";     
    public String worldName = "";     
    public String worldDescription = ""; 
    public String worldIcon = "";
    public String worldMagicSystem = "";
    public String worldInspiration = "";  
    public String worldDonations = "";
    public String worldEnteredOrUpdated = "";
    public String webUserId = "";
    public String userEmail = "";
    
    public String errorMsg = "";
    
    public StringData() {
    }

    public int characterCount() {
        String s = this.worldId + this.worldName + this.worldDescription +
                this.worldIcon + this.worldMagicSystem + this.worldInspiration +
                this.worldDonations + this.worldEnteredOrUpdated + this.webUserId + this.userEmail;
        return s.length();
    }

    // not required, can be useful for debugging, e.g.,
    // System.println(sdObj.toString());
    public String toString() {
        return "World Id: " + this.worldId + 
        "World Name: " + this.worldName + 
        "World Description: " + this.worldDescription +
        "World Icon: " + this.worldIcon + 
        "World Magic System: " + this.worldMagicSystem + 
        "World Inspiration: " + this.worldInspiration +
        "World Donations: " + this.worldDonations + 
        "World Entered Or Updated: " + this.worldEnteredOrUpdated + 
        "Web User Id: " + this.webUserId + 
        "User Email: " + this.userEmail;
    }
}
