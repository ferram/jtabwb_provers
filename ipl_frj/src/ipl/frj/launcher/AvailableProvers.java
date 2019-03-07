package ipl.frj.launcher;

import ferram.rtoptions._NamedArgument;
import ipl.frj.tp.basic._FrjProver;
import jtabwb.util.ImplementationError;
import jtabwbx.prop.formula.Formula;

public enum AvailableProvers implements _NamedArgument<AvailableProvers> {
  ONE_LEVEL_ITERATION("oli", "one level iteration prover: at any iteration applies all possbile rules"), //
  RUDE("rude", "rude prover"), //
  ;

  private String name;
  private String description;

  private AvailableProvers(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public AvailableProvers getValue() {
    return this;
  }

  public _FrjProver getProverInstance(LauncherExecConfiguration configuration, Formula goal) {
    switch (configuration.getSelectedProver()) {
    case ONE_LEVEL_ITERATION:
      return new ipl.frj.tp.oneLevelIteration.FrjProver_oneLevelIteration(configuration, goal);
    case RUDE:
      return new ipl.frj.tp.rude.FrjProver_Rude(configuration, goal);
    default:
      throw new ImplementationError(ImplementationError.CASE_NOT_IMPLEMENTED);
    }
  }

}
