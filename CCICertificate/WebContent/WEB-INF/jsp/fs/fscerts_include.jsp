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
			  goToList('fscerts.do?page=1&pagesize=${fsmanager.pagesize}&orderby=${fsmanager.orderby}&order=${fsmanager.order}');
			  $("#pview").dialog("close");
		});
	}

	function close() {
		$("#pview").dialog("close");
		$("#pview").html('');
	}
	
	$(document).ready(function() {
		$("#pview").dialog({
			autoOpen : false
		});
		$("#pdfview").dialog({
			autoOpen : false
		});
        document.getElementById("filter").checked=${fsmanager.onfilter};

		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a  href="javascript: loadWindow();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
	});

	function goToList(link) {
		var url = link;
		spin();
		if (document.getElementById("filter").checked) {
			url = url + "&filter="
			        + document.getElementById("filter").checked;
   	    }
  		document.location.href = url;

	}

    function spin() {
	   var opts = {
		  lines: 13, // The number of lines to draw
		  length: 20, // The length of each line
		  width: 10, // The line thickness
		  radius: 35, // The radius of the inner circle
		  corners: 1, // Corner roundness (0..1)
		  rotate: 0, // The rotation offset
		  direction: 1, // 1: clockwise, -1: counterclockwise
		  color: '#FF0000', // #rgb or #rrggbb or array of colors
		  speed: 1, // Rounds per second
		  trail: 60, // Afterglow percentage
		  shadow: false, // Whether to render a shadow
		  hwaccel: false, // Whether to use hardware acceleration
		  className: 'spinner', // The CSS class to assign to the spinner
		  zIndex: 2e9, // The z-index (defaults to 2000000000)
		  top: '50%', // Top position relative to parent
		  left: '50%' // Left position relative to parent
	        };
	        var target = document.getElementById('listwindow');
	        var spinner = new Spinner(opts).spin(target);

	}

	function swithFilter() {
		goToList('fscerts.do?page=1&pagesize=${fsmanager.pagesize}&orderby=${fsmanager.orderby}&order=${fsmanager.order}');
		
		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a href="javascript: loadWindow();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
	}

	function loadWindow(link) {
        link="fscertfilter.do?&pagesize=${fsmanager.pagesize}&orderby=${fsmanager.orderby}&order=${fsmanager.order}";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Фильтр поиска');
		$("#pview").dialog("option", "width", 800);
		$("#pview").dialog("option", "height", 535);
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

	function viewCertificate(certid) {
		memo = "Воспроизведение бумажной версиисертификата. <p>" + 
		       "Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
		       "но полностью воспроизводит содержание документа.</p>"
        $('#pdf').contents().find("body").html("<div style='color:black; text-align:center; font-size:16pt;'>" + memo + "</div> ");
                                   $('#pdf').contents().find('body').attr('style', 'background-color: white'); 
		link = "fscert.do?certid=" + certid;
		$("#pdfview").dialog("option", "title", 'Сертификат');
		$("#pdfview").dialog("option", "width", 963);
		$("#pdfview").dialog("option", "height", 570);
		$("#pdfview").dialog("option", "modal", true);
		$("#pdfview").dialog("option", "resizable", false);
		$("#pdfview").dialog({
			buttons : [ 	{ text : "Закрыть",	click : function() {$(this).dialog("close"); $('#pdf').contents().find("body").html('');}} ]
		});

		$("#pdfview").dialog("option", "position", {
			my : "center top",
			at : "center",
			of :  listwindow
		});
                                   
        $('#pdf').attr('height', $("#pdfview").dialog("option", "height") - 150);
        $('#pdf').attr('width', $("#pdfview").dialog("option", "width") - 40);
        $('#pdf').attr('scrolling', 'yes');
		$('#pdf').attr('src', link);

		$("#pdfview").dialog("open");
	}
	
	
    // ---------------------------------------------------------------------------------
    // Download list to Excel файл 
    // ---------------------------------------------------------------------------------
	function downloadCertificates() {
		link = "fscertconfig.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Экспорт списка сертификатов');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 400);
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
    	iframe.src = "fscertdownload.do";
	}
	
	
	// ---------------------------------------------------------------------------------
    // Open Report Window  
    // ---------------------------------------------------------------------------------
	function reportWindow() {
		link = "certconfigreport.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Отчет');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 520);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
		$("#pview").dialog({
			buttons : [ { text : "Закрыть",	click : function() {close();} } ]
		});

		$("#pview").dialog("option", "position", {
			my : "center",
			at : "center",
			of : window
		});
		$("#pview").dialog("open");
	}
</script>


<div id="listwindow" class="main">
	<h3>Список сертификатов свободной продажи</h3>
	
	<table style="width: 100%">
		<tr>
			<td style="width: 60%">
                <input id="filter" type="checkbox"	onclick="javascript:swithFilter();" >
                <span id="filterlink"></span>
            </td>

			<td style="width: 40%; text-align: right">
			   	   <a href="javascript:addFSCertificate();"><img src="resources/images/addclient.png" alt="Добавить"/></a>
				   <a href="javascript:downloadCertificates();"><img src="resources/images/exp_excel.png"alt="Загрузить"/></a>
				   &nbsp;			        
			       Строк в списке: <c:forEach items="${sizes}" var="item">
	           	   &nbsp;	
	               <a href="javascript: goToList('fscerts.do?page=1&pagesize=${item}&orderby=${fsmanager.orderby}
							&order=${fsmanager.order}');">${item}</a>
				</c:forEach>
			</td>
		</tr>
	</table>

	<table class="certificate" style="width: 100%">
		<tr>
			<c:forEach items="${fsmanager.headers}" var="item">
				<td
					style="width:${item.width}%;background-color: gray; color: black"><a
					href="javascript: goToList('${item.link}');" style="color: white; font-size: 120%;">${item.name}${item.selection}

			</a></td>
			</c:forEach>
		</tr>

		<c:forEach items="${certs}" var="cert">
			<tr>
				<td>
				<div class="ccidropdown"><span>${cert.certnumber}</span>
				<div class="ccidropdown-content"> 
				<ul class="cci">
					<li class="cci"><a class="cci" href="javascript:editFSCertificate('${cert.id}')"><i class="glyphicon glyphicon-edit"></i></a></li>
					<li class="cci"><a class="cci" href="javascript:openFSCertificate('${cert.id}')"><i class="glyphicon glyphicon-eye-open"></i></a></li>
					<li class="cci"><a class="cci" href="javascript:printOriginalFSCertificate('${cert.id}')"><i class="glyphicon glyphicon-print"></i></a></li>
					<li class="cci"><a class="cci" href="javascript:printCopyFSCertificate('${cert.id}')"><i class="glyphicon glyphicon-print"></i></a></li>
				</ul> </div> </div>
				</td>
				<td>${cert.exportername}. ${cert.exporteraddress} </td>
				<td>${cert.producername}. ${cert.produceraddress}</td>
				<td>${cert.datecert}</td>
                <td>${cert.listscount}
                </td> 
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
	               <a href="javascript: goToList('fscerts.do?page=${item}&pagesize=${fsmanager.pagesize}&orderby=${fsmanager.orderby}&order=${fsmanager.order}');"
						<c:if test="${item==fsmanager.page}">style="border-style: solid; border-width:1px;"</c:if>>
						${item} </a>
				</c:forEach> &nbsp; [Сертификатов:&nbsp;${fsmanager.pagecount}]</td>
			<td style="width: 20%; text-align: right"><a
				href="javascript: goToList('${next_page}');"><img
					src="resources/images/next_page_24.png" alt="След."></a> <a
				href="javascript: goToList('${last_page}');"><img
					src="resources/images/last_page_24.png" alt="Посл."></a></td>
		</tr>
	</table>

	<div id="pview" name="pview">
	</div>

	<div id="pdfview" name="pdfview">
     <!--  iframe class="pdf" id="pdf"></iframe -->
	</div>

    <p >Время загрузки: ${timeduration}</p> 
</div>



