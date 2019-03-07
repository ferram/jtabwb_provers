package ipl.frj.seqdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.rules.JoinPremises;
import ipl.frj.rules.SigmaThetaUpsilon;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._JumpPremisesBuilder;
import ipl.frj.tp.basic._ProvedSequentsDB;
import ipl.frj.util.MSGManager;
import jtabwbx.prop.basic.FormulaType;
import jtabwbx.prop.formula.BitSetOfFormulas;
import jtabwbx.prop.formula.Formula;

public class JumpPremisesBuilder_BackwardSubsumption implements _JumpPremisesBuilder {

  /**
   * Build a compatibility table.
   * 
   * @param candidateFormulas the candidate formulas for the prover.
   */
  public JumpPremisesBuilder_BackwardSubsumption(CandidateFormulas candidateFormulas,
      DB_BackwardSubsumption db, LauncherExecConfiguration configuration) {
    this.VERBOSE = configuration.verboseMode();
    this.DEBUG = configuration.debugMode();
    JOIN_MAX_DEGREE = candidateFormulas.getJoinORMaxDegree();
    this.db = db;
    this.compatibilityTable = new HashMap<FrjIrregularSequent, HashSet<FrjIrregularSequent>>();
    this.allIrregularWrapper = new LinkedList<IrregularWrapper>();
    this.idxToIrregular = new HashMap<Integer, IrregularWrapper>();
    this.irregularToIdx = new HashMap<FrjIrregularSequent, Integer>();
    this.currentCompatibleSetIdxs = new HashSet<BitSet>();
  }

  private boolean VERBOSE = false;
  private boolean DEBUG = false;
  private final int JOIN_MAX_DEGREE; // max degree 
  private DB_BackwardSubsumption db;
  //private SequentsTable globalTable;
  private HashMap<FrjIrregularSequent, HashSet<FrjIrregularSequent>> compatibilityTable;
  private LinkedList<IrregularWrapper> allIrregularWrapper;
  private HashMap<Integer, IrregularWrapper> idxToIrregular;
  private HashMap<FrjIrregularSequent, Integer> irregularToIdx;
  private HashSet<BitSet> currentCompatibleSetIdxs;

  private static class IrregularWrapper {

    IrregularWrapper(FrjIrregularSequent irregularSequent) {
      super();
      this.sequent = irregularSequent;
      this.idx = irregularSequent.getSequentProgessiveNumber();
    }

    final FrjIrregularSequent sequent;
    final int idx;

    public String toString() {
      return "idx=" + idx + " -- " + sequent.format();
    }
  }

  private boolean addCompatiblePair(FrjIrregularSequent first, FrjIrregularSequent second) {
    HashSet<FrjIrregularSequent> compatibleWith = compatibilityTable.get(first);
    if (compatibleWith == null) {
      compatibleWith = new HashSet<FrjIrregularSequent>();
      compatibilityTable.put(first, compatibleWith);
    }
    compatibleWith.add(second);

    compatibleWith = compatibilityTable.get(second);
    if (compatibleWith == null) {
      compatibleWith = new HashSet<FrjIrregularSequent>();
      compatibilityTable.put(second, compatibleWith);
    }
    return compatibleWith.add(first);
  }

  private boolean areCompatible(FrjIrregularSequent seq, FrjIrregularSequent other) {
    HashSet<FrjIrregularSequent> compatibleWith = compatibilityTable.get(seq);
    if (compatibleWith == null)
      return false;
    else
      return compatibleWith.contains(other);
  }

  // a set of irregular-sequents is valid if for every pair (i,j) seq_i and seq_j are compatible
  private boolean isValidSet(BitSet set) {
    for (int i = set.nextSetBit(0); i != -1; i = set.nextSetBit(i + 1))
      for (int j = set.nextSetBit(0); j != -1; j = set.nextSetBit(j + 1))
        if (i != j && !areCompatible(idxToIrregular.get(i).sequent, idxToIrregular.get(j).sequent)) // if a pair of seq are no compatible return false
          return false;
    return true;
  }

