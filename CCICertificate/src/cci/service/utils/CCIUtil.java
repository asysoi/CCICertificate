package cci.service.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CCIUtil {
	
	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers range into List of separated numbers to write into certificate
	 * blanks 
	 * ---------------------------------------------- */
	public static Collection<String> getSequenceNumbers(String addblanks) {
		List<String> numbers = new ArrayList<String>();
		int pos = addblanks.indexOf("-");
		
		if (pos > 0) {
			int firstnumber = Integer.parseInt((addblanks.substring(0, pos)));
			int lastnumber = Integer.parseInt(addblanks.substring(pos + 1));
			
			for (int i = firstnumber; i <= lastnumber; i++) {
				numbers.add(addnull(i + ""));
			}
		} else if (!addblanks.trim().isEmpty())
			numbers.add(addblanks);
		return numbers;
	}
	
	private static String addnull(String number) {
		if (number.length() < 7) {
			number = addnull("0"+number);
		}
		return number;
	}


}
