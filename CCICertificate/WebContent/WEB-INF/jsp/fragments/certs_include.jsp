<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:url value="resources/css/login.css" var="LoginCss"/>
<link href="${LoginCss}" rel="stylesheet"/>


<div class="col-md-10 col-md-offset-2 main">
     <table style="width:100%">     
      <tr>
      <th style="width:10%">Код сертификата</th>
      <th style="width:20%">Номер сертификата</th>
      <th style="width:30%">Форма Сертификата</th>
      <th style="width:15%">УНП</th>
      <th style="width:15%">Номер бланка</th>
      <th style="width:10%">Дата Сертификата</th>
      </tr>  
	 <c:forEach items="${certs}" var="cert">
	        <tr>
	        <td style="width:10%">${cert.cert_id}</td>
	        <td style="width:20%">${cert.nomercert}</td>
	        <td style="width:30%">${cert.forms}</td>
	        <td style="width:15%">${cert.unn}</td>
	        <td style="width:15%">${cert.nblanka}</td>
	        <td style="width:10%">${cert.datacert}</td>
	        </tr>
     </c:forEach>
     <tr>
     <td><a href="${prev_page}">Предыдущая страница</a></td>
     <td> </td><td></td><td> </td><td> </td>
     <td><a href="${next_page}">Следующая страница</a></td>
     </tr>
     </table>     
     
</div>
