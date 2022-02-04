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
					//$("#datefrom").datepicker("setDate",
					//		"${viewfilter.viewcertificate.datefrom}");
					//$("#dateto").datepicker("setDate",
					//		"${viewfilter.viewcertificate.dateto}");
					$("#codestranapr")
							.val('${viewfilter.viewcertificate.codestranapr}');
					$("#str_otd_id")
							.val('${viewfilter.viewcertificate.str_otd_id}');
					$("#forms").val('${viewfilter.viewcertificate.forms}');
				});

	});

	function clearelement(element) {
		element.val('');
	}
</script>

<form:form id="ffilter" method="POST" commandName="viewfilter">

	<fieldset>
		<legend class="grp_title">Основные данные</legend>

		<table class="filter">
			<tr>
				<td>Форма сертификата</td>
				<td><form:select path="viewcertificate.forms" items="${forms}"
						id="forms" /><a href="javascript:clearelement($('#forms'));">
						<img src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Номер сертификата</td>
				<td><form:input path="viewcertificate.nomercert" id="nomercert" /><a
					href="javascript:clearelement($('#nomercert'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Номер бланка</td>
				<td><form:input path="viewcertificate.nblanka" id="nblanka" /><a
					href="javascript:clearelement($('#nblanka'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Дата сертификата</td>
				<td><form:input path="viewcertificate.datefrom" id="datefrom"
						class="datepicker" size="8" placeholder="с" />&nbsp;-&nbsp; 
						<form:input	path="viewcertificate.dateto" id="dateto" class="datepicker"
						size="8" placeholder="по" /> <a
					href="javascript:clearelement($('.datepicker'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Кол. доплистов</td>
				<td><form:input path="viewcertificate.doplistfrom" id="doplistfrom" class="doplist" 
						size="8" placeholder="от" />&nbsp;-&nbsp; 
						<form:input	path="viewcertificate.doplistto" id="doplistto" class="doplist" 
						size="8" placeholder="до" /> <a
					href="javascript:clearelement($('.doplist'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Эксперт</td>
				<td><form:input path="viewcertificate.expert" id="expert" /><a
					href="javascript:clearelement($('#expert'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			
		</table>
	</fieldset>

	<fieldset>
		<legend class="grp_title">Экспортеры и Импортеры</legend>
		<table class="filter">
		    <tr>
		       <td >УНП экспортера</td>
				<td colspan="3"><form:input path="viewcertificate.unn" id="unn" /><a
					href="javascript:clearelement($('#unn'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>	
			<tr>
				<td>Экспортер</td>
				<td><form:input path="viewcertificate.expp" id="expp" /><a
					href="javascript:clearelement($('#expp'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес экспортера</td>
				<td><form:input path="viewcertificate.adress" id="adress" /><a
					href="javascript:clearelement($('#adress'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Грузоотправитель</td>
				<td><form:input path="viewcertificate.kontrp" id="kontrp" /><a
					href="javascript:clearelement($('#kontrp'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес грузоотправителя</td>
				<td><form:input path="viewcertificate.expadress" id="expadress" /><a
					href="javascript:clearelement($('#expadress'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Иимпортер</td>
				<td><form:input path="viewcertificate.importer" id="importer" /><a
					href="javascript:clearelement($('#importer'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес импортера</td>
				<td><form:input path="viewcertificate.adresspol" id="adresspol" /><a
					href="javascript:clearelement($('#adresspol'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Грузопролучатель</td>
				<td><form:input path="viewcertificate.poluchat" id="poluchat" /><a
					href="javascript:clearelement($('#poluchat'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
				<td>Адрес грузополучателя</td>
				<td><form:input path="viewcertificate.adressimp" id="adressimp" /><a
					href="javascript:clearelement($('#adressimp'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
		</table>
	</fieldset>

	<fieldset>
		<legend class="grp_title">Территории</legend>

		<table class="filter">
			<tr>
				<td>Маршрут транспорта</td>
				<td><form:input path="viewcertificate.marshrut" id="marchrut" /><a
					href="javascript:clearelement($('#marchrut'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<security:authorize ifAnyGranted="ROLE_EXPERT">
			<tr>
				<td>Отделение</td>
				<td>
				      <form:select path="viewcertificate.str_otd_id"
						items="${departments}" id="str_otd_id" />
					  <a href="javascript:clearelement($('#str_otd_id'));"> 
					  <img src="resources/images/delete-16.png" alt="удл." />	</a>
			</td>
			</tr>
			</security:authorize>
			<tr>
				<td>Страна предоставления</td>
				<td><form:select path="viewcertificate.codestranapr"
						items="${countries}" id="codestranapr" /><a
					href="javascript:clearelement($('#codestranapr'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Отметка</td>
				<td><form:input path="viewcertificate.otmetka" id="otmetka" /><a
					href="javascript:clearelement($('#otmetka'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
		</table>
	</fieldset>

	<fieldset>
		<legend class="grp_title">Товарные позиции</legend>

		<table class="filter">
			<tr>
				<td>Товар</td>
				<td><form:input path="viewcertificate.tovar" id="tovar" style="width: 380px"/><a
					href="javascript:clearelement($('#tovar'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Критерий происхождения</td>
				<td><form:input path="viewcertificate.kriter" id="kriter" style="width: 250px"/><a
					href="javascript:clearelement($('#kriter'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr>
				<td>Счет фактура</td>
				<td><form:input path="viewcertificate.schet" id="schet" /><a
					href="javascript:clearelement($('#schet'));"> <img
						src="resources/images/delete-16.png" alt="удл." />
				</a></td>
			</tr>
			<tr />
		</table>
	</fieldset>
</form:form>