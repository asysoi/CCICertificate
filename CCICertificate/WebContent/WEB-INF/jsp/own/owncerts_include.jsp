<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
		$( document ).ajaxComplete(function(event, request, settings) {
			  goToList('owncerts.do?page=1&pagesize=${ownmanager.pagesize}&orderby=${ownmanager.orderby}&order=${ownmanager.order}');
			  $("#pview").dialog("close");
		});
	}

	function close() {
		$("#pview").dialog("close");
		$("#pview").html('');
	}
	
	$(document).ready(function() {
		
		$(".datepicker").datepicker({
			changeMonth : true,
			changeYear : true
		});
		
		$("#pview").dialog({
			autoOpen : false
		});
		$("#pdfview").dialog({
			autoOpen : false
		});
        document.getElementById("filter").checked=${ownmanager.onfilter};

		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a  href="javascript: loadWindow();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
		$(".datepicker").datepicker("option", "dateFormat", 'dd.mm.yy');

	});
	
	function goToList(link) {
		var url = link;
		spin('listwindow');
		if (document.getElementById("filter").checked) {
			url = url + "&filter="
			        + document.getElementById("filter").checked;
   	    }
  		document.location.href = url;
	}

    function spin(element) {
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
	   //var target = document.getElementById('listwindow');
	   var target = document.getElementById(element);
	   var spinner = new Spinner(opts).spin(target);
	   $(target).data('spinner', spinner); 
	}

	function swithFilter() {
		goToList('owncerts.do?page=1&pagesize=${ownmanager.pagesize}&orderby=${ownmanager.orderby}&order=${ownmanager.order}');
		
		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a href="javascript: loadWindow();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
	}

	function loadWindow(link) {
        link="owncertfilter.do?&pagesize=${ownmanager.pagesize}&orderby=${ownmanager.orderby}&order=${ownmanager.order}";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Фильтр поиска');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 545);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false );
		$("#pview").dialog({ buttons: [ { text: "Применить",  click : function() { submit(); } },  
				               { text: "Очистить Все ", click: function() { clear(); } },
 				               { text: "Отменить изменения", click: function() { reset(); } },
				               { text: "Отмена", click: function() { $( this ).dialog( "close" ); } }
                  	                                               ] });
              
		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
		$("#pview").dialog("open");
	}

	function viewCertificate(certid) {
		memo = "Воспроизведение бумажной версиисертификата. <p>" + 
		       "Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
		       "но полностью воспроизводит содержание документа.</p>"
        $('#pdf').contents().find("body").html("<div style='color:black; text-align:center; font-size:16pt;'>" + memo + "</div> ");
                                   $('#pdf').contents().find('body').attr('style', 'background-color: white'); 
		link = "owncert.do?certid=" + certid;
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

	function openCertificate(certid) {
		memo = "Воспроизведение бумажной версии сертификата. <p>" + 
		       "Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
		       "но полностью воспроизводит содержание документа.</p>"
        //$('#pdf').contents().find("body").html("<div style='color:black; text-align:center; font-size:16pt;'>" + memo + "</div> ");
        //                           $('#pdf').contents().find('body').attr('style', 'background-color: white'); 
		url = "owncert.do?certid=" + certid;
		var win=window.open(url,'_blank');
		win.focus();
	}
	
    // ---------------------------------------------------------------------------------
    // Download list to Excel файл 
    // ---------------------------------------------------------------------------------
	function downloadCertificates() {
		link = "owncertconfig.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Экспорт списка сертификатов');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 420);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
		$("#pview").dialog({
			buttons : [ { text : "Загрузить",	click : function() {download();}}, 
 				{ text : "Очистить Все ", click : function() {clearconfig(); }}, 
 				{ text : "Выбрать Все ", click : function() {selectall(); }}, 
				{ text : "Закрыть",	click : function() {$(this).dialog("close");}
			} ]
		});

		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
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
    	iframe.src = "owncertdownload.do";
	}
	
	
	// ---------------------------------------------------------------------------------
    // Open Pivot tables Window  
    // ---------------------------------------------------------------------------------
	function reportWindow() {
		link = "ownconfigreport.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Отчет');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 520);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
		$("#pview").dialog({
			buttons : [ { text : "Закрыть",	click : function() {close();} } ]
		});
		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
		$("#pview").dialog("open");
	}

	// ---------------------------------------------------------------------------------
    // Create Orsha report  
    // ---------------------------------------------------------------------------------	
	function reportOrsha() {
		// url = $("#config").attr("action");
		// $.post(url, $("#config").serialize());
		
   		var hiddenIFrameID = 'hiddenDownloader';
        var iframe = document.getElementById(hiddenIFrameID);
        
    	if (iframe == null) {
        	iframe = document.createElement('iframe');
        	iframe.id = hiddenIFrameID;
	    	iframe.style.display = 'none';
	    	document.body.appendChild(iframe);
	    	$("#hiddenDownloader").on('load', function() {alert("IFrame loaded");});	    	
    	}
    	
    	spin('hiddenDownloader');
    	if ($("#reportdate").val() === "")  {
    	    iframe.src = "ownorshareport.do";
    	} else {
    		iframe.src = "ownorshareport.do?reportdate=" + $("#reportdate").val();
    	}
    	$('#hiddenDownloader').data('spinner').stop();
	}
	
	// ---------------------------------------------------------------------------------
    // Create Waste report  
    // ---------------------------------------------------------------------------------	
	function wastereport() {
		
   		var hiddenIFrameID = 'hiddenDownloader';
        var iframe = document.getElementById(hiddenIFrameID);
        alert("Отправлен запрос на формирование отчета по отходам на сервер. Оэидайте результатов обработки.");
    	if (iframe == null) {
        	iframe = document.createElement('iframe');
        	iframe.id = hiddenIFrameID;
	    	iframe.style.display = 'none';
	    	document.body.appendChild(iframe);
    	}
    	$("#hiddenDownloader").on('load', function(event) {  alert("IFrame loaded");} );	    	
    	$("#hiddenDownloader").onload = "function(){parent.iframeload()}";
    	iframe.addEventListener("load", iframeload);
    	iframe.addEventListener("load", iframeload);
    	iframe.addEventListener("onunload", iframeload); 
    	
    	$('#waste').attr("href", "javascript:alert('Формирование отчета об отходах. Ожидайте ответа сервера. После сохренения файла отчета необходимо перегрузить страницу со списком сертификатов для получения возможности повторить формирование отчета.');");
    	spin('hiddenDownloader');
    	if ($("#reportdate").val() === "")  {
    	    iframe.src = "ownwastereport.do";
    	} else {
    		iframe.src = "ownwastereport.do?certdate=" + $("#reportdate").val();
    	}
        $('#hiddenDownloader').data('spinner').stop();
	}
	
	function iframeload() {
		$('#waste').attr("href", "javascript:wastereport();");
	}
	
</script>


<div id="listwindow" class="main">
	<h3>Список сертификатов собственного производства (${ownmanager.pagecount})</h3>
	<table style="width: 100%">
		<tr>

			<td style="width: 20%">
                <input id="filter" type="checkbox"	onclick="javascript:swithFilter();" >
                <span id="filterlink"></span>
            </td>

			<td style="width: 80%; text-align: right">
			       <security:authorize ifAnyGranted="ROLE_EXPERT">		
			         <a id="waste" href="javascript:wastereport();" title="Выгрузить отчет по отходам">
			         <img src="resources/images/wastereport.png" alt="Отчет по отходам" /></a>
				     &nbsp;
				   </security:authorize>
			       <security:authorize ifAnyGranted="ROLE_VITEBSK,ROLE_EXPERT">		
			         <input id="reportdate" class="datepicker" size="12" placeholder="отчет на дату"/>
			         <a href="javascript:reportOrsha();" title="Выгрузить отчет по Оршанскому региону">
			         <img src="resources/images/orshareport.png" alt="Отчет по Орше" /></a>
				     &nbsp;
				   </security:authorize>
				   
				   <security:authorize ifNotGranted="ROLE_MNS">
			          <a href="javascript:reportWindow();" title="Получить сводный отчет по сертификатам">
			          <img src="resources/images/report_24.png" alt="Сводный отчет"/></a>
                      &nbsp;
                   </security:authorize>
                    
				   <a href="javascript:downloadCertificates();"  title="Экспортировать список сертификатов в формате Excel">
				   <img src="resources/images/exp_excel.png" alt="Экспорт в файл формата Excel"/></a>
				   &nbsp;			        
			       Строк на странице: <c:forEach items="${sizes}" var="item">
	           	   &nbsp;	
	               <a href="javascript: goToList('owncerts.do?page=1&pagesize=${item}&orderby=${ownmanager.orderby}
							&order=${ownmanager.order}');">${item}</a>
				</c:forEach>
			</td>
		</tr>
	</table>

	<table class="certificate" style="width: 100%; margin-top: 4px;">
		<tr>
			<c:forEach items="${ownmanager.headers}" var="item">
				<td
					style="width:${item.width}%;background-color: #36478B; color: white"><a
					href="javascript: goToList('${item.link}');" style="color: white; font-size: 110%;">${item.name}${item.selection}

</a></td>
			</c:forEach>
		</tr>

		<c:forEach items="${certs}" var="cert">
			<tr>
				<td><a href="javascript:openCertificate('${cert.id}')">${cert.number}
				</a>
				<c:if test="${cert.filename != null && !cert.filename.isEmpty() && cert.filename.indexOf('pdf') > -1}">
				     <img src="resources/images/pdf.png" style="height: 16; vertical-align: top;" alt="PDF"/>  
				</c:if>
				</td>
				<td>${cert.beltpp.name}</td>
				<td>${cert.customername}</td>
				<td>${cert.blanknumber}</td>
                <td>${cert.datecert}</td>
                <!--  td>${cert.additionalblanks}</td --> 
			</tr>
		</c:forEach>
	</table>

	<table style="width: 100%; margin-top: 10px;">
		<tr>
			<td style="width: 80%; text-align: left"><a
				href="javascript: goToList('${first_page}');"><img
					src="resources/images/first_page_24.png" alt="Перв."> </a> <a
				href="javascript: goToList('${prev_page}');"><img
					src="resources/images/prev_page_24.png" alt="Пред."></a> <c:forEach
					items="${pages}" var="item">
	           	   &nbsp;	
	               <a
						href="javascript: goToList('owncerts.do?page=${item}&pagesize=${ownmanager.pagesize}

&orderby=

${ownmanager.orderby}

&order=${ownmanager.order}');"
						<c:if test="${item==ownmanager.page}">style="border-style: solid; border-width: 

1px;"</c:if>>
						${item} </a>
				</c:forEach> </td>
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

    <!-- p>Время загрузки: ${timeduration}</p--> 
</div>



