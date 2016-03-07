package cpl.g3c.launcher;

import cpl.g3c.tp.Prover;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._Prover;
import jtabwb.launcher.Launcher.LaunchConfiguration;
import jtabwb.launcher._ProblemReader;
import jtabwb.launcher._SingleExecutionConfigurator;
import jtabwb.util.ImplementationError;
import jtabwbx.problems.ProblemDescription;
import jtabwbx.prop.formula.FormulaFactory;

class SingelExecutionConfigurator implements _SingleExecutionConfigurator {

  public SingelExecutionConfigurator(InitialNodeSetBuilder initialNodeSetBuilder) {
    this.initialNodeSetBuilder = initialNodeSetBuilder;
  }

  private FormulaFactory formulaFactory;
  private InitialNodeSetBuilder initialNodeSetBuilder;

  @Override
  public void configProblemReader(_ProblemReader reader,
      LaunchConfiguration currentLauncherConfiguration) {
  }

  @Override
  public void configInitialNodeSetBuilder(ProblemDescription problemescrption,
      LaunchConfiguration launcherConfiguration) {
    this.formulaFactory = new FormulaFactory("@FALSE", "@TRUE");
    initialNodeSetBuilder.setFormulaFactory(formulaFactory);
  }

  @Override
  public void configProver(_Prover prover, _AbstractGoal initialNodeSet,
      LaunchConfiguration currentLauncherConfiguration) {

    if (prover instanceof Prover)
      ((Prover) prover).configure(this.formulaFactory);
    else
      throw new ImplementationError();

  }
}
