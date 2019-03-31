﻿﻿<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:url value="resources/css/login.css" var="LoginCss" />
<link href="${LoginCss}" rel="stylesheet" />

<script>

   $("document").ready(function() {
		$("#pdfview").dialog({
			autoOpen : false
		});
	});

	$(function() {
	       $('.numeric-only').keypress(function(e) {
		  if (e.keyCode == 8 || e.keyCode == 46 || e.keyCode == 37 || e.keyCode == 38 || e.keyCode == 39 || e.keyCode == 40 ) {
			  return true;
		  }
		 if(isNaN(this.value + "" + String.fromCharCode(e.charCode)) || e.charCode == 32) {
			  return false;
  		    }
          })
	      .on("cut copy paste",function(e){
		e.preventDefault();
	   });
	});

	$(function() {
		$("#datepicker").datepicker({
			changeMonth : true,
			changeYear : true
		});
		$("document").ready(function() {
			$("#datepicker").datepicker("option", "dateFormat", 'dd.mm.yy');
			$("#datepicker").datepicker("setDate", "${cert.datacert}");
		});
	});

    function setLanguage(lang) {
		url = "check.do?lang=" + lang 
				+ "&ncert=" + $("#nomercert").val()  
				+ "&nblanka=" + $("#nblanka").val() 
				+ "&datecert=" + $("#datepicker").val();
  		document.location.href = url;
    }
    
    function setType(type) {
		url = "check.do?type=" + type 
				+ "&ncert=" + $("#nomercert").val()  
				+ "&nblanka=" + $("#nblanka").val() 
				+ "&datecert=" + $("#datepicker").val();
  		document.location.href = url;
    }

	
	function submit() {
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
	    var spinner = new Spinner(opts).spin(document.getElementById('checker'));
		var errors = 0;
		$("#certview").empty();
		
	    $("#form :input").map(function(){
	         if( !$(this).val() ) {
                              $(this).addClass('error_input');
	             errors++;
    	        } else {
                             $(this).removeClass('error_input');
                        }
	    });
	    
	    if(errors > 0) {
			spinner.stop();
	    } else {
			url = "check.do";
			var posting = $.post(url, $("#form").serialize());
             
           	posting.done(function(data) {
			   var content = $(data).find("#message");
			   $("#certview").html(content);
			   spinner.stop();
		    });
	    }
	}

	function viewCertificate(link) {
	    $('#pdf').contents().find('body').attr('style', 'background-color: black'); 
		$("#pdfview").dialog("option", "title", 'Сертификат');
		$("#pdfview").dialog("option", "width", 963);
		$("#pdfview").dialog("option", "height", 570);
		$("#pdfview").dialog("option", "modal", true);
		$("#pdfview").dialog("option", "resizable", false);
		$("#pdfview").dialog({
			buttons : [ 	{ text : "Закрыть",	click : function() {$(this).dialog("close"); $('#pdf').contents().find("body").html('');}} ]
		});

		$("#pdfview").dialog("option", "position", {
			my : "center",
			at : "center",
			of : window
		});
                                   
		$('#pdf').attr('height', $("#pdfview").dialog("option", "height") - 150);
		$('#pdf').attr('width', $("#pdfview").dialog("option", "width") - 40);
	                $('#pdf').attr('scrolling', 'yes');
		$('#pdf').attr('src', link);

		$("#pdfview").dialog("open");
	}

	function openCertificate(link) {
		var win=window.open(link,'_blank');
		win.focus();
	}

</script>

<div style="text-align:right; margin-top:40px;">
	   <c:if test="${lang=='ru'}">  
		    <a href="javascript:setType('ct1')">Сертификат происхождения </a> &nbsp|&nbsp
		    <a href="javascript:setType('own')"> Сертификат собственного производства</a>
	   </c:if>
	   <c:if test="${lang=='eng'}">  
		    <a href="javascript:setType('ct1')">Certificate of origin </a> &nbsp|&nbsp
		    <a href="javascript:setType('own')"> Certificate of own production</a>
	   </c:if>
</div>

