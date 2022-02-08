<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:url value="resources/css/cci.css" var="CCICss" />
<link href="${CCICss}" rel="stylesheet" />

<script>

    $(function() {
	  $(".datepicker").datepicker({
		changeMonth : true,
		changeYear : true
	  });
    });

	function clear() {
		$('input').val('');
		$('select').val('');
	}
	
	function clearelement(element) {
		element.val('');
	}
	
	function reset() {
		$('#ffilter')[0].reset();
	}

	function submit() {
		url = $("#ffilter").attr("action");
		$.post(url, $("#ffilter").serialize());
		$( document ).ajaxComplete(function(event,request, settings ) {
			  goToList('reportcerts.do?page=1&pagesize=${rmanager.pagesize}&orderby=${rmanager.orderby}&order=${rmanager.order}');
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
    	$(".datepicker").datepicker("option", "dateFormat",
		'dd.mm.yy');
		$("#datefrom").datepicker("setDate",
			"${datefrom}");
		$("#dateto").datepicker("setDate",
			"${dateto}");
	});
	
	

	function goToList(link) {
		var url = link;
		spin();
		url = url + "&datefrom="
			        + document.getElementById("datefrom").value
			        + "&dateto="
			        + document.getElementById("dateto").value;
   	    
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
	        var target = document.getElementById('reportwindow');
	        var spinner = new Spinner(opts).spin(target);

	}
	
	
	function viewCertificate(certid) {
		memo = "Воспроизведение бумажной версиисертификата. <p>" + 
		       "Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
		       "но полностью воспроизводит содержание документа.</p>"
        $('#pdf').contents().find("body").html("<div style='color:black; text-align:center; font-size:16pt;'>" + memo + "</div> ");
                                   $('#pdf').contents().find('body').attr('style', 'background-color: white'); 
		link = "certgo.do?certid=" + certid;
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
			of :  reportwindow
		});
                                   
        $('#pdf').attr('height', $("#pdfview").dialog("option", "height") - 150);
        $('#pdf').attr('width', $("#pdfview").dialog("option", "width") - 40);
        $('#pdf').attr('scrolling', 'yes');
		$('#pdf').attr('src', link);

		$("#pdfview").dialog("open");
	}

	function openCertificate(certid) {
		memo = "Воспроизведение бумажной версиисертификата. <p>" + 
		       "Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
		       "но полностью воспроизводит содержание документа.</p>"
        //$('#pdf').contents().find("body").html("<div style='color:black; text-align:center; font-size:16pt;'>" + memo + "</div> ");
        //                           $('#pdf').contents().find('body').attr('style', 'background-color: white'); 
		url = "certgo.do?certid=" + certid;
		var win=window.open(url,'_blank');
		win.focus();
	}
	
    // ---------------------------------------------------------------------------------
    // Download list to Excel файл 
    // ---------------------------------------------------------------------------------
	function downloadCertificates() {
		link = "reportconfig.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Экспорт списка сертификатов');
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
    	iframe.src = "reportdownload.do";
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


<div id="reportwindow" class="main">
	<h3>Список загрузки сертификатов</h3>
	<table style="width: 100%">
		<tr>

			<td style="width: 60%">
               <input id="datefrom"
						class="datepicker" size="8" placeholder="с" />&nbsp;-&nbsp; 
				<input	id="dateto" class="datepicker"
						size="8" placeholder="по" /> 
					<a href="javascript: goToList('reportcerts.do?page=1&pagesize=${rmanager.pagesize}&orderby=${rmanager.orderby}&order=${rmanager.order}');"> 
					<img src="resources/images/refresh_16.png" alt="удл."/></a>
					<a href="javascript:clearelement($('.datepicker'));"> 
					<img src="resources/images/delete-16.png" alt="удл."/>	</a>    
                
            </td>

			<td style="width: 40%; text-align: right">
			       <a href="javascript:downloadCertificates();"><img src="resources/images/exp_excel.png"alt="Загрузить"/></a>
				   &nbsp;			        
			       Строк в списке: <c:forEach items="${sizes}" var="item">
	           	   &nbsp;	
	               <a
						href="javascript: goToList('reportcerts.do?page=1&pagesize=${item}&orderby=${rmanager.orderby}&order=${rmanager.order}');">${item}</a>
				</c:forEach>
			</td>


		</tr>
	</table>

	<table class="certificate" style="width: 100%; margin-top: 6px;">
		<tr>
			<c:forEach items="${rmanager.headers}" var="item">
				<td
					style="width:${item.width}%;background-color: #36478B; color: white"><a
					href="javascript: goToList('${item.link}');" style="color: white; font-size: 110%;">${item.name}${item.selection}

</a></td>
			</c:forEach>
		</tr>

		<c:forEach items="${certs}" var="cert">
			<tr>
			    <td>${cert.date_load}</td>
				<td><a href="javascript:openCertificate('${cert.cert_id}')">${cert.nomercert}</a></td>
				<td>${cert.otd_name}</td>
				<td>${cert.expert}</td>
				<td>${cert.nblanka}</td>
				<td>${cert.datacert}</td>
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
						href="javascript: goToList('reportcerts.do?page=${item}&pagesize=${rmanager.pagesize}&orderby=${rmanager.orderby}&order=${rmanager.order}');"
						<c:if test="${item==rmanager.page}">style="border-style: solid; border-width: 

1px;"</c:if>>
						${item} </a>
				</c:forEach> &nbsp; [Сертификатов:&nbsp;${rmanager.pagecount}]</td>
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

</div>



