package nz.auckland.arch.planner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.heuristics.relaxation.Heuristic;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.planners.statespace.StateSpacePlanner;
import fr.uga.pddl4j.planners.statespace.ff.FF;
import fr.uga.pddl4j.planners.statespace.generic.GenericPlanner;
import fr.uga.pddl4j.planners.statespace.search.strategy.AStar;
import fr.uga.pddl4j.planners.statespace.search.strategy.BreadthFirstSearch;
import fr.uga.pddl4j.planners.statespace.search.strategy.DepthFirstSearch;
import fr.uga.pddl4j.planners.statespace.search.strategy.EnforcedHillClimbing;
import fr.uga.pddl4j.planners.statespace.search.strategy.GreedyBestFirstSearch;
import fr.uga.pddl4j.planners.statespace.search.strategy.GreedyBestFirstSearchAnytime;
import fr.uga.pddl4j.planners.statespace.search.strategy.HillClimbing;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.SequentialPlan;
import nz.auckland.arch.Component;
import nz.auckland.arch.Connector;
import nz.auckland.arch.DesignModel;
import nz.auckland.arch.Device;
import nz.auckland.arch.ExecutionEnvironment;
import nz.auckland.arch.HostType;
import nz.auckland.arch.NetAccessType;
import nz.auckland.arch.NodeType;
import nz.auckland.arch.Port;
import nz.auckland.arch.Role;
import nz.auckland.arch.planner.object.Action;
import nz.auckland.arch.planner.object.ComponentLinkage;
import nz.auckland.arch.planner.object.Parameter;
import nz.auckland.arch.planner.object.Plan;

public class RefactorPlanner implements Runnable{


	private static final String PDDL_URL = "/home/nacha/pddl4j/pddl/";
	//private static final String PDDL_URL = "C:/config/pddl/";
	
	private String problemStr;
	
	private Plan refactorPlan;

	public RefactorPlanner(String problemStr) {
		this.problemStr = problemStr;
	}
	

	
	public Plan getRefactorPlan() {
		return refactorPlan;
	}



	public void run() {
		System.out.println("RefactorPlanner started...");
		long start = System.currentTimeMillis();
		// generate problem
		try {
			
			System.out.println(problemStr);

			String domainStr = readDomain();
			
			final ProblemFactory factory = ProblemFactory.getInstance();
			System.out.println("Factory initiates....");
			ErrorManager errorManager = factory.parseFromString(domainStr, problemStr);
//			if (!errorManager.isEmpty()) {
//				errorManager.printAll();
//			}

			// execute planner
			//EnforcedHillClimbing stateSpaceStrategy = new EnforcedHillClimbing(15 * 1000, Heuristic.Type.FAST_FORWARD, 1);
			//AStar stateSpaceStrategy = new AStar(15 * 1000, Heuristic.Type.FAST_FORWARD, 1);
			//GreedyBestFirstSearch stateSpaceStrategy = new   GreedyBestFirstSearch(15 * 1000, Heuristic.Type.FAST_FORWARD, 1);
			BreadthFirstSearch stateSpaceStrategy = new BreadthFirstSearch();
		    GenericPlanner  planner = new GenericPlanner(stateSpaceStrategy);
			
			CodedProblem pb = factory.encode();
			SequentialPlan plan = planner.search(pb);
			System.out.println("Output:"+plan.actions().size());
			

			// print plan
			refactorPlan = new Plan();
			System.out.println("#### start given plan.....");
			int count =0;
			for (BitOp op : plan.actions()) {
				Action action = new Action(count, op.getName());
				System.out.print(action.getName());
				 for (int i = 0; i < op.getArity(); i++) {
			             int index = op.getValueOfParameter(i);
			             String type = pb.getTypes().get(op.getTypeOfParameters(i));
			           
			             if (index != -1) {
			            	 System.out.print(type +":"+pb.getConstants().get(index));
			            	 action.getParameters().add(new Parameter(type, pb.getConstants().get(index)));
			             } else {
			            	 System.out.print(type +":"+ "?");
			            	 action.getParameters().add(new Parameter(type, "?"));
			             }
			             
				 }
				 System.out.println();
				 refactorPlan.addStep(action);
				 count++;
				
			}
			System.out.println("#### end given plan.....");
			long finish = System.currentTimeMillis();
			System.out.println("Elapse Time: "+ (finish-start));
			 return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refactorPlan = new Plan();

	}

	private String readDomain() throws Exception {
		File domainFile = new File(PDDL_URL + "arch.pddl");
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(domainFile));

			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			// delete the last new line separator
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);

		} finally {
			if (reader != null)
				reader.close();
		}

		return stringBuilder.toString();
	}


}
