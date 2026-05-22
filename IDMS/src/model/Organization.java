package model;

public class Organization {

    private int    orgId;
    private String orgName;
    private String location;
    private String contactEmail;

    public Organization() {}

    public Organization(int orgId, String orgName, String location, String contactEmail) {
        this.orgId        = orgId;
        this.orgName      = orgName;
        this.location     = location;
        this.contactEmail = contactEmail;
    }

    public int    getOrgId()       { return orgId; }
    public void   setOrgId(int id) { this.orgId = id; }

    public String getOrgName()          { return orgName; }
    public void   setOrgName(String s)  { this.orgName = s; }

    public String getLocation()         { return location; }
    public void   setLocation(String s) { this.location = s; }

    public String getContactEmail()         { return contactEmail; }
    public void   setContactEmail(String s) { this.contactEmail = s; }
}