package ipl.frj.kripke;

class MSG {

  static class KRIPKE_MODEL_BUILDER {

    static class DEBUG {

      static final String BEGIN = "Extracting counter-model";
      static final String SEQUENT_DETAILS = "Treating: idx[%d] -- [%s] generated by [%s]";
      static final String WORLD_ADDED =
          "Node added: idx[%d] -- [%s] -- successors=%s -- level=[%d]";
      static final String END = "Model =\n%s";
    }

    static class ERRORS {
    }

  }

  static class KRIPKE_MODEL {
    static class DEBUG {
      static final String WORLD_COMPLETE =
          "Level [%d] - node idx[%d] -- [%s] -- successors=%s -- predecessors=%s\n";
    }
  }
  
}