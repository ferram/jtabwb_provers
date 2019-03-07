package ipl.frj.latex;

import java.io.PrintStream;
import java.util.LinkedList;

import ferram.util.ArrayFormatting;
import ipl.frj.kripke.KripkeModel;
import ipl.frj.kripke.World;
import ipl.frj.launcher.FrjSingleProblemExecutionData;
import jtabwbx.prop.formula.Formula;

/**
 * Generates the Graphviz (.gv) rendering of a Kripke model.
 * 
 * @author Mauro Ferrari
 *
 */
class KripkeModelGraphvizGenerator {

  private KripkeModel model;
  private FrjSingleProblemExecutionData execData;
  private String modelFileName;
  private PrintStream out;

  public KripkeModelGraphvizGenerator(KripkeModel model, FrjSingleProblemExecutionData execData,
      String modelFileName, PrintStream out) {
    super();
    this.model = model;
    this.execData = execData;
    this.modelFileName = modelFileName;
    this.out = out;
  }

  private final String FILE_PREAMBLE = // 
      "/***  SOURCE GRAPHVIZ FILE  ***\n\n" //
          + "Source file for %s\n\n" //
          + "Compiled with:\n\n" //
          + "    dot %1$s.gv -Tpng -o %1$s.png\n\n"
          + "Edit this file or change the compilation option to modify the layout\n\n" //
          + "***/";

  private final String DIAGRAM_BEGIN = //
      "graph ObjectDiagram {\n" // 
          + "  graph [\n" //
          + "    rankdir= BT\n" //
          + "    overlap = false\n" //
          + "  ];\n" //
          + "node [style = filled];\n"
          //          + "  node [\n" //
          //          + "    fontsize = \"16\"\n" // 
          //          + "    shape = \"record\"\n" //
          //          + "    style = filled\n" //
          //          + "    fillcolor = green\n" //
          //          + "  ];\n" //
          + "  label=\"Counter model for %s.\" // optional\n" //
          + "  fontsize=12;\n\n";

  private final String DIAGRAM_END = //
      "} // end model";

  public void generateGraphviz() {
    out.print(String.format(FILE_PREAMBLE, modelFileName));
    out.print(String.format(DIAGRAM_BEGIN, execData.problemDescription().getProblemName()));
    toDiagram("", model);
    out.print(DIAGRAM_END);
  }

  private final String PRE_NODES = "// NODES of level %d\n";
  private final String POST_NODES = "\n";
  private final String NODE = "w%1$d  [label = \"%1$d: %2$s\"]\n";

  private final String PRE_PATHS = "// PATHS from nodels with level %d\n";
  private final String POST_PATHS = "\n";
  private final String PATH = "w%d -- w%d\n";

  private void toDiagram(String indentation, KripkeModel kripke) {

    for (int level = 0; level <= kripke.maxLevel(); level++) {
      out.print(String.format(PRE_NODES, level));
      // generate nodes
      for (World w : kripke.worldsOfLevel(level))
        out.print(String.format(NODE, w.getIdx(), toString(w.focing())));
      out.print(POST_NODES);
    }

    for (int level = 1; level <= kripke.maxLevel(); level++) {
      out.print(String.format(PRE_PATHS, level));
      LinkedList<World> list = kripke.worldsOfLevel(level);
      World[] nodes = list.toArray(new World[list.size()]);
      // generate path
      for (int i = 0; i < nodes.length; i++) {
        if (nodes[i].successors() != null)
          for (World succ : nodes[i].successors())
            out.print(String.format(PATH, nodes[i].getIdx(), succ.getIdx()));
      }
      out.print(POST_PATHS);
    }

  }

  private String toString(LinkedList<Formula> forcing) {
    if (forcing == null)
      return "";
    Formula[] array = forcing.toArray(new Formula[forcing.size()]);
    return ArrayFormatting.toString(array, ", ");
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
