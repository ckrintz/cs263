package cs263w16;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class TaskData {
  private String keyname;
  private String value;
  private Date date;
  public TaskData(String k, String v, Date d) {
      this.keyname = k;
      this.value = v;
      this.date = d;
  }
  public TaskData () {}

  public String getValue() {
    return this.value;
  }
  public void setValue(String val) {
    this.value = val;
  }
  public String getKeyname() {
    return this.keyname;
  }
  public void setKeyname(String k) {
    this.keyname = k;
  }
  public Date getDate() {
    return this.date;
  }
  public void setDate(Date dt) {
    this.date = dt;
  }
} 

