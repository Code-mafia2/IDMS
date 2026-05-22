# IDMS — How Everything Works (For a Java Beginner)

> Think of this project like a **digital register** for a defense organization. You fill in fields on a screen, click a button, and the data gets saved into a database. This document explains every file and every step of that journey.

---

## 1. Big Picture — The 5 Layers of This Project

```
┌─────────────────────────────────────────────────────┐
│  LAYER 1 — main/Main.java                           │  ← Starts the app
├─────────────────────────────────────────────────────┤
│  LAYER 2 — ui/ (Dashboard, IncidentForm, etc.)      │  ← What you SEE on screen
├─────────────────────────────────────────────────────┤
│  LAYER 3 — dao/ (IncidentDAO, DroneDAO, etc.)       │  ← Handles DB operations
├─────────────────────────────────────────────────────┤
│  LAYER 4 — model/ (Incident.java, Drone.java, etc.) │  ← Data containers
├─────────────────────────────────────────────────────┤
│  LAYER 5 — db/DBConnection.java                     │  ← Talks to PostgreSQL
└─────────────────────────────────────────────────────┘
```

Data flows **down** when saving (UI → DAO → DB) and **up** when loading (DB → DAO → UI).

---

## 2. The Database Connection — `db/DBConnection.java`

```java
private static final String URL      = "jdbc:postgresql://localhost:5432/idms";
private static final String USER     = "postgres";
private static final String PASSWORD = "root";
```

**What is this?**
This file is like a **phone book entry** for your database. It stores:
- **URL** — the address of your database (`localhost` = your own computer, `5432` = the port PostgreSQL listens on, `idms` = name of the database)
- **USER / PASSWORD** — your PostgreSQL login credentials

```java
public static Connection getConnection() throws SQLException {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(URL, USER, PASSWORD);
}
```

**What does `getConnection()` do?**
- `Class.forName("org.postgresql.Driver")` — loads the PostgreSQL driver (a piece of code that knows how to talk to PostgreSQL). Think of it as "dialing" the database.
- `DriverManager.getConnection(...)` — actually opens a live connection (like a phone call being answered).
- Every time you click Insert/Update/Delete, this method is called to open a fresh connection, and it is automatically closed afterward.

---

## 3. The Models — `model/` folder

These are simple **data containers**. They hold one row of data from a database table.

### Example: `model/Incident.java`

```java
public class Incident {
    private int       incidentId;
    private String    incidentType;
    private String    severity;
    private Timestamp incidentDate;
    private String    description;
    private String    status;
    private Integer   systemId;    // can be null
    private Integer   satelliteId; // can be null
    private Integer   droneId;     // can be null
}
```

**What are these?**
Each `private` variable is like a **column in your database table**. The `Incident` class represents one row in the `Incident` table.

**What are getters and setters?**
```java
public String getSeverity()         { return severity; }
public void   setSeverity(String s) { this.severity = s; }
```
- **Getter** (`getSeverity`) — gives you the value stored inside. Like reading a sticky note.
- **Setter** (`setSeverity`) — lets you change the value. Like writing on the sticky note.

Since the variables are `private`, nobody outside the class can touch them directly. They must go through getters/setters. This is called **encapsulation**.

**Why `Integer` instead of `int` for systemId/satelliteId/droneId?**
`int` cannot be `null`. `Integer` (capital I) CAN be null. These IDs are optional — an incident may not be linked to any system, satellite, or drone.

### Other Models

| File | Represents | Key Fields |
|---|---|---|
| `Organization.java` | A defense org | orgId, orgName, location, contactEmail |
| `Drone.java` | A drone | droneId, droneName, droneType, status, rangeKm, orgId |
| `Satellite.java` | A satellite | satelliteId, satelliteName, orbitType, launchDate, monitoringRegion, orgId |
| `SystemModel.java` | A defense system | systemId, systemName, systemType, status, orgId |

---

## 4. The DAO Layer — `dao/` folder

