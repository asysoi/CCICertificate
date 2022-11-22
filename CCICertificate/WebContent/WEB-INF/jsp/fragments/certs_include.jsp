<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

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
			  goToList('certs.do?page=1&pagesize=${vmanager.pagesize}&orderby=${vmanager.orderby}&order=${vmanager.order}');
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
        document.getElementById("filter").checked=${vmanager.onfilter};

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
		goToList('certs.do?page=1&pagesize=${vmanager.pagesize}&orderby=${vmanager.orderby}&order=${vmanager.order}');
		
		if (document.getElementById("filter").checked) {
			$("#filterlink").html('<a href="javascript: loadWindow();">&nbsp;Фильтр</a>');
		} else {
			$("#filterlink").html('&nbsp;Фильтр');
		}
	}

	function loadWindow(link) {
        link="certfilter.do?&pagesize=${vmanager.pagesize}&orderby=${vmanager.orderby}&order=${vmanager.order}";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Фильтр поиска');
		$("#pview").dialog("option", "width", 800);
		$("#pview").dialog("option", "height", 680);
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
		
		//$("#pview").dialog( "option", "position", { my: "center",  at: "center", of:window} );
		//$("#pview").dialog("open");
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

		//$("#pdfview").dialog("option", "position", {	my : "center top",at : "center",of :  listwindow});

		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
                                   
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
		url = "certgo.do?certid=" + certid;
		var win=window.open(url,'_blank');
		win.focus();
	}
	
	function deleteCertificate(number, nblank, datecert) {
		$("#pview").dialog("option", "title", 'Удаление сертификата');
		$("#pview").dialog("option", "width", 550);
		$("#pview").dialog("option", "height", 320);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
        $("#pview").html('<p align="center">Удалять сертификат номер ' + number + ' на бланке ' + nblank + ', выданный  '  + datecert + '? <p><p align="center"> Не спеши, подумай ...');
  
		$("#pview").dialog({
			buttons : [ { text : "Удалить",	click : function() {
			$.ajax({
				    url: "rcert.do?number=" + number+ "&nblank="+ nblank + "&date=" + datecert,
				    type: 'DELETE'
			}).done(function(response) {
	    	        $("#pview").html(response);
                    sleep(2000);
                    location.reload();
			}).fail(function (jqXHR, textStatus, errorThrown) {
	    	        $("#pview").html("Ошибка удаления: " +jqXHR.status + " <p> " + jqXHR.responseText);
			});}}, 
			       { text : "Закрыть",  click : function() {$(this).dialog("close");}
			} ]
		});

		//$("#pview").dialog("option", "position", {my : "center",at : "center",of : window});
		//$("#pview").dialog("open");
		
		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
		$("#pview").dialog("open");		

	}
	function sleep(ms) {
	      setTimeout(sleep, ms);
	}

		
    // ---------------------------------------------------------------------------------
    // Download list to Excel файл 
    // ---------------------------------------------------------------------------------
	function downloadCertificates() {
		link = "certconfig.do";
		$("#pview").load(link);
		$("#pview").dialog("option", "title", 'Экспорт списка сертификатов');
		$("#pview").dialog("option", "width", 850);
		$("#pview").dialog("option", "height", 520);
		$("#pview").dialog("option", "modal", true);
		$("#pview").dialog("option", "resizable", false);
		$("#pview").dialog({
			buttons : [ { text : "Загрузить",	click : function() {download();}}, 
 				{ text : "Очистить Все ", click : function() {clearconfig(); }}, 
 				// { text : "Выбрать Все ", click : function() {selectall(); }}, 
				{ text : "Закрыть",	click : function() {$(this).dialog("close");}
			} ]
		});

		//$("#pview").dialog("option", "position", {	my : "center",	at : "center",	of : window	});
		//$("#pview").dialog("open");
		
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
    	iframe.src = "certdownload.do";
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

		//$("#pview").dialog("option", "position", {	my : "center",at : "center",of : window	});
		//$("#pview").dialog("open");
		
		$("#pview").dialog({position: {  my: 'top',  at: 'top+130', of: window, collision: "none"}});
		$("#pview").on( "dialogopen",  function (event, ui) {$(event.target).parent().css("position", "fixed");});		
		$("#pview").dialog("open");		

		
	}



</script>


<div id="listwindow" class="main">
	<h3>Список сертификатов происхождения товара (${vmanager.pagecount})</h3>
	<table style="width: 100%">
		<tr>

			<td style="width: 60%">
                <input id="filter" type="checkbox"	onclick="javascript:swithFilter();" >
                <span id="filterlink"></span>
            </td>

			<td style="width: 40%; text-align: right">
			       <security:authorize ifNotGranted="ROLE_MNS">
   				      <a href="javascript:reportWindow();"><img src="resources/images/report_24.png" alt="Отчеты"/></a>
				   </security:authorize>
				   &nbsp;
				   <a href="javascript:downloadCertificates();"><img src="resources/images/exp_excel.png"alt="Загрузить"/></a>
				   &nbsp;			        
			       Строк в списке: <c:forEach items="${sizes}" var="item">
	           	   &nbsp;	
	               <a
						href="javascript: goToList('certs.do?page=1&pagesize=${item}&orderby=${vmanager.orderby}&order=${vmanager.order}');">${item}
				   </a>
				</c:forEach>
			</td>


		</tr>
	</table>

	<table class="certificate" style="width: 100%; margin-top: 4px;">
		<tr>
			<c:forEach items="${vmanager.headers}" var="item">
				<td
					style="width:${item.width}%;background-color: #36478B; color: white"><a
					href="javascript: goToList('${item.link}');" style="color: white; font-size: 110%;">${item.name}${item.selection}

</a></td>
			</c:forEach>
		</tr>

		<c:forEach items="${certs}" var="cert">
			<tr>
				<td><a href="javascript:openCertificate('${cert.cert_id}')">${cert.nomercert}</a>&nbsp;
  	            <c:if test="${cert.otd_id == otd}">
					<a href="javascript:deleteCertificate('${cert.nomercert}', '${cert.nblanka}', '${cert.datacert}')">
					<img src="resources/images/trash_16.png" alt="удл." />
                    </a>
				</c:if>	
				</td>
				<td>${cert.otd_name}</td>
				<td>${cert.kontrp}</td>
				<td>${cert.nblanka}</td>
				<td>${cert.datacert}</td>
                <td>
                  <c:if test="${cert.koldoplist > 0}"> 
                       ${cert.koldoplist}
                  </c:if> 
                </td> 
				<!-- 
	        <td>
	        <c:if test="${cert.child_id != null}">
	            <a href="certgo.do?certid=${cert.child_id}">child</a>
	        </c:if>    
	        </td>  
	        -->

				<td>
					<c:choose>
    					<c:when test="${cert.parent_id > 0}"> 
						    <a href="javascript:openCertificate('${cert.parent_id}')">${cert.parentnumber}</a>
    					</c:when>
					    <c:when test="${cert.parentnumber != null}">
							${cert.parentnumber}
	   					</c:when>
					</c:choose>
				</td>
					
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
						href="javascript: goToList('certs.do?page=${item}&pagesize=${vmanager.pagesize}

&orderby=

${vmanager.orderby}

&order=${vmanager.order}');"
						<c:if test="${item==vmanager.page}">style="border-style: solid; border-width: 

1px;"</c:if>>
						${item} </a>
				</c:forEach></td>
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

    <!-- p>Время загрузки: ${timeduration}</p --> 
</div>



