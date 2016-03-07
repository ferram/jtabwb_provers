package s4.s4tab.launcher;

import s4.s4tab.nodeset.S4TabGoal;
import s4.s4tab.nodeset.Sign;
import s4.s4tab.nodeset.TabS4FormulaFactory;
import jtabwb.engine._AbstractGoal;
import jtabwb.launcher.InitialGoalBuilderException;
import jtabwb.launcher._InitialGoalBuilder;
import jtabwbx.modal.btformula.BTModalFormula;
import jtabwbx.modal.btformula.BTModalFormulaFactory;
import jtabwbx.modal.formula.ModalFormula;
import jtabwbx.modal.parser.ModalFormulaParseException;
import jtabwbx.modal.parser.ModalFormulaParser;
import jtabwbx.problems.ProblemDescription;

/**
 * Initial node set builder for S4Tab.
 * @author Mauro Ferrari
 *
 */
class InitialNodeSetBuilder implements _InitialGoalBuilder {

  @Override
  public _AbstractGoal buildInitialNodeSet(ProblemDescription inputProblem)
      throws InitialGoalBuilderException {

    BTModalFormulaFactory btfactory = new BTModalFormulaFactory();
    BTModalFormula wff;
    try {
      ModalFormulaParser parser = new ModalFormulaParser();
      wff = btfactory.buildFrom(parser.parse(inputProblem.getFormulasByRole("conjecture").getFirst()));
    } catch (ModalFormulaParseException e) {
      throw new InitialGoalBuilderException(e.getMessage());
    }
    BTModalFormula cnf = wff.convertToCNF();
    TabS4FormulaFactory factory = new TabS4FormulaFactory();
    ModalFormula translated = factory.buildFrom(cnf);
    S4TabGoal goal = new S4TabGoal((TabS4FormulaFactory) translated.getFactory());
    goal.addSigned(Sign.F, translated);
    return goal;
  }

}