**DAO = Data Access Object.** This is the layer that contains all the SQL queries. The UI never writes SQL — it just calls methods in the DAO.

### How IncidentDAO Works — Detailed Walkthrough

#### 4.1 INSERT

```java
public boolean insert(Incident inc) {
    String sql = "INSERT INTO Incident (incident_type, severity, ...) VALUES (?, ?, ...)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, inc.getIncidentType());
        ps.setString(2, inc.getSeverity());
        ...
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Step by step:**

1. **`String sql = "INSERT INTO ... VALUES (?, ?, ...)"`**
   - This is the SQL command. The `?` marks are **placeholders** — like blanks in a form that you fill in later.
   - We never put values directly in the SQL string (e.g., `VALUES ('Fire', 'High')`) because that would be a **SQL Injection** security risk.

2. **`DBConnection.getConnection()`**
   - Opens a live connection to PostgreSQL.

3. **`conn.prepareStatement(sql)`**
   - Sends the SQL template to the database so it can prepare/compile it. The `?`s are ready to be filled.

4. **`ps.setString(1, inc.getIncidentType())`**
   - Fills in the 1st `?` with the incident type text.
   - `ps.setString` = for text. `ps.setInt` = for numbers. `ps.setTimestamp` = for date+time.

5. **`ps.executeUpdate()`**
   - Fires the query at the database. The row actually gets inserted now.

6. **`return true`**
   - Tells the caller (the button handler in the UI) that it worked.

7. **`catch (SQLException e)`**
   - If anything went wrong (DB is down, wrong data type, etc.), it prints the error to the console and returns `false`.

8. **`try ( ... )`** — *try-with-resources*
   - The connection (`conn`) and the statement (`ps`) are automatically closed when done. You don't need to write `conn.close()` manually.

#### 4.2 UPDATE

```java
String sql = "UPDATE Incident SET incident_type=?, severity=?, ... WHERE incident_id=?";
...
ps.setInt(9, inc.getIncidentId()); // The last ? is the ID to find the row
ps.executeUpdate();
```

Same idea as INSERT, except:
- We tell the DB **which row** to update using `WHERE incident_id=?`
- The ID is always set as the **last** parameter.

#### 4.3 DELETE

```java
String sql = "DELETE FROM Incident WHERE incident_id=?";
ps.setInt(1, incidentId);
ps.executeUpdate();
```

Simple — just pass the ID and the row matching that ID gets removed.

#### 4.4 GET ALL (Read)

```java
public List<Incident> getAll() {
    List<Incident> list = new ArrayList<>();
    String sql = "SELECT * FROM Incident ORDER BY incident_id";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(new Incident(
                rs.getInt("incident_id"),
                rs.getString("incident_type"),
                ...
            ));
        }
    }
    return list;
}
```

**What is a `ResultSet`?**
Think of it like a **cursor** pointing to the results returned by the database. It starts before the first row.

- `rs.next()` — moves to the next row. Returns `true` if there is a row, `false` when rows are finished.
- `rs.getInt("incident_id")` — reads the `incident_id` column from the current row.
- `rs.getString("incident_type")` — reads a text column.

Each row creates a new `Incident` object which is added to the `list`. The list is returned to the UI so it can display all records in the table.

---

## 5. The App Entry Point — `main/Main.java`

```java
public static void main(String[] args) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SwingUtilities.invokeLater(() -> new Dashboard());
}
```

- `main` is where Java starts. Every Java application begins here.
- `UIManager.setLookAndFeel(...)` — makes the buttons and windows look like native Windows/Mac windows instead of the default grey Swing look.
- `SwingUtilities.invokeLater(...)` — safely starts the UI on a special thread called the **Event Dispatch Thread (EDT)**. Swing GUIs must run on the EDT to avoid display bugs. The `() -> new Dashboard()` is a **lambda** — a shorthand way of saying "run this code later".

---

## 6. The Dashboard — `ui/Dashboard.java`

```java
public class Dashboard extends JFrame implements ActionListener {
```

- **`JFrame`** — this IS the window. Extending it means `Dashboard` is a window.
- **`implements ActionListener`** — means this class can listen for button clicks. You must implement `actionPerformed()`.

**How buttons are created:**
```java
private JButton createButton(String label, int x, int y, Color bg, Color fg, Font font) {
    JButton btn = new JButton(label);
    btn.setBounds(x, y, 245, 55);  // position and size on screen
    btn.addActionListener(this);    // "tell me when this is clicked"
    add(btn);                       // add button to the window
    return btn;
}
```

**How clicks are handled:**
```java
@Override
public void actionPerformed(ActionEvent e) {
    Object src = e.getSource(); // which button was clicked?
    if (src == btnOrganization) new OrganizationForm();
    else if (src == btnIncident) new IncidentForm();
    // ... etc
}
```
- `e.getSource()` returns the button object that was clicked.
- We compare it to our stored button references to figure out which one.
- Then we open the matching form window.

---

## 7. IncidentForm — Full Journey from TextField to Database

This is the most important section. Let's trace **exactly** what happens when you type data and click "Insert".

### 7.1 The Class Structure

```java
public class IncidentForm extends JFrame implements ActionListener {
    private JTextField txtIncidentType;   // input box
    private JTextField txtSeverity;
    // ... more fields

