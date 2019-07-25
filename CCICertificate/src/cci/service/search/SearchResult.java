package cci.service.search;

import java.util.List;

public class SearchResult {
    private int numFoundDocs;
    private List<String> ids;
    private List<String> dates;
    private List<String> scores;
    private int pageNumber;
    private int hitsPerPage;
    
	public int getNumFoundDocs() {
	     return numFoundDocs;
	}
	
	public void setNumFoundDocs(int numFoundDocs) {
		this.numFoundDocs = numFoundDocs;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public List<String> getDates() {
		return dates;
	}
	public void setDates(List<String> dates) {
		this.dates = dates;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getHitsPerPage() {
		return hitsPerPage;
	}
	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}

	public List<String> getScores() {
		return scores;
	}

	public void setScores(List<String> scores) {
		this.scores = scores;
	}
    
	
}
