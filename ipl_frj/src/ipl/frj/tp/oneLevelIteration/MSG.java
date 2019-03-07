package ipl.frj.tp.oneLevelIteration;

class MSG {

  static class LOG {

    static class DEBUG {
    }

    static class INFO {

      static final String PROVER_INITIAL_STATE = "Prover intial state:\n%s";

    }
  }

  static class VERBOSE {

    static class FRJPROVER {

      static String GOAL = "-- Goal: %s";

      static String APPLYING_AXIOMS = "  Applying axiom rules";
      static String APPLYING_AND = "  Applying AND rules";
      static String APPLYING_OR = "  Applying OR rules";
      static String APPLYING_IMPLIES = "  Applying IMPLIES rules";
      static String APPLYING_JOIN = "  Applying JOIN rules";

      static String ITERATION_BEGIN = "--Iteration [%d]";
      static String ITERATION_END = "  Proved sequents: last iteration [%d], total[%d]";
      static String RULE_APPLICATION_RESULT= "    Rule [%s] conclusion idx[%d] -- [%s]";
      static String RULE_APPLICATION_DETAILS_WITH_PREMISES = "    Rule [%s]: [%s] ===>>>\n    idx[%d] -- [%s]";       
    }

  }


}
