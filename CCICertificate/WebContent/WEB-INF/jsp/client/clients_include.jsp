﻿<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:url value="resources/css/cci.css" var="CCICss" />
<link href="${CCICss}" rel="stylesheet" />

<script>
	function clear() {
		$('input').val('');
		$('select').val('');
	}

	function reset() {
		$('#ffilter')[0].reset();
	}

	function submit() {
		url = $("#ffilter").attr("action");
		$.post(url, $("#ffilter").serialize());
		$( document ).ajaxComplete(function(event,request, settings ) {
			  goToList('clients.do?page=1&pagesize=${cmanager.pagesize}&orderby=${cmanager.orderby}&order=${cmanager.order}');
			  $("#pview").dialog("close");
		});
	}
	
	function resetClient() {
		$('#fclient')[0].reset();
	}

	function saveClient() {
		url = $("#fclient").attr("action");
		$.post(url, $("#fclient").serialize());
		
		$( document ).ajaxComplete(function(event,request, settings ) {
			  goToList('clients.do?page=1&pagesize=${cmanager.pagesize}&orderby=${cmanager.orderby}&order=${cmanager.order}');
			  $("#clview").dialog("close");
		});
	}
	
	function updateClient() {
		url = $("#fclient").attr("action");
		$.post(url, $("#fclient").serialize());
		
		$( document ).ajaxComplete(function(event,request, settings ) {
			  goToList('clients.do?page=1&pagesize=${cmanager.pagesize}&orderby=${cmanager.orderby}&order=${cmanager.order}');
			  $("#clview").dialog("close");
		});
	}
	
	function close() {
		$("#pview").dialog("close");
	}
	
	$(document).ready(function() {
		$("#pview").dialog({
			autoOpen : false
		});
		$("#clview").dialog({
			autoOpen : false
		});
        document.getElementById("filter").checked=${cmanager.onfilter};

		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a  href="javascript: setFilter();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}

	});

	function goToList(link) {
		var url = link;
		if (document.getElementById("filter").checked) {
			url = url + "&filter="
			        + document.getElementById("filter").checked;
   	    }
  		document.location.href = url;
	}

	function swithFilter() {
		goToList('clients.do?page=1&pagesize=${cmanager.pagesize}&orderby=${cmanager.orderby}&order=${cmanager.order}');
		
		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a href="javascript: setFilter();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
	}

	function setFilter(link) {
        link="cfilter.do?&pagesize=${cmanager.pagesize}&orderby=${cmanager.orderby}&order=${cmanager.order}";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Фильтр поиска компаний');
		$("#pview").dialog("option", "width", 740);
		$("#pview").dialog("option", "height", 500);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false );
		$("#pview").dialog({ buttons: [ { text: "Применить",  click : function() { submit(); } },  
				               { text: "Очистить Все ", click: function() { clear(); } },
 				               { text: "Отменить изменения", click: function() { reset(); } },
				               { text: "Отмена", click: function() { $( this ).dialog( "close" ); } }
                  	                                               ] });
		$("#pview").dialog( "option", "position", { my: "center",  at: "center", of:window} );
		$("#pview").dialog("open");
	}

	function viewClient(id) {
        link = "clientview.do?id=" + id;
		$("#clview").load(link);        
		$("#clview").dialog("option", "title", 'Компания');
		$("#clview").dialog("option", "width", 650);
		$("#clview").dialog("option", "height", 480);
		$("#clview").dialog("option", "modal", true);
		$("#clview").dialog("option", "resizable", false);
		$("#clview").dialog({
			buttons : [ 	{ text : "Закрыть",	click : function() {$(this).dialog("close");}} ]
		});

		$("#clview").dialog("option", "position", {
			my : "center top", at : "center", of :  listwindow });

		$("#clview").dialog("open");
	}
	
	function editClient(id) {
        link = "editclient.do?id=" + id;
		$("#clview").load(link);        
		$("#clview").dialog("option", "title", 'Компания');
		$("#clview").dialog("option", "width", 820);
		$("#clview").dialog("option", "height", 570);
		$("#clview").dialog("option", "modal", true);
		$("#clview").dialog("option", "resizable", false);
		$("#clview").dialog({ buttons: [ { text: "Сохранить",  click : function() { updateClient(); } },  
		    				               { text: "Очистить Все ", click: function() { clear(); } },
		     				               { text: "Отмена", click: function() { $( this ).dialog( "close" ); } }
		                      	                                               ] });

		$("#clview").dialog("option", "position", {
			my : "center top", at : "center", of :  listwindow });

		$("#clview").dialog("open");
	}
	

	function addClient(id) {
        link = "addclient.do";
		$("#clview").load(link);        
		$("#clview").dialog("option", "title", 'Компания');
		$("#clview").dialog("option", "width", 820);
		$("#clview").dialog("option", "height", 570);
		$("#clview").dialog("option", "modal", true);
		$("#clview").dialog("option", "resizable", false);
		$("#clview").dialog({ buttons: [ { text: "Сохранить",  click : function() { saveClient(); } },  
		    				               { text: "Очистить Все ", click: function() { clear(); } },
		     				               { text: "Отмена", click: function() { $( this ).dialog( "close" ); } }
		                      	                                               ] });

		$("#clview").dialog("option", "position", {
			my : "center top", at : "center", of :  listwindow });

		$("#clview").dialog("open");
	}
	

	function downloadClients() {
		link = "сconfig.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Экспорт списка клиентов');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 520);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
		$("#pview").dialog({
			buttons : [ { text : "Загрузить",	click : function() {download();}}, 
 				{ text : "Очистить Все ", click : function() {clearconfig(); }}, 
 				{ text : "Выбрать Все ", click : function() {selectall(); }}, 
				{ text : "Закрыть",	click : function() {$(this).dialog("close");}
			} ]
		});

		$("#pview").dialog("option", "position", {
			my : "center",
			at : "center",
			of : window
		});
		$("#pview").dialog("open");
	}


	function clearconfig() {
	      $('form input[type="checkbox"]').prop('checked', false);
	}

	function selectall() {
	      $('form input[type="checkbox"]').prop('checked', true);
	}

	function download() {
		url = $("#config").attr("action");
		$.post(url, $("#config").serialize());
		
   		var hiddenIFrameID = 'hiddenDownloader';
        var iframe = document.getElementById(hiddenIFrameID);
        
    	if (iframe == null) {
        	iframe = document.createElement('iframe');
        	iframe.id = hiddenIFrameID;
	    	iframe.style.display = 'none';
	    	document.body.appendChild(iframe);
    	}
    	iframe.src = "сdownload.do";
		
	}

