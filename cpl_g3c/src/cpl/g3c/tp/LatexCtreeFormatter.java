package cpl.g3c.tp;

import cpl.g3c.calculus.ClashDetectionRule;
import cpl.g3c.calculus.Rule_Left_AND;
import cpl.g3c.calculus.Rule_Left_IMPLIES;
import cpl.g3c.calculus.Rule_Left_NOT;
import cpl.g3c.calculus.Rule_Left_OR;
import cpl.g3c.calculus.Rule_Right_AND;
import cpl.g3c.calculus.Rule_Right_IMPLIES;
import cpl.g3c.calculus.Rule_Right_NOT;
import cpl.g3c.calculus.Rule_Right_OR;
import jtabwb.engine._AbstractGoal;
import jtabwb.engine._AbstractRule;
import jtabwb.tracesupport.CTree;
import jtabwb.tracesupport.LatexTranslator.ProofStyle;
import jtabwb.tracesupport._LatexCTreeFormatter;
import jtabwb.util.CaseNotImplementedImplementationError;
import jtabwbx.prop.formula._Sequent;

/**
 * Implementation of {@link _LatexCTreeFormatter}.
 * 
 * @author Mauro Ferrari
 */
class LatexCtreeFormatter implements _LatexCTreeFormatter {

  private SequentLatexFormatter sequentFormatter;

  public LatexCtreeFormatter() {
    super();
    this.sequentFormatter = new SequentLatexFormatter();
  }

  @Override
  public String getPreamble() {
    return SequentLatexFormatter.LATEX_MACROS;
  }

  @Override
  public String getIntro() {
    return "";
  }

  @Override
  public ProofStyle proofStyle() {
    return ProofStyle.SEQUENT;
  }

  public String format(_AbstractGoal nodeSet) {
    return sequentFormatter.toLatex((_Sequent) nodeSet);
  }

  /*
   * @see
   * jptp.util._LatexProofFormatter#formatRuleName(jptp.basic._AbstractRule)
   */
  public String formatRuleName(_AbstractRule rule) {
    if (rule instanceof Rule_Left_AND)
      return "\\land L";
    if (rule instanceof Rule_Left_OR)
      return "\\lor L";
    if (rule instanceof Rule_Left_IMPLIES)
      return "\\to L";
    if (rule instanceof Rule_Left_NOT)
      return "\\neg L";
    if (rule instanceof Rule_Right_AND)
      return "\\land R";
    if (rule instanceof Rule_Right_OR)
      return "\\lor R";
    if (rule instanceof Rule_Right_IMPLIES)
      return "\\to R";
    if (rule instanceof Rule_Right_NOT)
      return "\\neg R";
    if (rule instanceof ClashDetectionRule)
      return "\\textrm{Id}";
    throw new CaseNotImplementedImplementationError(rule.name());
  }

  @Override
  public boolean generateNodeSetIndex() {
    return false;
  }

  @Override
  public boolean generateRuleIndex() {
    return false;
  }

  @Override
  public String pre(CTree ctree) {
    return null;
  }

  @Override
  public String post(CTree ctree) {
    return null;
  }

  @Override
  public boolean generateFailureGoalAnnotations() {
    return false;
  }

  
  
}
