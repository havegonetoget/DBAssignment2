import java.io.IOException;

public class Record {

  public boolean empty;

  public String college_ID;
  public String state;
  public String city;
  public String name;

  public Record() {
    this.empty = true;
  }

  public void updateFields(String[] fields) throws IOException {
    if (fields.length == 4) {
      this.college_ID = fields[0].trim();
      this.state = fields[1].trim();
      this.city = fields[2].trim();
      this.name = fields[3].trim();
      this.empty = false;
    } else
      throw new IOException();
  }

  public void makeEmpty() {
    this.state = "";
    this.city = "";
    this.name = "";
    empty = true;
  }

  public boolean isEmpty() {
    return empty;
  }

  public String toString() {
    return "College ID: " + this.college_ID +
        ", State: " + this.state +
        ", City: " + this.city +
        ", Name: " + this.name;
  }

}
