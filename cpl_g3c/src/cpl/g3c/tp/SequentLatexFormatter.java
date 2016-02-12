package cpl.g3c.tp;

import jtabwbx.prop.formula.FormulaLatexFormatter;
import jtabwbx.prop.formula._Sequent;

/**
 * Latex formatter for sequents.
 * 
 * @author Mauro Ferrari
 */
class SequentLatexFormatter {

  public static final String LATEX_MACROS = //
  "%% \\seq{Gamma}{Delta} writes the sequent Gamma ==> Delta\n"
      + "\\newcommand{\\seq}[2]{#1\\Rightarrow #2}\n";

  final static String SEQ_FORMAT = "\\seq{%s}{%s}";

  private FormulaLatexFormatter formulaFormatter;

  public SequentLatexFormatter() {
    super();
    this.formulaFormatter = new FormulaLatexFormatter();
  }

  public SequentLatexFormatter(FormulaLatexFormatter formulaFormatter) {
    super();
    this.formulaFormatter = formulaFormatter;
  }

  public String toLatex(_Sequent seq) {
    String s = String.format(SEQ_FORMAT, formulaFormatter.toLatex(seq.getLeftFormulas(), ", "),
        formulaFormatter.toLatex(seq.getRightFormulas(), ","));
    return s;
  }

}