    private JButton btnInsert;
    // ... more buttons

    private JTable            table;       // the grid at the bottom
    private DefaultTableModel tableModel;  // the data behind the grid

    private final IncidentDAO dao = new IncidentDAO(); // DB helper
}
```

When `IncidentForm` is created (when you click "Incidents" on Dashboard), the constructor runs:
```java
public IncidentForm() {
    setTitle("Incident Management");
    setSize(1050, 730);
    buildHeader();      // draws the red top bar
    buildInputPanel();  // draws all text fields
    buildButtons();     // draws Insert/Update/Delete/View All/Clear
    buildTable();       // draws the grid at the bottom
    loadData();         // immediately fetches all records from DB and shows them
    setVisible(true);   // makes the window appear
}
```

### 7.2 Building the Input Fields

```java
private void buildInputPanel() {
    addLabel("Incident Type:", 30, 110);
    txtIncidentType = addField(200, 110, 200); // x=200, y=110, width=200
}

private JTextField addField(int x, int y, int width) {
    JTextField tf = new JTextField();
    tf.setBounds(x, y, width, 28); // place it at exact pixel position
    add(tf);    // add to the window
    return tf;  // return so we can refer to it later (e.g., txtIncidentType)
}
```

`JTextField` is a single-line text input box. `setBounds(x, y, width, height)` places it at an exact pixel position on screen (since `setLayout(null)` means absolute positioning).

### 7.3 Building the Buttons

```java
private void buildButtons() {
    btnInsert = addButton("Insert", 30, 280, new Color(34, 139, 34)); // green
    btnUpdate = addButton("Update", 175, 280, new Color(30, 100, 180)); // blue
    // ...
}

private JButton addButton(String label, int x, int y, Color bg) {
    JButton btn = new JButton(label);
    btn.setBounds(x, y, 130, 32);
    btn.setBackground(bg);
    btn.addActionListener(this); // ← THIS is what makes the button "clickable"
    add(btn);
    return btn;
}
```

`btn.addActionListener(this)` — "When this button is clicked, call `actionPerformed()` on this form." The `this` refers to `IncidentForm` itself (since it `implements ActionListener`).

### 7.4 The Table (Grid Display)

```java
tableModel = new DefaultTableModel(
    new String[]{"ID","Type","Severity","Date","Description","Status","SysID","SatID","DrnID"}, 0
) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
};
table = new JTable(tableModel);
```

- `DefaultTableModel` is the **data** behind the grid (rows and columns).
- `JTable` is the **visual** grid on screen.
- `isCellEditable` returning `false` makes every cell read-only — you can't type directly into the table.

**Clicking a row auto-fills the fields:**
```java
table.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtIncidentId.setText(safeGet(row, 0));
            txtIncidentType.setText(safeGet(row, 1));
            // ... fills all fields from that row
        }
    }
});
```
When you click a row, `table.getSelectedRow()` gives the row index, and we read each column value and push it into the matching text field. This is how you select a record for Update or Delete.

### 7.5 The Full INSERT Journey — Step by Step

**Step 1 — You type in the fields and click "Insert".**

**Step 2 — `actionPerformed()` is called by Java automatically.**
```java
public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btnInsert) doInsert();
    // ...
}
```

**Step 3 — `doInsert()` runs.**
```java
private void doInsert() {
    Incident inc = buildFromFields(0); // id=0 because DB will auto-assign
    if (inc == null) return; // timestamp was bad, abort

    if (dao.insert(inc)) {
        JOptionPane.showMessageDialog(this, "Incident inserted successfully!", ...);
        loadData();    // refresh the table
        clearFields(); // empty the text boxes
    } else {
        JOptionPane.showMessageDialog(this, "Insert failed.", ...);
    }
}
```

**Step 4 — `buildFromFields()` collects all text field values.**
```java
private Incident buildFromFields(int id) {
    Timestamp ts = parseTimestamp(); // converts "2024-06-15 09:30:00" → Timestamp object
    return new Incident(id,
        txtIncidentType.getText().trim(),  // .trim() removes leading/trailing spaces
        txtSeverity.getText().trim(),
        ts,
        txtDescription.getText().trim(),
        txtStatus.getText().trim(),
        parseNullableId(txtSystemId),      // converts "5" → Integer 5, "" → null
        parseNullableId(txtSatelliteId),
        parseNullableId(txtDroneId)
    );
}
```

**Step 5 — `dao.insert(inc)` is called.** (See Section 4.1 for full details.)

The DAO:
- Opens a DB connection
- Builds the `INSERT` SQL with `?` placeholders
- Fills in the `?`s from the `Incident` object's getters
- Fires `executeUpdate()` → row is saved in PostgreSQL
- Returns `true` or `false`

**Step 6 — Back in `doInsert()`, success message is shown and `loadData()` refreshes the table.**

```java
private void loadData() {
    tableModel.setRowCount(0); // clears all existing rows from the grid
    List<Incident> list = dao.getAll(); // fetches fresh data from DB
    for (Incident inc : list) {
        tableModel.addRow(new Object[]{
            inc.getIncidentId(), inc.getIncidentType(), ...
        });
    }
}
```

The new record now appears in the table.

---

### 7.6 UPDATE Flow

1. You **click a row** in the table → fields are auto-filled (including the ID).
2. You **change** something in a field.
3. You click **Update**.
4. `doUpdate()` reads the ID from `txtIncidentId`, builds an `Incident` object with the ID set, and calls `dao.update(inc)`.
5. The DAO runs `UPDATE Incident SET ... WHERE incident_id=?` — only the row with that ID is changed.

### 7.7 DELETE Flow

1. Click a row → ID is filled in.
2. Click **Delete**.
3. A confirmation dialog appears (`JOptionPane.showConfirmDialog`).
4. If you click Yes → `dao.delete(id)` → runs `DELETE FROM Incident WHERE incident_id=?`.

---

## 8. The Other Forms — `OrganizationForm`, `DroneForm`, `SatelliteForm`, `SystemForm`

They all work **identically** to `IncidentForm`. The only differences are:
- Different fields matching their database table
- Different DAO class used (e.g., `OrganizationDAO`)
- Different colors for the header panel

The pattern is always the same:
```
Build UI → Load Data → User Clicks Button → actionPerformed() → do___() → DAO method → DB → Refresh Table
```

### 8.1 The "Joint View"

The `OrganizationForm` has an extra button called **"Joint View"**. Clicking it opens `OrgIncidentJointView.java`. This is a special, read-only window that uses an **SQL JOIN query** to combine data from multiple tables (`Organization`, `Incident`, and intermediate tables like `Drone` or `Satellite`) into a single view. It demonstrates how to fetch complex relational data without using the standard DAO methods.

---

## 9. Null / Optional Fields Explained

For Incident, the `systemId`, `satelliteId`, and `droneId` are optional. The user might not know which drone was involved.

```java
private Integer parseNullableId(JTextField tf) {
    String raw = tf.getText().trim();
    if (raw.isEmpty()) return null;    // blank = no value
    try {
        int val = Integer.parseInt(raw);
        return val > 0 ? val : null;   // 0 or negative = treat as no value
    } catch (NumberFormatException ex) {
        return null; // user typed letters — ignore it
    }
}
```

In the DAO:
```java
if (inc.getSystemId() != null && inc.getSystemId() > 0) {
    ps.setInt(6, inc.getSystemId());   // set the actual ID
} else {
    ps.setNull(6, Types.INTEGER);      // insert NULL into the database column
}
```

`ps.setNull(position, type)` tells the database "this column has no value for this row."

---

## 10. Complete File Reference

| File | Purpose |
|---|---|
| `main/Main.java` | Entry point. Starts the app, opens Dashboard |
| `db/DBConnection.java` | Opens a connection to PostgreSQL |
| `model/Incident.java` | Holds one incident's data |
| `model/Organization.java` | Holds one organization's data |
| `model/Drone.java` | Holds one drone's data |
| `model/Satellite.java` | Holds one satellite's data |
| `model/SystemModel.java` | Holds one system's data |
| `dao/IncidentDAO.java` | SQL: INSERT, UPDATE, DELETE, SELECT for Incident |
| `dao/OrganizationDAO.java` | SQL: INSERT, UPDATE, DELETE, SELECT for Organization |
| `dao/DroneDAO.java` | SQL: INSERT, UPDATE, DELETE, SELECT for Drone |
| `dao/SatelliteDAO.java` | SQL: INSERT, UPDATE, DELETE, SELECT for Satellite |
| `dao/SystemDAO.java` | SQL: INSERT, UPDATE, DELETE, SELECT for System |
| `ui/Dashboard.java` | Main menu window with 5 navigation buttons |
| `ui/IncidentForm.java` | Full CRUD form for Incidents |
| `ui/OrganizationForm.java` | Full CRUD form for Organizations (includes Joint View button) |
| `ui/DroneForm.java` | Full CRUD form for Drones |
| `ui/SatelliteForm.java` | Full CRUD form for Satellites |
| `ui/SystemForm.java` | Full CRUD form for Systems |
| `ui/OrgIncidentJointView.java` | Special read-only view joining Organizations and Incidents |

---

## 11. Key Java / Swing Terms Glossary

| Term | Plain English Meaning |
|---|---|
| `JFrame` | A window |
| `JPanel` | A rectangle/container inside a window |
| `JLabel` | A piece of text on screen (not editable) |
| `JTextField` | A box where users type text |
| `JButton` | A clickable button |
| `JTable` | A grid/spreadsheet widget |
| `DefaultTableModel` | The data storage behind a JTable |
| `JScrollPane` | Adds a scrollbar to a component |
| `ActionListener` | An interface that lets a class respond to button clicks |
| `actionPerformed()` | The method called automatically when a button is clicked |
| `Connection` | A live link to the database |
| `PreparedStatement` | A pre-compiled SQL query with `?` placeholders |
| `ResultSet` | The rows returned by a SELECT query |
| `try-with-resources` | Automatically closes Connection/Statement when done |
| `JOptionPane` | A pop-up dialog box (info, warning, confirm) |
| `setBounds(x,y,w,h)` | Places a component at exact pixel coordinates |
| `setLayout(null)` | Disables automatic layout; you position everything manually |
| `SwingUtilities.invokeLater` | Safely starts the GUI on the correct thread |
| `instanceof` / `getSource()` | Figuring out which button was clicked |
| `trim()` | Removes whitespace from start and end of a String |
| `parseInt()` | Converts a String like `"42"` into the number `42` |
