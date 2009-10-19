package aima.test.core.unit.search.online;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import aima.core.agent.Action;
import aima.core.agent.EnvironmentState;
import aima.core.agent.EnvironmentView;
import aima.core.search.map.BidirectionalMapProblem;
import aima.core.search.map.ExtendableMap;
import aima.core.search.map.MapEnvironment;
import aima.core.search.online.OnlineDFSAgent;

public class OnlineDFSAgentTest {

	ExtendableMap aMap;

	StringBuffer envChanges;

	@Before
	public void setUp() {
		aMap = new ExtendableMap();
		aMap.addBidirectionalLink("A", "B", 5.0);
		aMap.addBidirectionalLink("A", "C", 6.0);
		aMap.addBidirectionalLink("B", "D", 4.0);
		aMap.addBidirectionalLink("B", "E", 7.0);
		aMap.addBidirectionalLink("D", "F", 4.0);
		aMap.addBidirectionalLink("D", "G", 8.0);

		envChanges = new StringBuffer();
	}

	@Test
	public void testAlreadyAtGoal() {
		MapEnvironment me = new MapEnvironment(aMap);
		OnlineDFSAgent agent = new OnlineDFSAgent(new BidirectionalMapProblem(
				me.getMap(), "A", "A"));
		me.addAgent(agent, "A");
		me.addEnvironmentView(new EnvironmentView() {
			public void notify(String msg) {
				envChanges.append(msg).append("->");
			}

			public void envChanged(Action action, EnvironmentState state) {
				envChanges.append(action).append("->");
			}
		});
		me.stepUntilDone();

		Assert.assertEquals("Action[name==NoOp]->", envChanges.toString());
	}

	@Test
	public void testNormalSearch() {
		MapEnvironment me = new MapEnvironment(aMap);
		OnlineDFSAgent agent = new OnlineDFSAgent(new BidirectionalMapProblem(
				me.getMap(), "A", "G"));
		me.addAgent(agent, "A");
		me.addEnvironmentView(new EnvironmentView() {
			public void notify(String msg) {
				envChanges.append(msg).append("->");
			}

			public void envChanged(Action action, EnvironmentState state) {
				envChanges.append(action).append("->");
			}
		});
		me.stepUntilDone();

		Assert
				.assertEquals(
						"Action[name==moveTo, location==C, distance==6.0]->Action[name==moveTo, location==A, distance==6.0]->Action[name==moveTo, location==B, distance==5.0]->Action[name==moveTo, location==E, distance==7.0]->Action[name==moveTo, location==B, distance==7.0]->Action[name==moveTo, location==D, distance==4.0]->Action[name==moveTo, location==G, distance==8.0]->Action[name==NoOp]->",
						envChanges.toString());
	}

	@Test
	public void testNoPath() {
		// TODO - The OnlineDFSAgent as it is currently written
		// goes into a never ending loop if there is not goal!
		// Need to fix.
		aMap = new ExtendableMap();
		aMap.addBidirectionalLink("A", "B", 1.0);
		MapEnvironment me = new MapEnvironment(aMap);
		OnlineDFSAgent agent = new OnlineDFSAgent(new BidirectionalMapProblem(
				me.getMap(), "A", "X"));
		me.addAgent(agent, "A");
		me.addEnvironmentView(new EnvironmentView() {
			public void notify(String msg) {
				envChanges.append(msg).append("->");
			}

			public void envChanged(Action action, EnvironmentState state) {
				envChanges.append(action).append("->");
			}
		});
		
		// Note: Will not exit as can't find goal, therefore
		// limit number of steps in order to demonstrate
		// oscillation between existing nodes.
		me.step(5);

		Assert.assertEquals("Action[name==moveTo, location==B, distance==1.0]->Action[name==moveTo, location==A, distance==1.0]->Action[name==moveTo, location==B, distance==1.0]->Action[name==moveTo, location==A, distance==1.0]->Action[name==moveTo, location==B, distance==1.0]->", envChanges.toString());
	}

	@Test
	public void testAIMA2eFig4_18() {
		aMap = new ExtendableMap();
		aMap.addBidirectionalLink("1,1", "1,2", 1.0);
		aMap.addBidirectionalLink("1,1", "2,1", 1.0);
		aMap.addBidirectionalLink("1,2", "1,3", 1.0);
		aMap.addBidirectionalLink("1,2", "2,2", 1.0);
		aMap.addBidirectionalLink("1,3", "2,3", 1.0);
		aMap.addBidirectionalLink("2,2", "3,2", 1.0);
		aMap.addBidirectionalLink("2,3", "3,3", 1.0);
		aMap.addBidirectionalLink("3,1", "3,2", 1.0);

		MapEnvironment me = new MapEnvironment(aMap);
		OnlineDFSAgent agent = new OnlineDFSAgent(new BidirectionalMapProblem(
				me.getMap(), "1,1", "3,3"));
		me.addAgent(agent, "1,1");
		me.addEnvironmentView(new EnvironmentView() {
			public void notify(String msg) {
				envChanges.append(msg).append("->");
			}

			public void envChanged(Action action, EnvironmentState state) {
				envChanges.append(action).append("->");
			}
		});
		me.stepUntilDone();

		Assert
				.assertEquals(
						"Action[name==moveTo, location==2,1, distance==1.0]->Action[name==moveTo, location==1,1, distance==1.0]->Action[name==moveTo, location==1,2, distance==1.0]->Action[name==moveTo, location==2,2, distance==1.0]->Action[name==moveTo, location==3,2, distance==1.0]->Action[name==moveTo, location==3,1, distance==1.0]->Action[name==moveTo, location==3,2, distance==1.0]->Action[name==moveTo, location==2,2, distance==1.0]->Action[name==moveTo, location==1,2, distance==1.0]->Action[name==moveTo, location==1,3, distance==1.0]->Action[name==moveTo, location==2,3, distance==1.0]->Action[name==moveTo, location==3,3, distance==1.0]->Action[name==NoOp]->",
						envChanges.toString());
	}
}
