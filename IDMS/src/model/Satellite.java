package model;

import java.sql.Date;

public class Satellite {

    private int    satelliteId;
    private String satelliteName;
    private String orbitType;
    private Date   launchDate;
    private String monitoringRegion;
    private int    orgId;

    public Satellite() {}

    public Satellite(int satelliteId, String satelliteName, String orbitType,
                     Date launchDate, String monitoringRegion, int orgId) {
        this.satelliteId      = satelliteId;
        this.satelliteName    = satelliteName;
        this.orbitType        = orbitType;
        this.launchDate       = launchDate;
        this.monitoringRegion = monitoringRegion;
        this.orgId            = orgId;
    }

    public int    getSatelliteId()        { return satelliteId; }
    public void   setSatelliteId(int id)  { this.satelliteId = id; }

    public String getSatelliteName()         { return satelliteName; }
    public void   setSatelliteName(String s) { this.satelliteName = s; }

    public String getOrbitType()         { return orbitType; }
    public void   setOrbitType(String s) { this.orbitType = s; }

    public Date getLaunchDate()          { return launchDate; }
    public void setLaunchDate(Date d)    { this.launchDate = d; }

    public String getMonitoringRegion()         { return monitoringRegion; }
    public void   setMonitoringRegion(String s) { this.monitoringRegion = s; }

    public int  getOrgId()       { return orgId; }
    public void setOrgId(int id) { this.orgId = id; }
}