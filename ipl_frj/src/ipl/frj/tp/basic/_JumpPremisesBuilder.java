package ipl.frj.tp.basic;

import java.util.Collection;

import ipl.frj.rules.JoinPremises;

/**
 * An object building the all the possible sets of premises of join rules.
 * 
 * @author Mauro Ferrari
 *
 */
public interface _JumpPremisesBuilder {

  /**
   * Returns the collection of the join premises built after having added the
   * specified irregular sequents to this builder.
   * 
   * @param newIrregular the irregular sequents to add to this builder.
   * @return the collection of join premises or <code>null</code> if no set of
   * premises can be built.
   */
  public Collection<JoinPremises> buildJoinPremises();

  /**
   * Returns the sequents DB used by this jump premises builder.
   * 
   * @return
   */
  public _ProvedSequentsDB getSequntsDB();

}