  HashSet<SigmaThetaUpsilon> alreadyTreatedJumpPremise = new HashSet<SigmaThetaUpsilon>();

  HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>> lastIterationSubsubedTable = null;

  public void setLastIterationSubsumedTable(
      HashMap<FrjIrregularSequent, Collection<FrjIrregularSequent>> subsumedTable) {
    lastIterationSubsubedTable = subsumedTable;
  }

  @Override
  public _ProvedSequentsDB getSequntsDB() {
    return db;
  }

  @Override
  public Collection<JoinPremises> buildJoinPremises() {
    if (VERBOSE)
      MSGManager.infoNoLn(MSG.VERBOSE.BUILDING_JOIN_PREMISES_BEGIN);

    Collection<FrjIrregularSequent> irregular =
        db.irregular(ProvedSequentsTables.LAST_COMMITTED_ITERATION);

    Collection<JoinPremises> joinPremises = null;
    if (irregular != null)
      joinPremises = _buildJoinPremises(irregular);

    if (VERBOSE)
      MSGManager.info(MSG.VERBOSE.BUILDING_JOIN_PREISES_END,
          (joinPremises == null ? 0 : joinPremises.size()));

    return joinPremises;
  }

  /**
   * Returns the list of JoinPremises
   * 
   * @param newCandidates
   * @return
   */
  private LinkedList<JoinPremises> _buildJoinPremises(Collection<FrjIrregularSequent> newCandidates) {

    // get the subsumption table built during the last commit
    SubsumptionTable subsumptionTable = db.getLastCommitSubsumptionTable();

    // this array will contain the new irregular sequents with their index
    LinkedList<IrregularWrapper> newNotUsedInSubsumtions = new LinkedList<IrregularWrapper>();

    // for a new candidate used in subsumtion we will add the set only containing it
    LinkedList<IrregularWrapper> newUsedInSubsumtions = new LinkedList<IrregularWrapper>();

    for (FrjIrregularSequent newCandidate : newCandidates) {
      IrregularWrapper newInWrapper = new IrregularWrapper(newCandidate);
      if (subsumptionTable == null)
        newNotUsedInSubsumtions.add(newInWrapper);
      else {
        Collection<FrjIrregularSequent> subsumedSequents = subsumptionTable.getSubsumedBy(newCandidate);
        if (subsumedSequents == null)
          newNotUsedInSubsumtions.add(newInWrapper);
        else {
          newUsedInSubsumtions.add(newInWrapper);
          for (FrjIrregularSequent subsumed : subsumedSequents) {
            for (BitSet set : currentCompatibleSetIdxs)
              if (set.get(irregularToIdx.get(subsumed))) {
                String oldset = null;
                if (DEBUG)
                  oldset = set.toString();
                // replace subsumed with new sequent
                set.clear(irregularToIdx.get(subsumed));
                set.set(newInWrapper.idx);
                if (DEBUG)
                  MSGManager.debug(MSG.BW_COMPATIBILITY_TABLE.DEBUG.OLD_SET, oldset, set);
              }
          }
        }
      }
    }

    // this array will contain the new irregular sequents with their index
    IrregularWrapper[] notUsedInSubsumptionArray =
        newNotUsedInSubsumtions.toArray(new IrregularWrapper[newNotUsedInSubsumtions.size()]);

    IrregularWrapper[] usedInSubsumptionArray =
        newUsedInSubsumtions.toArray(new IrregularWrapper[newUsedInSubsumtions.size()]);

    newNotUsedInSubsumtions.addAll(newUsedInSubsumtions);
    IrregularWrapper[] all =
        newNotUsedInSubsumtions.toArray(new IrregularWrapper[newNotUsedInSubsumtions.size()]);

    // update compatibility table
    updateCompatibilityTable(all);

    for (int i = 0; i < usedInSubsumptionArray.length; i++) {
      allIrregularWrapper.add(usedInSubsumptionArray[i]);
      idxToIrregular.put(usedInSubsumptionArray[i].idx, usedInSubsumptionArray[i]);
      irregularToIdx.put(usedInSubsumptionArray[i].sequent, usedInSubsumptionArray[i].idx);
      if (DEBUG)
        debug_print_added_sequent("Used in subsumtpion", usedInSubsumptionArray[i]);
    }

    for (int i = 0; i < notUsedInSubsumptionArray.length; i++) {
      allIrregularWrapper.add(notUsedInSubsumptionArray[i]);
      idxToIrregular.put(notUsedInSubsumptionArray[i].idx, notUsedInSubsumptionArray[i]);
      irregularToIdx.put(notUsedInSubsumptionArray[i].sequent, notUsedInSubsumptionArray[i].idx);
      if (DEBUG)
        debug_print_added_sequent("Not used in subsumption", notUsedInSubsumptionArray[i]);
    }

    // build compatible sets of premises
    LinkedList<FrjIrregularSequent[]> compatible =
        _buildCompatible(all, notUsedInSubsumptionArray, usedInSubsumptionArray);

    LinkedList<FrjIrregularSequent[]> candidatePremises = new LinkedList<FrjIrregularSequent[]>();
    // select the compatible sets satisfying condition on implications
    for (FrjIrregularSequent[] premises : compatible) {
      if (checkConditionOnImplications(premises))
        candidatePremises.add(premises);
    }

    if (candidatePremises.size() == 0)
      return null;

    // sort the join premises by degree
    LinkedList<JoinPremises> list = buildJoinPremisesToReturn(candidatePremises);
    JoinPremises[] array = list.toArray(new JoinPremises[list.size()]);
    Arrays.sort(array, new Comparator<JoinPremises>() {

      public int compare(JoinPremises o1, JoinPremises o2) {
        return o1.degree() - o2.degree();
      }
    });

    LinkedList<JoinPremises> result = new LinkedList<JoinPremises>();
    for (JoinPremises jp : array)
      result.add(jp);
    return result;
  }

