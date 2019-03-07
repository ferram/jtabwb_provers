package ipl.frj.launcher;

import jtabwbx.prop.formula.Formula;

public class InitialNodeSetData {

  public InitialNodeSetData(Formula goal, long initial_node_set_construction_start_time,
      long initial_node_set_construction_end_time) {
    super();
    this.goal = goal;
    this.initial_node_set_construction_start_time = initial_node_set_construction_start_time;
    this.initial_node_set_construction_end_time = initial_node_set_construction_end_time;
  }

  private Formula goal;
  private long initial_node_set_construction_start_time = 0;
  private long initial_node_set_construction_end_time = -1;

  /**
   * @return the goal
   */
  public Formula getGoal() {
    return this.goal;
  }

  /**
   * @return the initial_node_set_construction_start_time
   */
  public long getInitial_node_set_construction_start_time() {
    return this.initial_node_set_construction_start_time;
  }

  /**
   * @return the initial_node_set_construction_end_time
   */
  public long getInitial_node_set_construction_end_time() {
    return this.initial_node_set_construction_end_time;
  }

  /**
   * Returns the time required to build the initial node set.
   * 
   * @return the time needed to build the initial node set.
   */
  public long initialNodeSetConstructionTime() {
    return initial_node_set_construction_end_time - initial_node_set_construction_start_time;
  }
}
