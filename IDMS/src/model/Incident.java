package model;

import java.sql.Timestamp;

public class Incident {

    private int       incidentId;
    private String    incidentType;
    private String    severity;
    private Timestamp incidentDate;
    private String    description;
    private String    status;
    private Integer   systemId;      // nullable FK
    private Integer   satelliteId;   // nullable FK
    private Integer   droneId;       // nullable FK

    public Incident() {}

    public Incident(int incidentId, String incidentType, String severity,
                    Timestamp incidentDate, String description, String status,
                    Integer systemId, Integer satelliteId, Integer droneId) {
        this.incidentId   = incidentId;
        this.incidentType = incidentType;
        this.severity     = severity;
        this.incidentDate = incidentDate;
        this.description  = description;
        this.status       = status;
        this.systemId     = systemId;
        this.satelliteId  = satelliteId;
        this.droneId      = droneId;
    }

    public int       getIncidentId()          { return incidentId; }
    public void      setIncidentId(int id)    { this.incidentId = id; }

    public String    getIncidentType()         { return incidentType; }
    public void      setIncidentType(String s) { this.incidentType = s; }

    public String    getSeverity()         { return severity; }
    public void      setSeverity(String s) { this.severity = s; }

    public Timestamp getIncidentDate()          { return incidentDate; }
    public void      setIncidentDate(Timestamp t){ this.incidentDate = t; }

    public String    getDescription()         { return description; }
    public void      setDescription(String s) { this.description = s; }

    public String    getStatus()         { return status; }
    public void      setStatus(String s) { this.status = s; }

    public Integer   getSystemId()        { return systemId; }
    public void      setSystemId(Integer i){ this.systemId = i; }

    public Integer   getSatelliteId()        { return satelliteId; }
    public void      setSatelliteId(Integer i){ this.satelliteId = i; }

    public Integer   getDroneId()        { return droneId; }
    public void      setDroneId(Integer i){ this.droneId = i; }
}