  private LinkedList<JoinPremises> buildJoinPremisesToReturn(
      LinkedList<FrjIrregularSequent[]> candidatePremises) {

    HashMap<SigmaThetaUpsilon, LinkedList<JoinPremises>> map =
        new HashMap<SigmaThetaUpsilon, LinkedList<JoinPremises>>();

    for (FrjIrregularSequent[] prem : candidatePremises) {
      JoinPremises jp = new JoinPremises(prem);
      SigmaThetaUpsilon stu = jp.getSigmaThetaUpsilon();
      if (!alreadyTreatedJumpPremise.contains(stu)) {
        alreadyTreatedJumpPremise.add(stu);
        LinkedList<JoinPremises> list = map.get(stu);
        if (list == null) {
          list = new LinkedList<JoinPremises>();
          map.put(stu, list);
        }
        list.add(jp);
      }
    }

    // for every set with the same sigmatauupsilon selects the one with lower degree
    LinkedList<JoinPremises> result = new LinkedList<JoinPremises>();
    for (SigmaThetaUpsilon stu : map.keySet()) {
      JoinPremises minDegree = null;
      for (JoinPremises jp : map.get(stu)) {
        if (minDegree == null || jp.degree() < minDegree.degree())
          minDegree = jp;
      }
      result.add(minDegree);
    }

    return result;
  }

  String strIdxs(FrjIrregularSequent[] prem) {
    String s = "";
    for (int i = 0; i < prem.length; i++)
      s += prem[i].getSequentProgessiveNumber() + " ";
    return s;
  }

