package model;

public class SystemModel {

    private int    systemId;
    private String systemName;
    private String systemType;
    private String status;
    private int    orgId;

    public SystemModel() {}

    public SystemModel(int systemId, String systemName, String systemType, String status, int orgId) {
        this.systemId   = systemId;
        this.systemName = systemName;
        this.systemType = systemType;
        this.status     = status;
        this.orgId      = orgId;
    }

    public int    getSystemId()          { return systemId; }
    public void   setSystemId(int id)    { this.systemId = id; }

    public String getSystemName()          { return systemName; }
    public void   setSystemName(String s)  { this.systemName = s; }

    public String getSystemType()          { return systemType; }
    public void   setSystemType(String s)  { this.systemType = s; }

    public String getStatus()          { return status; }
    public void   setStatus(String s)  { this.status = s; }

    public int  getOrgId()         { return orgId; }
    public void setOrgId(int id)   { this.orgId = id; }
}