<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:url value="resources/css/cci.css" var="CCICss" />

<html> 
<header>

<link href="${CCICss}" rel="stylesheet" />
</header>
<body>

<table>
<tr> 
<td width="5%" height="20px"></td>
<td width="90%" height="20px"></td>
<td width="5%" height="20px"></td>
</tr>
<tr>
<td > </td>
<td width="90%">
<p align="right"><b>${owncert.blanknumber}</b></p>
<h2 align="center">БЕЛОРУССКАЯ ТОРГОВО-ПРОМЫШЛЕННАЯ ПАЛАТА</h2>
<h3 align="center"><b>${owncert.beltpp.name}, ${owncert.beltpp.address}</b></h3>
<h1 align="center"><b>СЕРТИФИКАТ</b></h1> 
<p align="center"> продукции собственного производства</p>   
<p>№ <b>${owncert.number}</b></p>
<p> 1. Производитель: <b>${owncert.customername}, ${owncert.customeraddress}</b> </p> 
<p>Наименование обособленных подразделений юридического лица, осуществляющих производство продукции <br>
место нахождения: <br><b> ${owncert.branches}</b> </p>
<p>2. Регистрационный номер производителя в Едином государственном регистре юридических лиц и индивидуальных<br>
предпринимателей: <b>${owncert.customerunp}</b></p>
<p>3. Место нахождения производства:<b>${owncert.factories}</b></p>
<p>4. Наименование продукции, код продукции в соответствии с единой Товарной номенклатурой внешнеэкономической<br> 
деятельности Таможенного союза:
  <table>
  <tr>
  		    <td>Номер</td>
			<td>Наименование</td>
			<td>Код ТН ВЭД ТС</td>
			</tr>
	
       <c:forEach items="${owncert.products}" var="product">
          <tr>
			<td>${product.number}</td>
			<td>${product.name}</td>
			<td>${product.code}</td>
		  </tr> 
	   </c:forEach>
      </table>

<p>5. Сертификат действителен с  <b>${owncert.datestart}</b>  до  <b>${owncert.dateexpire}</b> </p>
<p>6. На основании результатов провежденной экспертизы навтоящим подтверждаю, что продукция, указанная<br>
в пункте 4 настоящего сертификата относится к продукции собственного производства </p>
 <p style="font-size: 120% "><b>${owncert.signerjob}  ${owncert.signer} ${owncert.datecert}</b></p>
 </td>
 <td></td>
<tr> 
<td width="5%" height="20px"></td>
<td width="90%" height="20px"></td>
<td width="5%" height="20px"></td>
</tr>
</table>
 </body>
</html>
	
	






