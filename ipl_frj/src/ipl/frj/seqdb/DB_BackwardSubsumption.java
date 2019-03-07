package ipl.frj.seqdb;

import java.util.Collection;

import ipl.frj.launcher.LauncherExecConfiguration;
import ipl.frj.sequent.FrjIrregularSequent;
import ipl.frj.sequent.FrjRegularSequent;
import ipl.frj.sequent._FrjSequent;
import ipl.frj.tp.basic.CandidateFormulas;
import ipl.frj.tp.basic.ProvedSequentsTables;
import ipl.frj.tp.basic._ProvedSequentsDBWithBwSubsumption;
import ipl.frj.tp.basic._SequentsTable;
import ipl.frj.util.MSGManager;

/**
 * An implementation of the database storing the proved sequents performing
 * forward subsumption check when adding a proved sequent.
 * 
 * The database storing the proved sequents. The DB maintains a table for every
 * iteration performed by the prover and a global table storing all the proved
 * sequents. The {@link #add(_FrjSequent)} method add a sequent to the <em>current
 * iteration</em> table only if the added sequent is not subsumed by a sequent
 * already contained either in the <em>global table</em> or in the
 * <em>current-iteration</em> table.
 * 
 * 
 * The sequent is effectively added only if it has not been previously added to
 * the DB. The sequents added to the current iteration table are not stored in
 * the global table until the {@link #commitIteration()} method is invoked; when
 * this method is invoked a new fresh iteration table is created (the new
 * <em>current iteration</em> table). The class provides methods to access the
 * sequents stored in the main tables maintained by the DB, that is: the
 * <em>global table</em>, in the <em>current iteration</em> table, and the
 * <em>last-committed iteration</em> table; available tables are specified by
 * the {@link ProvedSequentsTables} enumerative constants.
 * 
 * @author Mauro Ferrari
 *
 */