</script>


<div id="listwindow" class="col-md-10 col-md-offset-2 main">
	<h3>Список контрагентов</h3>
	<table style="width: 100%">
		<tr>

			<td style="width: 60%">
                <input id="filter" type="checkbox"	onclick="javascript:swithFilter();" />
                <span id="filterlink"></span>
                </td>

			<td style="width: 40%; text-align: right">
				<a href="javascript:addClient();"><img src="resources/images/addclient.png" alt="Добавить"/></a>
				<a href="javascript:downloadClients();"><img src="resources/images/exp_excel.png" alt="Загрузить"/></a>
				   &nbsp; Строк в списке: 
				   <c:forEach items="${sizes}" var="item"> 
	           	   &nbsp;	
	               <a  href="javascript: goToList('clients.do?page=1&pagesize=${item}&orderby=${cmanager.orderby}&order=${cmanager.order}');">${item}</a>
				</c:forEach>
			</td>


		</tr>
	</table>

	<table class="certificate" style="width: 100%">
		<tr>
			<c:forEach items="${cmanager.headers}" var="item">
				<td
					style="width:${item.width}%;background-color: gray; color: black"><a
					href="javascript: goToList('${item.link}');" style="color: white">${item.name}${item.selection}

</a></td>
			</c:forEach>
		</tr>

		<c:forEach items="${clients}" var="client">
			<tr>
				<td><a href="javascript:editClient('${client.id}')">${client.name}</a></td>
				<td>${client.address}</td>
                <td>${client.unp}</td>
                <td>${client.bname}</td>
                <td>${client.work_phone}</td>
			</tr>
		</c:forEach>
	</table>

	<table style="width: 100%">
		<tr>
			<td style="width: 80%; text-align: left"><a
				href="javascript: goToList('${first_page}');"><img
					src="resources/images/first_page_24.png" alt="Перв."> </a> <a
				href="javascript: goToList('${prev_page}');"><img
					src="resources/images/prev_page_24.png" alt="Пред."></a> <c:forEach
					items="${pages}" var="item">
	           	   &nbsp;	
	               <a
						href="javascript: goToList('clients.do?page=${item}&pagesize=${cmanager.pagesize}

&orderby=

${cmanager.orderby}

&order=${cmanager.order}');"
						<c:if test="${item==cmanager.page}">style="border-style: solid; border-width: 

1px;"</c:if>>
						${item} </a>
				</c:forEach> &nbsp; [Клиентов: &nbsp;${cmanager.pagecount}]</td>
			<td style="width: 20%; text-align: right"><a
				href="javascript: goToList('${next_page}');"><img
					src="resources/images/next_page_24.png" alt="След."></a> <a
				href="javascript: goToList('${last_page}');"><img
					src="resources/images/last_page_24.png" alt="Посл."></a></td>
		</tr>
	</table>

	
	<div id="pview" name="pview">
	</div>

	<div id="clview" name="clview" style="text-align:center;">
	</div>
   
</div>



