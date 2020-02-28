package cci.service.owncert;

import java.util.ArrayList;
import java.util.List;

public class ExpRange {
     private List<String> from;
     private List<String> to;
     
     public void add(String strfrom, String strto) {
    	 if (from == null) from = new ArrayList();
    	 if (to == null) to = new ArrayList();
    	 from.add(strfrom);
    	 to.add(strto);
     }
          
     public String getFrom(int index) {
    	 return (from != null && index < from.size()) ? from.get(index) : null;
     }
          
     public String getTo(int index) {
    	 return (to != null && index < to.size()) ? to.get(index) : null;
     }
     
     public int size() {
    	 return from != null ? from.size() : 0; 
     }
}
