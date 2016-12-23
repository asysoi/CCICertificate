﻿package cci.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cci.config.cert.ExportCertConfig;
import cci.service.Filter;

@Component
@Scope("session")
public class ViewManager {
	
	public static final String ORDASC ="asc";
	public static final String ORDDESC ="desc";
	private String[] hnames; 
	private String[] ordnames;
	private String[] ffields; 
	private String[] fieldnames;
	private int[] widths;
	private String orderby;
	private String order;
	private List elements;
	private int pagecount;
	private int pagesize;
	private int page;
	private Filter filter;
	private Boolean onfilter;
	private String url;
	private String fullsearchvalue;
	private ExportCertConfig downloadconfig;
		
	public String getFullsearchvalue() {
		return fullsearchvalue;
	}

	public void setFullsearchvalue(String fullsearchvalue) {
		this.fullsearchvalue = fullsearchvalue;
	}

	public String[] getFfields() {
		return ffields;
	}

	public void setFfields(String[] ffields) {
		this.ffields = ffields;
	}

	public String[] getFieldnames() {
		return fieldnames;
	}

	public void setFieldnames(String[] fieldnames) {
		this.fieldnames = fieldnames;
	}

	public String[] getHnames() {
		return hnames;
	}

	public void setHnames(String[] hnames) {
		this.hnames = hnames;
	}


	public String[] getOrdnames() {
		return ordnames;
	}


	public void setOrdnames(String[] ordnames) {
		this.ordnames = ordnames;
	}


	public int[] getWidths() {
		return widths;
	}


	public void setWidths(int[] widths) {
		this.widths = widths;
	}


	public String getOrderby() {
		return orderby;
	}


	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}

	public List getElements() {
		return elements;
	}


	public void setElements(List elements) {
		this.elements = elements;
	}


	public int getPagecount() {
		return pagecount;
	}


	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}


	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public int getPagesize() {
		return pagesize;
	}


	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}


	
	public Boolean getOnfilter() {
		return onfilter;
	}


	public void setOnfilter(Boolean onfilter) {
		this.onfilter = onfilter;
	}
	
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<Integer> getSizesList() {
		List<Integer> sizes = new ArrayList<Integer>();
        sizes.add(new Integer(5));
        sizes.add(new Integer(10));
        sizes.add(new Integer(15));
        sizes.add(new Integer(20));
        sizes.add(new Integer(50));
		return sizes;
	}


	public List<Integer> getPagesList() {
		List<Integer> pages = new ArrayList<Integer>();
		
		int pagelast = (pagecount + (pagesize -1))/pagesize;
		int pagestart = page - 5; 
		if (pagestart < 1) pagestart = 1;
		
		int pageend = pagestart + 9;
		if (pageend > pagelast) pageend = pagelast;
		if (pagelast - 9 < pagestart && pagelast > 10) pagestart = pagelast - 9; 
		
		for (int i = pagestart; i <= pageend; i++) {
			pages.add(new Integer(i));
		}
        return pages;
	}


	public String getPrevPageLink() {
		String link = "#";
		
		if (page > 1) {
		   link  = url + "?page=" + (page -1)+"&pagesize="+ pagesize + "&orderby="+orderby+"&order="+order;
		} 
 
		return link;
	}
	
	public String getNextPageLink() {
		String link = "#";
		
	    if ((pagesize * page) < pagecount) {
		   link  = url + "?page=" + (page + 1)+"&pagesize="+ pagesize + "&orderby="+orderby+"&order="+order;
	    }
 
		return link;
	}


	private HeaderTableView makeHeaderTableView(int width, String name, String orderby, String order, boolean selected) {
		
        HeaderTableView header = new HeaderTableView();
        header.setWidth(width);
        header.setName(name);
        header.setDbfield(orderby);
        header.setLink(url + "?pagesize=" + pagesize + "&orderby=" + orderby + "&order=" + order);
        header.setSelected(selected);
        header.setSelection(selected ? (order.equals("asc") ?  "▲" : "▼") : "");
		return header;
	}
	
	
	public List<HeaderTableView> getHeaders() {
		List<HeaderTableView> headers = new ArrayList<HeaderTableView>(); 
        for (int i = 0; i < widths.length ; i++) {
        	if (ordnames[i].equals(orderby)) {
   	           headers.add(makeHeaderTableView(widths[i], hnames[i], ordnames[i], order.equals(ORDASC) ? ORDDESC : ORDASC, true));
        	} else {
        	   headers.add(makeHeaderTableView(widths[i], hnames[i], ordnames[i], ORDASC, false)); 	
        	}
        }
        return headers;
	}
	
	public String getLastPageLink() {
		return url + "?page=" + ((pagecount + (pagesize -1))/pagesize) + "&pagesize="+ pagesize + "&orderby="+orderby+"&order="+order;
	}

	public String getFirstPageLink() {
		return url + "?page=1&pagesize="+ pagesize + "&orderby="+orderby+"&order="+order;
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public ExportCertConfig getDownloadconfig() {
		return downloadconfig;
	}

	public void setDownloadconfig(ExportCertConfig downloadconfig) {
		this.downloadconfig = downloadconfig;
	}
	

}
