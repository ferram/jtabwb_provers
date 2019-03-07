package ipl.frj.launcher;

import ferram.rtoptions._NamedArgument;

public enum AvailableSequentDB implements _NamedArgument<AvailableSequentDB> {
  PLAIN("plain", "Plain Sequents DB"), //
  SUBSUMPTION_FORWARD("fw", "Sequent DB with forward subsumption"), //
  SUBSUMPTION_BACWARD("bw", "Sequent DB with backward subsumption"), //
  ;

  private String name;
  private String description;

  private AvailableSequentDB(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public AvailableSequentDB getValue() {
    return this;
  }


}
