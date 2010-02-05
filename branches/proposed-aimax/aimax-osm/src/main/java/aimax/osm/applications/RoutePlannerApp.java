package aimax.osm.applications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import aimax.osm.data.DataResource;
import aimax.osm.data.MapDataEvent;
import aimax.osm.data.MapDataEventListener;
import aimax.osm.data.MapDataStore;
import aimax.osm.reader.MapReader;
import aimax.osm.reader.OsmReader;
import aimax.osm.routing.RouteCalculator;
import aimax.osm.viewer.MapViewFrame;

/**
 * Implements a simple route planning tool. It extends the OSM map viewer
 * by a little search engine for shortest paths using the A* algorithm.
 * Set two marks (mouse left) before starting route calculation!
 * @author R. Lunde
 */
public class RoutePlannerApp implements ActionListener {
	protected MapViewFrame frame;
	protected JComboBox waySelection;
	protected JButton calcButton;
	protected RouteCalculator routeCalculator;
	
	public RoutePlannerApp(InputStream osmFile) {
		MapReader mapReader = new OsmReader();
		frame = new MapViewFrame(mapReader, osmFile);
		frame.setTitle("OSM Route Planner");
		routeCalculator = new RouteCalculator();
		JToolBar toolbar = frame.getToolbar();
		toolbar.addSeparator();
		waySelection = new JComboBox(new String[]{"Distance", "Distance (Car)", "Distance (Bike)"});
		toolbar.add(waySelection);
		toolbar.addSeparator();
		calcButton = new JButton("Calculate Route");
		calcButton.setEnabled(frame.getMapData().getMarks().size() >= 2);
		calcButton.addActionListener(this);
		toolbar.add(calcButton);
		
		frame.getMapData().addMapDataEventListener(new MapDataEventHandler());
	}
	
	public void showFrame() {
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	/** Starts route generation after the calculate button has been pressed. */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == calcButton) {
			MapDataStore mapData = frame.getMapData();
			routeCalculator.calculateRoute(mapData.getMarks(), mapData,
					waySelection.getSelectedIndex());
		}
	}
	
	/**
	 * Updates the info field based on events sent by the MapViewPane. 
	 * @author R. Lunde
	 */
	class MapDataEventHandler implements MapDataEventListener {
		@Override
		public void eventHappened(MapDataEvent event) {
			calcButton.setEnabled(frame.getMapData().getMarks().size() > 1);
		}
	}
	
	
	public static void main(String[] args) {
		// indicates progress when reading large maps (for testing only)
		// Logger.getLogger("aimax.osm").setLevel(Level.FINEST);
		// Logger.getLogger("").getHandlers()[0].setLevel(Level.FINE);
		
		RoutePlannerApp demo = new RoutePlannerApp(DataResource.getULMFileResource());
		demo.showFrame();
	}
}
