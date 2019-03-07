package ipl.frj.sequent;

public enum SequentType {
  IRREGULAR("--->"), REGULAR("===>");

  private SequentType(String description) {
    this.description = description;
  }

  private String description;

  public String getStringRepresentation() {
    return description;
  }

}