  private boolean checkConditionOnImplications(FrjIrregularSequent[] premises) {
    // build the set of right sides
    HashSet<Formula> setOfRightSides = new HashSet<Formula>();
    for (FrjIrregularSequent seq : premises)
      setOfRightSides.add(seq.right());

    // check condition on implications
    for (FrjIrregularSequent seq : premises) {// for every seq in premises
      //check that every implication in resource has antecedent in setOfRightSizes
      BitSetOfFormulas res_implies = seq.stable(FormulaType.IMPLIES_WFF);
      if (res_implies != null)
        for (Formula imp : res_implies)
          if (!setOfRightSides.contains(imp.immediateSubformulas()[0]))
            return false;
    }
    return true;
  }

  /* check if sequents i and j meet the conditions on resources */
  private boolean checkCompatibility(IrregularWrapper i, IrregularWrapper j) {
    //DD.dd("-- compatibility\n-- " + i + "\n-- " + j);
    boolean result = true;
    // check compatibility between i and j
    if (i.sequent.stable() != null)
      if (j.sequent.left() == null || !i.sequent.stable().subseteq(j.sequent.left()))
        result = false;

    if (result) {
      // TODO: ottimizzare !!
      // check compatibility between j and i
      if (j.sequent.stable() == null)
        result = true;
      else {
        if (i.sequent.left() == null)
          result = false;
        else
          result = j.sequent.stable().subseteq(i.sequent.left());
      }
    }

    if (DEBUG)
      MSGManager.debug(MSG.BW_COMPATIBILITY_TABLE.DEBUG.COMPATIBILITY_CHECK, result, i, j);

    return result;
  }

  private LinkedList<FrjIrregularSequent[]> _buildCompatible(IrregularWrapper[] allNew,
      IrregularWrapper[] notUsedInSubsumption, IrregularWrapper[] usedInSubsumption) {

    // ---- BUILD ALL SETS OF INDEXS OF COMPATIBLE SEQUENTS FROM newCandidates
    HashSet<BitSet> newCompatibileSets = new HashSet<BitSet>();

    //    // add to currentCompatibleSetIdxs the sets {s} for s in usedInSubsumption
    //    for (IrregularWrapper s : usedInSubsumption) {
    //      BitSet set = new BitSet();
    //      set.set(s.idx);
    //      newCompatibileSets.add(set);
    //    }

    //    LinkedList<IrregularWrapper> all_new = new LinkedList<IrregularWrapper>();
    //    if (notUsedInSubsumption != null)
    //      for (IrregularWrapper seq : notUsedInSubsumption)
    //        all_new.add(seq);
    //
    //    if (usedInSubsumption != null)
    //      for (IrregularWrapper seq : usedInSubsumption)
    //        all_new.add(seq);

    //IrregularWrapper[] allll = all_new.toArray(new IrregularWrapper[all_new.size()]);

    for (int i = 0; i < allNew.length; i++) { // Build the set of indexes of the sequents compatibile with i
      HashSet<Integer> set_idxCompatible = new HashSet<Integer>();

      set_idxCompatible.add(allNew[i].idx); // note the set is not empty since it contains i
      for (int j = 0; j < allNew.length; j++)
        if (i != j && this.areCompatible(allNew[i].sequent, allNew[j].sequent))
          set_idxCompatible.add(allNew[j].idx);

      // generate all compatible subsets of set_idxCompatible
      HashSet<BitSet> powerset = this.powerSet(set_idxCompatible);

      for (BitSet set : powerset) { // check if all indexes in the set are compatible
        if (set.cardinality() > 0 && set.cardinality() <= JOIN_MAX_DEGREE) // add only non emtpty sets with degree <= JOIN_MAX_DEGREE 
          if (this.isValidSet(set)) // all indexes are compatible
            newCompatibileSets.add(set);
      }
    }

    // ---- UPDATE currentCompatibleSetIdxs WITH SETS EXTENDING THE OLD ONES WITH SETS FROM newCompatibileSets
    {
      // first build all the sets extending the old ones with sets from newCompatibileSets
      HashSet<BitSet> oldCompatibleExtended = new HashSet<BitSet>();
      for (BitSet oldset : currentCompatibleSetIdxs) {
        for (BitSet newset : newCompatibileSets) {
          BitSet union = (BitSet) oldset.clone();
          union.or(newset);
          if (union.cardinality() <= JOIN_MAX_DEGREE) // add only if it has degree <= JOIN_MAX_DEGREE
            oldCompatibleExtended.add(union);
        }
      }
      // select the compatible sets in newCompatibileSet
      for (BitSet set : oldCompatibleExtended) { // check if all indexes in the set are compatible
        if (set.cardinality() > 0 && !currentCompatibleSetIdxs.contains(set))
          if (this.isValidSet(set)) // all indexes are compatible
            currentCompatibleSetIdxs.add(set);
      }
    }

    // add to currentCompatibleSetIdxs the compatibility sets from newCompatibileSets
    for (BitSet set : newCompatibileSets)
      currentCompatibleSetIdxs.add(set);

    if (DEBUG) {// print generated sets of compatible idxs
      String strSets = "";
      for (BitSet set : currentCompatibleSetIdxs) {
        strSets += set.toString() + " - ";
      }
      MSGManager.debug(MSG.BW_COMPATIBILITY_TABLE.DEBUG.COMPATIBLE_SETS,
          currentCompatibleSetIdxs.size(), strSets);
    }

    // build the array of premises from the compatible sets and return int
    return buildFormCurrentCompaibilityIdxsSet();
  }

