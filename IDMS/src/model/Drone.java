package model;

public class Drone {

    private int    droneId;
    private String droneName;
    private String droneType;
    private String status;
    private int    rangeKm;
    private int    orgId;

    public Drone() {}

    public Drone(int droneId, String droneName, String droneType, String status, int rangeKm, int orgId) {
        this.droneId   = droneId;
        this.droneName = droneName;
        this.droneType = droneType;
        this.status    = status;
        this.rangeKm   = rangeKm;
        this.orgId     = orgId;
    }

    public int    getDroneId()       { return droneId; }
    public void   setDroneId(int id) { this.droneId = id; }

    public String getDroneName()         { return droneName; }
    public void   setDroneName(String s) { this.droneName = s; }

    public String getDroneType()         { return droneType; }
    public void   setDroneType(String s) { this.droneType = s; }

    public String getStatus()        { return status; }
    public void   setStatus(String s){ this.status = s; }

    public int  getRangeKm()       { return rangeKm; }
    public void setRangeKm(int km) { this.rangeKm = km; }

    public int  getOrgId()       { return orgId; }
    public void setOrgId(int id) { this.orgId = id; }
}