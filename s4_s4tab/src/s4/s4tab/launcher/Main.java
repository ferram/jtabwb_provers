package s4.s4tab.launcher;

import s4.s4tab.tp.S4TabProver;
import jtabwb.launcher.Launcher;
import jtabwbx.problems.JTabWbSimpleProblemReader;
import jtabwbx.problems.PlainProblemReader;

public class Main {

  private final Class<S4TabProver> S4TAB_PROVER_CLASS = S4TabProver.class;
  private final String S4TAB_PROVER_NAME = "nbu";

  private static String FORMULA_SYNTAX_DESCRIPTION = //
      "Syntax of formulas\n" + "  atoms: Java identifiers\n" + //
          "logical: false, true, & (and), | (or), ~ (not), -> (implies), <=> (iff), box, dia (diamond)\n" + //
          "note: formulas are translated in CNF";

  private Launcher launcher;

  private Main() {
    this.launcher = new Launcher();
  }

  /**
   * The method launching the prover from main.
   * 
   * @param args the command line arguments.
   */
  private void start(String[] args) {
    // basic configurations
    launcher.configLauncherName(this.getClass().getCanonicalName());
    launcher.configStandardInputReader(new PlainProblemReader());
    launcher.optConfigInputSyntax(FORMULA_SYNTAX_DESCRIPTION);

    // config problem description readers
    launcher
        .configProblemDescriptionReader("plain", PlainProblemReader.class);
    launcher.configProblemDescriptionReader("jtabwb", JTabWbSimpleProblemReader.class, true);

    InitialNodeSetBuilder i = new InitialNodeSetBuilder();
    launcher.configInitialNodeSetBuilder(i);

    // config provers
    launcher.configTheoremProver(S4TAB_PROVER_NAME, S4TAB_PROVER_CLASS, true);

    launcher.processCmdLineArguments(args);
    launcher.launch();

  }

  /**
   * Launch the application with the arguments specified on the command line.
   * 
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    Main main = new Main();
    main.start(args);
  }

}
