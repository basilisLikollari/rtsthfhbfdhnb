package aimax.osm.applications;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.SimpleAgentApp;
import aimax.osm.data.MapDataStore;
import aimax.osm.reader.MapReader;
import aimax.osm.reader.OsmReader;
import aimax.osm.routing.agent.OsmAgentController;
import aimax.osm.routing.agent.OsmAgentFrame;
import aimax.osm.routing.agent.OsmAgentView;
import aimax.osm.routing.agent.OsmMap;

/**
 * Demonstrates, how the OSM map viewer can be integrated into a graphical
 * agent application.
 * @author R. Lunde
 */
public class OsmAgentApp extends SimpleAgentApp {

	protected OsmMap map;
	
	/** Reads a map from the specified file and stores it in {@link #map}. */
	public OsmAgentApp(File osmFile) {
		MapDataStore mapData = new MapDataStore();
		MapReader mapReader = new OsmReader();
		mapReader.readMap(osmFile, mapData);
		map = new OsmMap(mapData);
	}
	
	/** Creates an <code>OsmAgentView</code>. */
	public OsmAgentView createEnvironmentView() {
		return new OsmAgentView(map.getMapData());
	}

	/** Factory method, which creates and configures a <code>OsmMapAgentFrame</code>. */
	@Override
	public AgentAppFrame createFrame() {
		return new OsmAgentFrame(map);
	}

	/** Factory method, which creates a <code>OsmMapAgentController</code>. */
	@Override
	public AgentAppController createController() {
		return new OsmAgentController(map);
	}

	
	/////////////////////////////////////////////////////////////////
	// starter method

	/** Application starter. */
	public static void main(String args[]) {
		Logger.getLogger("aimax.osm").setLevel(Level.FINEST);
		Logger.getLogger("").getHandlers()[0].setLevel(Level.FINE);
		
		File file = new File("src/main/resource/maps/ulm.osm");
		OsmAgentApp demo = new OsmAgentApp(file);
		demo.startApplication();
	}
}