public class DB_BackwardSubsumption extends AbstractDB
    implements _ProvedSequentsDBWithBwSubsumption {

  public DB_BackwardSubsumption(CandidateFormulas candidateFormulas,
      LauncherExecConfiguration execCofiguration) {
    super(candidateFormulas, new SequentsTableWithSubsumpionMaps(execCofiguration),
        new SequentsTableRemovingSubsumed(execCofiguration), execCofiguration);

    this.execConfiguration = execCofiguration;
    this.globalTable = (SequentsTableWithSubsumpionMaps) super.getGlobalTable();
  }

  // map an Irregular sequent to the sequent subsuming it
  private SubsumptionTable lastCommitSubsumtionTable;
  private SequentsTableWithSubsumpionMaps globalTable;
  private LauncherExecConfiguration execConfiguration;

  /**
   * Commits the sequents proved in the last iteration to the global table,
   * removing from the global table the irregular sequents subsumed by an
   * irregular sequent added in the last iteration. The method also builds a
   * associating with every removed sequent the sequent subsuming it; the client
   * can get this map invoking {@link #getLastCommitSubsumedMap()}.
   * 
   */
  @Override
  public void commitIteration() {
    // REMOVE IRREGULAR SUBSUMED FROM GLOBAL TABLE AND BUILD IRREGULAR 
    // SUBSUMPTION TABLE
    {
      if (VERBOSE)
        MSGManager.infoNoLn(MSG.BD_BACKWARD_SUBSUMPTION.VERBOSE.REMOVING_IRREGULAR_SUBSUMED_BEGIN);

      // get irregular sequents added in the last iteration
      Collection<FrjIrregularSequent> irregular =
          ((SequentsTableRemovingSubsumed) this.getCurrentIterationTable()).irregular();

      // build subsumtion table
      lastCommitSubsumtionTable = null;

      int subsumedRemoved = 0;
      if (irregular != null) {
        lastCommitSubsumtionTable = new SubsumptionTable();
        for (FrjIrregularSequent seq : irregular) {
          // get the irr sequents in the globale table subsumed by seq
          Collection<FrjIrregularSequent> subsumed = globalTable.subsumedIrregular(seq);
          if (subsumed != null) {
            lastCommitSubsumtionTable.add(seq, subsumed);
            subsumedRemoved += subsumed.size();
            // remove subsumed sequents
            for (FrjIrregularSequent subsumedSeq : subsumed) {
              globalTable.remove(subsumedSeq);
              globalTable.addSubsumption(subsumedSeq, seq);
            }
          }
        }
      }

      // if no subsumption was detected subsumption table is null
      if (subsumedRemoved == 0)
        lastCommitSubsumtionTable = null;

      if (VERBOSE)
        MSGManager.info(MSG.BD_BACKWARD_SUBSUMPTION.VERBOSE.REMOVING_IRREGULAR_SUBSUMED_END,
            subsumedRemoved);
    }

    // REMOVE REGULAR SUBSUMED FROM GLOBAL TABLE
    {

      if (VERBOSE)
        MSGManager.infoNoLn(MSG.BD_BACKWARD_SUBSUMPTION.VERBOSE.REMOVING_REGULAR_SUBSUMED_BEGIN);
      // get regular sequents added in the last iteration
      Collection<FrjRegularSequent> regular =
          ((SequentsTableRemovingSubsumed) this.getCurrentIterationTable()).regular();

      int subsumedRemoved = 0;
      if (regular != null) {
        for (FrjRegularSequent seq : regular) {
          // get the irr sequents in the globale table subsumed by seq
          Collection<FrjRegularSequent> subsumed = globalTable.subsumedRegular(seq);
          if (subsumed != null) {
            subsumedRemoved += subsumed.size();
            // remove subsumed sequents
            for (FrjRegularSequent subsumedSeq : subsumed) {
              globalTable.remove(subsumedSeq);
              globalTable.addSubsumption(subsumedSeq, seq);
            }
          }
        }
      }

      if (VERBOSE)
        MSGManager.info(MSG.BD_BACKWARD_SUBSUMPTION.VERBOSE.REMOVING_REGULAR_SUBSUMED_END,
            subsumedRemoved);

    }

    super.commitIteration();
  }

  /**
   * Add the specified sequent to those proved in the current iteration provided
   * that the specified sequent is not subsumed by a sequent already stored in
   * the <em>global table</em> or in the <em>current-iteration</em> table.
   * 
   * @param provedSequent the sequent to add.
   * @return true iff the sequent has been effectively added to the current
   * iteration table.
   */
  @Override
  public boolean add(_FrjSequent provedSequent) {
    SequentsTableRemovingSubsumed currentIterationTable =
        (SequentsTableRemovingSubsumed) super.getCurrentIterationTable();

    if (globalTable.contains(provedSequent) || currentIterationTable.contains(provedSequent))
      return false;

    if (globalTable.subsumes(provedSequent) || currentIterationTable.subsumes(provedSequent))
      return false;

    currentIterationTable.insert(provedSequent);
    this.setSequentIdx(provedSequent);
    return true;
  }

  @Override
  _SequentsTable geneateNewIterationTable() {
    return new SequentsTableRemovingSubsumed(execConfiguration);
  }

  /**
   * Returns the subsumption table built in the last commit invocation if some
   * subsumed sequent was removed during the last commit or <code>null</code>
   * otherwise. See {@link #commitIteration()} documentation for details.
   * 
   * @return the subsumption table built during the last commit invocation.
   */
  public SubsumptionTable getLastCommitSubsumptionTable() {
    return lastCommitSubsumtionTable;
  }

  @Override
  public void addSubsumption(_FrjSequent subsumed, _FrjSequent subsuming) {
    globalTable.addSubsumption(subsumed, subsuming);
  }

  @Override
  public _FrjSequent getMaximalSubsuming(_FrjSequent seq) {
    return globalTable.getMaximalSubsuming(seq);
  }

  @Override
  public FrjRegularSequent getMaximalRegularSubsuming(FrjRegularSequent seq) {
    return globalTable.getMaximalRegularSubsuming(seq);
  }

  @Override
  public FrjIrregularSequent getMaximalIrregularSubsuming(FrjIrregularSequent seq) {
    return globalTable.getMaximalIrregularSubsuming(seq);
  }

  @Override
  public _FrjSequent getFisrtSubsuming(_FrjSequent seq) {
    return globalTable.getFisrtSubsuming(seq);
  }

  @Override
  public FrjRegularSequent getFirstRegularSubsuming(FrjRegularSequent seq) {
    return globalTable.getFirstRegularSubsuming(seq);
  }

  @Override
  public FrjIrregularSequent getFirstIrregularSubsuming(FrjIrregularSequent seq) {
    return globalTable.getFirstIrregularSubsuming(seq);
  };

}
