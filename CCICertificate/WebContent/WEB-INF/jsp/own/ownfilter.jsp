<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<script>
	$(function() {

		$(".datepicker").datepicker({
			changeMonth : true,
			changeYear : true
		});

		$("document").ready(
				function() {
					$(".datepicker").datepicker("option", "dateFormat",
							'dd.mm.yy');
					$("#datefrom").datepicker("setDate",
							"${viewownfilter.viewcertificate.datecertfrom}");
					$("#dateto").datepicker("setDate",
							"${viewownfilter.viewcertificate.datecertto}");
					$("#otd_id")
							.val('${viewownfilter.viewcertificate.otd_id}');
					$("#types").val('${viewownfilter.viewcertificate.type}');
				});

	});

	function clearelement(element) {
		element.val('');
	}
</script>

<form:form id="ffilter" method="POST" commandName="viewownfilter">

	<fieldset>
		<legend class="grp_title">Основные данные</legend>

		<table class="filter">
			<tr>
				<td>Тип сертификата </td>
				<td> <form:select path="viewcertificate.type" items="${types}"
						id="types" /><a href="javascript:clearelement($('#types'));">
						<img src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Номер сертификата </td>
				<td><form:input path="viewcertificate.number" id="number" /><a
					href="javascript:clearelement($('#number'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Номер бланка </td>
				<td><form:input path="viewcertificate.blanknumber" id="blanknumber" /><a
					href="javascript:clearelement($('#blanknumber'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Дата сертификата</td>
				<td><form:input path="viewcertificate.datecertfrom" id="datecertfrom"
						class="datepicker" size="8" placeholder="с" />&nbsp;-&nbsp; 
						<form:input	path="viewcertificate.datecertto" id="datecertto" class="datepicker"
						size="8" placeholder="по" /> <a
					href="javascript:clearelement($('.datepicker'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
		</table>
	</fieldset>
	<fieldset>
	
		<legend class="grp_title">Производство</legend>
		<table class="filter">
			<tr>
				<td>УНП производителя </td>
				<td><form:input path="viewcertificate.customerunp" id="customerunp" /><a
					href="javascript:clearelement($('#customerunp'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Наимен. производителя </td>
				<td><form:input path="viewcertificate.customername" id="customername" /><a
					href="javascript:clearelement($('#customername'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Адрес производителя</td>
				<td><form:input path="viewcertificate.customeraddress" id="customeraddress" /><a
					href="javascript:clearelement($('#customeraddress'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес производства</td>
				<td><form:input path="viewcertificate.factoryaddress" id="factoryaddress" /><a
					href="javascript:clearelement($('#factoryaddress'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			
			<tr>
				<td>Наимен. подразделения</td>
				<td><form:input path="viewcertificate.branchname" id="branchname" /><a
					href="javascript:clearelement($('#branchname'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес подразделения</td>
				<td><form:input path="viewcertificate.branchaddress" id="branchaddress" /><a
					href="javascript:clearelement($('#branchaddress'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			
		</table>
	</fieldset>

	<fieldset>
		<legend class="grp_title">Выдан</legend>

		<table class="filter">
			<security:authorize ifAnyGranted="ROLE_EXPERT">
			<tr>
				<td>Отделение</td>
				<td>
				      <form:select path="viewcertificate.otd_id"
						items="${departments}" id="otd_id" />
					  <a href="javascript:clearelement($('#otd_id'));"> 
					  <img src="resources/images/delete-16.png" alt="удл." />	</a>
			</td>
			</tr>
			</security:authorize>
						<tr>
				<td>Эксперт</td>
				<td><form:input path="viewcertificate.expert" id="expert" /><a
					href="javascript:clearelement($('#expert'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Подписан</td>
				<td><form:input path="viewcertificate.signer" id="signer" /><a
					href="javascript:clearelement($('#signer'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			
			
		</table>
	</fieldset>

	<fieldset>
		<legend class="grp_title">Товарные позиции</legend>

		<table class="filter">
			<tr>
				<td>Продукция/Услуга</td>
				<td><form:input path="viewcertificate.productname" id="productname" style="width: 380px"/><a
					href="javascript:clearelement($('#productname'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Код товара</td>
				<td><form:input path="viewcertificate.productcode" id="productcode" style="width: 250px"/><a
					href="javascript:clearelement($('#productcode'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
		</table>
	</fieldset>
</form:form>