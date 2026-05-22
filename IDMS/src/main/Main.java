package main;

import ui.Dashboard;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // Use system look-and-feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default Swing L&F silently
        }

        // Launch the dashboard on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new Dashboard());
    }
}