  // update the compatibility table with new candidates
  private void updateCompatibilityTable(IrregularWrapper[] newCandidates) {
    // update compatibility table for old sequents adding compatibility with new sequents
    for (IrregularWrapper first : allIrregularWrapper)
      for (IrregularWrapper second : newCandidates) {
        if (checkCompatibility(first, second))
          this.addCompatiblePair(first.sequent, second.sequent);
      }

    // update compatibility table with the new sequents and their compatibilities 
    for (int i = 0; i < newCandidates.length; i++)
      for (int j = 0; j < newCandidates.length; j++)
        if (i != j) {
          if (checkCompatibility(newCandidates[i], newCandidates[j]))
            this.addCompatiblePair(newCandidates[i].sequent, newCandidates[j].sequent);
        }
  }

  // build the array containing the sequents whose index is in idxs
  private LinkedList<FrjIrregularSequent[]> buildFormCurrentCompaibilityIdxsSet() {
    LinkedList<FrjIrregularSequent[]> result = new LinkedList<FrjIrregularSequent[]>();

    for (BitSet set : currentCompatibleSetIdxs) {
      FrjIrregularSequent[] array = new FrjIrregularSequent[set.cardinality()];
      int k = 0;
      for (int i = set.nextSetBit(0); i != -1; i = set.nextSetBit(i + 1))
        array[k++] = idxToIrregular.get(i).sequent;
      result.add(array);
    }
    return result.size() == 0 ? null : result;
  }

  // build the hashset containing the power set of original set
  private HashSet<BitSet> powerSet(HashSet<Integer> originalSet) {

    HashSet<BitSet> sets = new HashSet<BitSet>();
    if (originalSet.isEmpty()) {
      sets.add(new BitSet());
      return sets;
    }
    List<Integer> list = new ArrayList<Integer>(originalSet);
    Integer head = list.get(0);
    HashSet<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
    for (BitSet set : powerSet(rest)) {
      BitSet newSet = (BitSet) set.clone();
      newSet.set(head);
      sets.add(newSet);
      sets.add(set);
    }
    return sets;
  }

  // DEBUG PRINT METHODS  
  private void debug_print_added_sequent(String str, IrregularWrapper irr) {
    MSGManager.debug(MSG.BW_COMPATIBILITY_TABLE.DEBUG.COMPATIBILITY_TABLE_ADDED, str, irr.idx,
        irr.sequent.format());
  }

}
