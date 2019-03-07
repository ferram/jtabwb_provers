package ipl.frj.latex;

import java.io.PrintStream;
import java.util.LinkedList;

import ipl.frj.kripke.KripkeModel;
import ipl.frj.kripke.World;
import jtabwbx.prop.formula.FormulaLatexFormatter;

/**
 * Generate the latex rendering of a Kripke model.
 * 
 * @author Mauro Ferrari
 *
 */
class KripkeModelLatexGenerator {

  KripkeModel model;
  FormulaLatexFormatter formulaFormatter;

  public KripkeModelLatexGenerator(KripkeModel model) {
    super();
    this.model = model;
    formulaFormatter = new FormulaLatexFormatter();
    formulaFormatter.setAbbreviateImpliesFalse(true);
    formulaFormatter.setCommandForFALSE("\\bot");
    formulaFormatter.setAbbreviateImpliesFalse(true);
  }

  private final String LATEX_PREAMBLE = //
      "\\documentclass[10pt]{article}\n" + //
          "\\usepackage{tikz}\n" + //
          "\\usepackage{color}\n" + //
          "\\usepackage{xifthen}\n" + //
          "\\pdfpagewidth 200in\n" + //
          "\\pdfpageheight 100in\n" + //
          "\\DeclareMathSizes{10}{12}{12}{8}\n" //
  ;

  private final String LATEX_BEGIN_DOCUMENT = // 
      "\\begin{document}\n" + //
          "\\thispagestyle{empty}\n"; //

  private final String LATEX_END_COCUMENT = "\\end{document}";

  public void generateLatex(PrintStream out) {
    out.print(LATEX_PREAMBLE);
    out.print(LATEX_BEGIN_DOCUMENT);
    //    out.print("\\begin{minipage}{100in}\n");
    //    out.print("{\\Large {\\bf Countermodel for} $" + seqform.toLatex(goal) + "$}\n\\\\[4ex]\n\n");
    //    out.print("\\end{minipage}\n");
    _toLatex(out, "", model);
    out.print(LATEX_END_COCUMENT);
  }

  private final String NODE = "\\node[event] (n%d) [%s%s] {$\\sigma{%1$d}$};\n";

  private final String BELOW_STRING = "below=1cm of n%d";
  private final String STRING_RIGHT = ", right=1cm of n%d";
  private final String PATH = "\\path[->] (n%d) edge node[sloped] {} (n%d);\n";
  private final String PRE_NODES = "%% NODES OF LEVEL %d\n";
  private final String POST_NODES = "\n\n";
  private final String PRE_PATHS = "%% PATHS\n";
  private final String POST_PATHS = "\n\n";

  private void _toLatex(PrintStream out, String indentation, KripkeModel kripke) {

    World referenceNodeOfPreviousLevel = null;
    for (int level = 0; level <= kripke.maxLevel(); level++) {
      LinkedList<World> list = kripke.worldsOfLevel(level);
      World[] nodes = list.toArray(new World[list.size()]);
      String belowString = (referenceNodeOfPreviousLevel == null ? ""
          : String.format(BELOW_STRING, referenceNodeOfPreviousLevel.getIdx()));

      out.print(String.format(PRE_NODES, level));
      // generate nodes
      World lastTreatedSuccessor = null;
      for (int i = 0; i < nodes.length; i++) {
        String rightString = (lastTreatedSuccessor == null ? ""
            : String.format(STRING_RIGHT, lastTreatedSuccessor.getIdx()));
        out.print(String.format(NODE, nodes[i].getIdx(), belowString, rightString));
        lastTreatedSuccessor = nodes[i];
      }
      referenceNodeOfPreviousLevel = nodes[0];

      out.print(POST_NODES);
    }

    out.print(PRE_PATHS);
    for (int level = kripke.maxLevel(); level >= 0; level--) {
      LinkedList<World> list = kripke.worldsOfLevel(level);
      World[] nodes = list.toArray(new World[list.size()]);
      // generate path
      for (int i = 0; i < nodes.length; i++) {
        if (nodes[i].successors() != null)
          for (World succ : nodes[i].successors())
            out.print(String.format(PATH, nodes[i].getIdx(), succ.getIdx()));
      }
    }
    out.print(POST_PATHS);

  }

  private static String[][] replacement = new String[][] { { "_", "\\\\_" }, //
      { "\\$", "\\$" } };

  private static String correctName(String name) {
    String result = name;
    for (int i = 0; i < replacement.length; i++)
      result = result.replaceAll(replacement[i][0], replacement[i][1]);
    return result;
  }

}
