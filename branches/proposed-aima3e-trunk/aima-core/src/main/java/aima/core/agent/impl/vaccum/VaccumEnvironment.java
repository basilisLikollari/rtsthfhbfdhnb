package aima.core.agent.impl.vaccum;

import java.util.Random;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.Percept;
import aima.core.agent.impl.AbstractEnvironment;
import aima.core.agent.impl.DynamicAction;

/**
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class VaccumEnvironment extends AbstractEnvironment {
	// Allowable Actions within the Vaccum Environment
	public static final Action ACTION_MOVE_LEFT = new DynamicAction("Left");
	public static final Action ACTION_MOVE_RIGHT = new DynamicAction("Right");
	public static final Action ACTION_SUCK = new DynamicAction("Suck");

	//
	public enum Location {
		A, B
	};

	public enum LocationState {
		Clean, Dirty
	};

	//
	private VaccumEnvironmentState envState = null;
	private boolean isDone = false; 

	public VaccumEnvironment() {
		Random r = new Random();
		envState = new VaccumEnvironmentState(
				0 == r.nextInt(2) ? LocationState.Clean : LocationState.Dirty,
				0 == r.nextInt(2) ? LocationState.Clean : LocationState.Dirty);
	}

	public VaccumEnvironment(LocationState locAState, LocationState locBState) {
		envState = new VaccumEnvironmentState(locAState, locBState);
	}

	@Override
	public EnvironmentState executeAction(Agent a, Action agentAction) {

		if (ACTION_MOVE_RIGHT == agentAction) {
			envState.setAgentLocation(a, Location.B);
			updatePerformanceMeasure(a, -1);
		} else if (ACTION_MOVE_LEFT == agentAction) {
			envState.setAgentLocation(a, Location.A);
			updatePerformanceMeasure(a, -1);
		} else if (ACTION_SUCK == agentAction) {
			if (LocationState.Dirty == envState.getLocationState(envState.getAgentLocation(a))) {
				envState.setLocationState(envState.getAgentLocation(a), LocationState.Clean);
				updatePerformanceMeasure(a, 10);
			}
		} else if (agentAction.isNoOp()) {
			// In the Vacuum Environment we consider things done if
			// the agent generates a NoOp.
			isDone = true;
		}
		
		return envState;
	}

	@Override
	public Percept getPerceptSeenBy(Agent anAgent) {
		Location agentLocation = envState.getAgentLocation(anAgent);
		return new VaccumEnvPercept(agentLocation, envState.getLocationState(agentLocation));
	}
	
	@Override
	public boolean isDone() {
		return super.isDone() || isDone;
	}

	public void addAgent(Agent a, Location location) {
		envState.setAgentLocation(a, location);
		addAgent(a);
	}

	public LocationState getLocationState(Location location) {
		return envState.getLocationState(location);
	}

	public Location getAgentLocation(Agent a) {
		return envState.getAgentLocation(a);
	}
}