package ipl.frj.seqdb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
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

public class JumpPremisesBuilder implements _JumpPremisesBuilder {

  /**
   * Build a compatibility table.
   * 
   * @param candidateFormulas the candidate formulas for the prover.
   */
  public JumpPremisesBuilder(CandidateFormulas candidateFormulas, _ProvedSequentsDB db,
      LauncherExecConfiguration execCofiguration) {
    this.db = db;
    this.VERBOSE = execCofiguration.verboseMode();
    this.JOIN_MAX_DEGREE = candidateFormulas.getJoinORMaxDegree();
    this.compatibilityTable = new HashMap<FrjIrregularSequent, HashSet<FrjIrregularSequent>>();
    this.allIrregular = new LinkedList<FrjIrregularSequent>();
    this.irregularByIndex = new HashMap<Integer, IrregularWrapper>();
    this.currentCompatibleSetIdxs = new HashSet<BitSet>();
  }

  private boolean VERBOSE = false;
  private final int JOIN_MAX_DEGREE; // max degree 
  private HashMap<FrjIrregularSequent, HashSet<FrjIrregularSequent>> compatibilityTable;
  private LinkedList<FrjIrregularSequent> allIrregular;
  private HashMap<Integer, IrregularWrapper> irregularByIndex;
  private HashSet<BitSet> currentCompatibleSetIdxs;
  private _ProvedSequentsDB db;

  private static class IrregularWrapper {

    private static int instances = 0;

    IrregularWrapper(FrjIrregularSequent irregularSequent) {
      super();
      this.sequent = irregularSequent;
      this.idx = instances++;
    }

    final FrjIrregularSequent sequent;
    final int idx;

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
        if (i != j
            && !areCompatible(irregularByIndex.get(i).sequent, irregularByIndex.get(j).sequent)) // if a pair of seq are no compatible return false
          return false;
    return true;
  }

  HashSet<SigmaThetaUpsilon> alreadyTreatedJumpPremise = new HashSet<SigmaThetaUpsilon>();

