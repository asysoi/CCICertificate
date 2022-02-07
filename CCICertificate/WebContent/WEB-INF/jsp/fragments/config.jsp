<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<style>
  input[name="fields"] {
      margin-top: 4px;
      margin-bottom: 3px;
      margin-right:8px;
      margin-left: 0px;
  }
</style>

<script>
    $(document).ready(function(){
	      $( "#content li" ).each(function(index, element) {
	        if (index < 17) {
	           $("#col1").append($(element).html() + "</br>");
	        } else {
	           $("#col2").append($(element).html() + "</br>");
	        }
	   });
	   $("#content").empty();

	    $("#col1").show();
	    $("#col2").show();
	});
</script>

<h4>Список отображаемых полей сертификата</h4>
<form:form id="config" method="POST" commandName="downloadconfig">
<div id="content" style="display: none">
	<form:checkboxes element="li" path="fields"	items="${downloadconfig.headermap}" />
</div>
	
<table style="width:100%">
<tr>
<td><div id="col1" style="display: none;  vertical-align:top"> </div></td> 
<td><div id="col2" style="display: none;  vertical-align:top"> </div></td> 
</tr>
</table>	
</form:form>


