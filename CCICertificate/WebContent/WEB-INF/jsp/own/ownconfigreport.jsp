<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script>
	$(document).ready(function() {
		$("#fields").removeAttr("multiple");
	});

	function submitreport() {
 	 if ($("#fields").val() ) {
		 var opts = {
			  lines: 11, // The number of lines to draw
			  length: 15, // The length of each line
			  width: 8, // The line thickness
			  radius: 20, // The radius of the inner circle
			  corners: 1, // Corner roundness (0..1)
			  rotate: 0, // The rotation offset
			  direction: 1, // 1: clockwise, -1: counterclockwise
			  color: '#000', // #rgb or #rrggbb or array of colors
			  speed: 1, // Rounds per second
			  trail: 60, // Afterglow percentage
			  shadow: false, // Whether to render a shadow
			  hwaccel: false, // Whether to use hardware acceleration
			  className: 'spinner', // The CSS class to assign to the spinner
			  zIndex: 2e9, // The z-index (defaults to 2000000000)
			  top: '50%', // Top position relative to parent
			  left: '50%' // Left position relative to parent
		   };
        var spinner = new Spinner(opts).spin(document.getElementById('content'));
 
		url = $("#configreport").attr("action");
		
		$.post(url, $("#configreport").serialize(), function(result) {
			$("#viewreport").html(result);
			spinner.stop();
		});
 	 }
	}
	
	$(function() {
	    $( document ).tooltip();
	});
	
</script>

<div id="content">
	<form:form id="configreport" action="owncertmakereport.do" method="POST" commandName="ownreportconfig">

		Сгруппировать сертификаты по:
		<form:select path="fields" items="${ownreportconfig.headermap}" style="width: 450px;" placeholder="Выберите параметр группирования"/>
		<a href="javascript:submitreport()"><img src="resources/images/refresh_32.png" id="goreport" alt="Сводный отчет" title="Сформировать отчет"/></a>
	</form:form>
	
</div>

<fieldset>
<div id="viewreport" style="max-height: 340px; overflow: auto;"></div>
</fieldset>