  @Override
  public Collection<JoinPremises> buildJoinPremises() {
    Collection<FrjIrregularSequent> lastIterationIrregular =
        db.irregular(ProvedSequentsTables.LAST_COMMITTED_ITERATION);
    
    LinkedList<JoinPremises> joinPremises = null;
    // build JoinPremise
    if (VERBOSE)
      MSGManager.infoNoLn(MSG.VERBOSE.BUILDING_JOIN_PREMISES_BEGIN);

    if (lastIterationIrregular != null)
      joinPremises = _buildJUMPPremises(lastIterationIrregular);
    
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
  private LinkedList<JoinPremises> _buildJUMPPremises(Collection<FrjIrregularSequent> newCandidates) {

    // build compatible sets of premises
    LinkedList<FrjIrregularSequent[]> compatible = _buildCompatible(newCandidates);

    // select the compatible sets satisfying condition on implications
    LinkedList<FrjIrregularSequent[]> candidatePremises = new LinkedList<FrjIrregularSequent[]>();
    for (FrjIrregularSequent[] premises : compatible) {
      if (checkConditionOnImplications(premises))
        candidatePremises.add(premises);
    }

    if (candidatePremises.size() == 0)
      return null;

    // build the JoinPremises to return so that 
    LinkedList<JoinPremises> result = new LinkedList<JoinPremises>();
    for (FrjIrregularSequent[] prem : candidatePremises) {
      JoinPremises jp = new JoinPremises(prem);
      SigmaThetaUpsilon stu = jp.getSigmaThetaUpsilon();
      if (!alreadyTreatedJumpPremise.contains(stu)) {
        alreadyTreatedJumpPremise.add(stu);
        result.add(new JoinPremises(prem));
      }
    }

    return result;
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
  private boolean checkCompatibility(FrjIrregularSequent i, FrjIrregularSequent j) {
    // check compatibility between i and j
    if (i.stable() != null)
      if (j.left() == null || !i.stable().subseteq(j.left()))
        return false;

    // TODO: ottimizzare !!
    // check compatibility between j and i
    if (j.stable() == null)
      return true;
    else {
      if (i.left() == null)
        return false;
      else
        return j.stable().subseteq(i.left());
    }
  }

  private LinkedList<FrjIrregularSequent[]> _buildCompatible(
      Collection<FrjIrregularSequent> sequentToAdd) {

    // this array will contain the new irregular sequents with their index
    IrregularWrapper[] newCandidatesWithIdxs = new IrregularWrapper[sequentToAdd.size()];
    {
      // add the new sequents to global lists
      int k = 0;
      for (FrjIrregularSequent seq : sequentToAdd)
        newCandidatesWithIdxs[k++] = new IrregularWrapper(seq);

      // update compatibility table
      updateCompatibilityTable(newCandidatesWithIdxs);

      // add the new sequents to global lists
      for (int i = 0; i < newCandidatesWithIdxs.length; i++) {
        allIrregular.add(newCandidatesWithIdxs[i].sequent);
        irregularByIndex.put(newCandidatesWithIdxs[i].idx, newCandidatesWithIdxs[i]);
      }
    }

    // ---- BUILD ALL SETS OF INDEXS OF COMPATIBLE SEQUENTS FROM newCandidates
    LinkedList<BitSet> newCompatibileSets = new LinkedList<BitSet>();

    for (int i = 0; i < newCandidatesWithIdxs.length; i++) { // Build the set of indexes of the sequents compatibile with i
      HashSet<Integer> set_idxCompatible = new HashSet<Integer>();

      set_idxCompatible.add(newCandidatesWithIdxs[i].idx); // note the set is not empty since it contains i
      for (int j = 0; j < newCandidatesWithIdxs.length; j++)
        if (i != j && this.areCompatible(newCandidatesWithIdxs[i].sequent,
            newCandidatesWithIdxs[j].sequent))
          set_idxCompatible.add(newCandidatesWithIdxs[j].idx);

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
      for (BitSet oldset : currentCompatibleSetIdxs)
        for (BitSet newset : newCompatibileSets) {
          BitSet union = (BitSet) oldset.clone();
          union.or(newset);
          if (union.cardinality() <= JOIN_MAX_DEGREE) // add only if it has degree <= JOIN_MAX_DEGREE
            oldCompatibleExtended.add(union);
        }

      // select the compatible sets in newCompatibileSet
      for (BitSet set : oldCompatibleExtended) { // check if all indexes in the set are compatible
        if (set.cardinality() > 0)
          if (this.isValidSet(set)) // all indexes are compatible
            currentCompatibleSetIdxs.add(set);
      }
    }

    // add to currentCompatibleSetIdxs the compatibility sets from newCompatibileSets
    for (BitSet set : newCompatibileSets)
      currentCompatibleSetIdxs.add(set);

    // build the array of premises from the compatible sets
    LinkedList<FrjIrregularSequent[]> result = new LinkedList<FrjIrregularSequent[]>();
    for (BitSet set : currentCompatibleSetIdxs)
      result.add(buildFormIndexs(set));

    return result.size() == 0 ? null : result;
  }

  // update the compatibility table with new candidates
  private void updateCompatibilityTable(IrregularWrapper[] newCandidates) {
    // update compatibility table for old sequents adding compatibility with new sequents
    for (FrjIrregularSequent first : allIrregular)
      for (IrregularWrapper second : newCandidates)
        if (checkCompatibility(first, second.sequent))
          this.addCompatiblePair(first, second.sequent);

    // update compatibility table with the new sequents and their compatibilities 
    for (int i = 0; i < newCandidates.length; i++)
      for (int j = 0; j < newCandidates.length; j++)
        if (i != j) {
          if (checkCompatibility(newCandidates[i].sequent, newCandidates[j].sequent))
            this.addCompatiblePair(newCandidates[i].sequent, newCandidates[j].sequent);
        }
  }

  // build the array containing the sequents whose index is in idxs
  private FrjIrregularSequent[] buildFormIndexs(BitSet idxs) {
    FrjIrregularSequent[] result = new FrjIrregularSequent[idxs.cardinality()];
    int k = 0;
    for (int i = idxs.nextSetBit(0); i != -1; i = idxs.nextSetBit(i + 1))
      result[k++] = irregularByIndex.get(i).sequent;
    return result;
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

  @Override
  public _ProvedSequentsDB getSequntsDB() {
    return db;
  }

}