<div class="main" id="checker">
     <c:if test="${type=='ct1'}">
    	<c:if test="${lang=='ru'}">
	        <h3  style="margin-top: 15; text-align:center;">Cертификат о происхождении товаров</h3>
    	</c:if>
    	<c:if test="${lang=='eng'}">
        	<h3 style="margin-top: 15; text-align:center;">Certificate of origin</h3>
    	</c:if>
    </c:if>
    
    <c:if test="${type=='own'}">
    	<c:if test="${lang=='ru'}">
	        <h3  style="margin-top: 15; text-align:center;">Cертификат собственного производства</h3>
    	</c:if>
    	<c:if test="${lang=='eng'}">
        	<h3  style="margin-top: 15; text-align:center;">Certificate of own production</h3>
    	</c:if>
    </c:if>
	
    <div class="row placeholders" id="msg" class="ver_message">

    <c:if test="${lang=='ru'}">
    <div style="margin: auto; width:70%; text-align: justify; font-size: 100%;  " >  
    <p  style="margin: 10px 10px 10px 10px; text-align: justify; font-size: 110%;">
        Сервис проверки сертификатов о происхождении товарa, сертификатов собственного производства 
        позволяет удостовериться, что сертификат действительно выдан Белорусской торгово-промышленной палатой, 
        являющейся уполномоченным органом по выдаче этих сертификатов в Республике Беларусь</p>    
    </div>
    </c:if>

    <c:if test="${lang=='eng'}">
    <div style="margin: auto; width:70%; text-align: justify; font-size: 100%;" >
     <p  style="margin: 10px 10px 10px 10px; text-align: justify; font-size: 110%;"> 
     The verification service of the certificates of origin and certificates of own production 
     make it possible to verify that the certificate was actually issued by the Belarusian Chamber of Commerce and Industry 
     which is authorized for issuing these certificates in the Republic of Belarus</p> 
    </div>
    </c:if>
   
   
	<form:form id="form" method="POST" commandName="cert" role="form" >
		<table class="verification" style="margin-top: 20px;">
			<tr style="height: 46px; ">
				<td style="text-align: right; height: 36px; vertical-align: middle; font-size: 120%;">
				
    			<c:if test="${lang=='ru'}">
						Номер сертификата&nbsp;        
    			</c:if>
    			<c:if test="${lang=='eng'}">
        				Certificate number&nbsp;
    			</c:if>
                 
				</td>
				<td><form:input path="nomercert" class="form-control" type="text" 
				placeholder="Введите номер сертификата с учетом регистра" id="nomercert"
				style="height: 28px; width: 65%;" /></td>
			</tr>
			<tr style="height: 46px;">
				<td style="text-align: right; height: 36px; vertical-align: middle; font-size: 120%;">
				<c:if test="${lang=='ru'}">
						Номер бланка&nbsp;        
    			</c:if>
    			<c:if test="${lang=='eng'}">
        				Form number&nbsp;
    			</c:if>
				</td>
				<td><form:input path="nblanka" class="form-control required numeric-only"   type="text" placeholder="Номер бланка состоит только из цифр" 

id="nblanka" style="height: 28px; width: 65%;"/></td>
			<tr style="height: 46px;">
				<td style="text-align: right; height: 36px; vertical-align: middle; font-size: 120%;">
				<c:if test="${lang=='ru'}">
						Дата выдачи&nbsp;        
    			</c:if>
    			<c:if test="${lang=='eng'}">
        				Issue date&nbsp;
    			</c:if>
				</td>
				<td><form:input path="datacert" class="form-control" placeholder="" id="datepicker" style="height: 28px; width: 20%;"/></td>
			</tr>
			<tr>
				<td />
				<td>
					<div align="left">
						<div class="btn bt1n-lg btn-primary btn-block" style="background-color: #36478B; width: 120; background: linear-gradient(to top left, #395B8D, #C1DDF1, #395B8D); color: #36478B;">
						<a href="javascript:submit()" style="height: 24px; font-size: 110%">
						<c:if test="${lang=='ru'}">
							Проверить        
    						</c:if>
    						<c:if test="${lang=='eng'}">
        						Check
	     					</c:if>
						</a>   
  						</div>

					</div>
				</td>
			</tr>
			
		</table>
	</form:form>

	    
     </div>
     
      <div id="certview" name="certview" align="center"></div>
      <div id="pdfview" name="pdfview" style="text-align:center;">
	     <iframe class="pdf" id="pdf"></iframe>
       </div> 
 
   	<div class="footer">
     <div class="yline">
         <p style=" padding-top: 10px; padding-left: 15px;"> <a class="brand" href="https://www.cci.by"> © Белорусская торгово-промышленная палата, 2017</a> </p>
     </div>
    </div>
 
</div>


