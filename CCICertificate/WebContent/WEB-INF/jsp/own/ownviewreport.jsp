<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
$('.rth').click(function(){
    var table = $(this).parents('table').eq(0)
    var rows = table.find('tr:gt(0)').toArray().sort(comparer($(this).index()))

    this.asc = !this.asc
    if (!this.asc) {rows = rows.reverse()}
    for (var i = 0; i < rows.length; i++){table.append(rows[i])}
})


$(".rth").hover(
      function () {
             $(this).css('cursor','pointer');
             $(this).css('text-decoration', 'underline');
      },
      function () {
             $(this).css('cursor','default');
             $(this).css('text-decoration', '');
      }
    );
    
function comparer(index) {
	    return function(a, b) {
	        var valA = getCellValue(a, index), valB = getCellValue(b, index)
	        return $.isNumeric(valA) && $.isNumeric(valB) ? valA - valB : valA.localeCompare(valB)
	    }
	}

function getCellValue(row, index){ return $(row).children('td').eq(index).html() }

</script>


<table class="certificate" style="width: 100%">
	<tr>
		<c:forEach items="${headers}" var="item">
			<th class="rth" style="width: 70%; background-color: gray; color: white">
				${item}</th>
			<th class="rth" style="width: 30%; background-color: gray; color: white">Кол. сертификатов</th>
		</c:forEach>
	</tr>

	<c:forEach items="${ownreports}" var="ownreport">
		<tr>
			<td>${ownreport.normalfield}</td>
			<td>${ownreport.value}</td>
		</tr>
	</c:forEach>
</table>