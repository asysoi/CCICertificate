﻿<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:url value="resources/css/login.css" var="LoginCss" />
<link href="${LoginCss}" rel="stylesheet" />


<div class="col-md-10 col-md-offset-2 main">
	<h1 class="page-header">Сертификат</h1>

	<div class="row placeholders">

		<div class="container">

			<table style="text-align: left; width: 951px; height: 390px;"
				border="1" cellpadding="2" cellspacing="2">
				<tbody>
					<tr>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; height: 89px; width: 69px;">&nbsp;
							1. Грузоотправитель/экспортер(наименование и адрес)<br>
							${cert.kontrs}<br>
							${cert.adress}<br>
							</td>
						<td colspan="3" rowspan="2"
							style="vertical-align: top; width: 85px;">4.&nbsp;&nbsp;&nbsp;
							№  <u>${cert.nomercert}</u> <br> <br>
							<div style="text-align: center;">
								Сертификат <br> о происхождении товара<br> форма СТ-1<br>
								<br>
								<div style="text-align: left;">
									&nbsp; Выдан <span style="text-decoration: underline;">в&nbsp;
										Республика Беларусь</span><br> &nbsp; Для представления в
									<u>${cert.stranapr} </u>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; height: 28px; width: 69px;">. 2.
							Грузополучатель/импортер(наименование и адрес)<BR>
							${cert.poluchat}<br>${cert.adresspol}
							</td>
					</tr>
					<tr>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; height: 50px; width: 69px;">&nbsp;
							3. Средства транспорта и маршрут следования <br> (насколько
							это известно)<br>${cert.transport}, ${cert.marshrut}<br> 
						</td>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; width: 85px;">5. Для служебных
							отметок<br>
						</td>
					</tr>
					<tr>
						<td colspan="1" rowspan="1"
							style="vertical-align: top; width: 45px;">6. №<br>
						</td>
						<td colspan="1" rowspan="1"
							style="vertical-align: top; width: 69px;">7. Кол-во<br>
							мест и вид<br> упоковки<br>
						</td>
						<td style="vertical-align: top; width: 435px;">8. Описание
							товара<br>
						</td>
						<td style="vertical-align: top; width: 128px;">9. Критерий
							происхождения<br>
						</td>
						<td style="vertical-align: top; width: 137px;">10. Количество
							товара<br>
						</td>
						<td style="vertical-align: top; width: 85px;">11. Номер и
							дата счет фактуры<br>
						</td>
					</tr>
					<tr>
					 
						<td colspan="1" rowspan="1" style="vertical-align: top; height: 181px; width: 45px;">
					         <c:forEach items="${cert.products}" var="product">	
                                ${product.numerator}<br> 						   
						     </c:forEach>
						</td>
					 	
						<td colspan="1" rowspan="1"	style="vertical-align: top; height: 181px; width: 69px;">
						     <c:forEach items="${cert.products}" var="product">	
                                ${product.vidup}<br> 						   
						     </c:forEach>
						</td>
						
						<td style="vertical-align: top; width: 435px;">
						     <c:forEach items="${cert.products}" var="product">	
                                ${product.tovar}<br> 						   
						     </c:forEach>
						</td>
						
						<td style="vertical-align: top; width: 128px;">
						     <c:forEach items="${cert.products}" var="product">	
                                ${product.kriter}<br> 						   
						     </c:forEach>
						</td>
						
						<td style="vertical-align: top; width: 137px;">
						     <c:forEach items="${cert.products}" var="product">	
                                ${product.ves}<br> 						   
						     </c:forEach>
						</td>
						
						<td style="vertical-align: top; width: 85px;">
						     <c:forEach items="${cert.products}" var="product">	
                                ${product.schet}<br> 						   
						     </c:forEach>
						</td>
					</tr>
					
					<tr>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; height: 0px; width: 69px;">12.
							Удостоверение<br>
							Настоящим удостоверяется, что декларация<br>
							заявителя соответствует действительности<br>
							<br><br>
							Унитарное предприятие по оказанию услуг<br>
							${cert.otd_name},<br>
							${cert.otd_address_index}, ${cert.otd_address_city}, ${cert.otd_address_line}, ${cert.otd_address_home}<br>
							<br><br>
							Эксперт:  ${cert.expert}<br>  
							Дата:     ${cert.datacert}<br>         
							
						</td>
						<td colspan="3" rowspan="1"
							style="vertical-align: top; height: 0px; width: 85px;">13.
							Декларация заявителя<br>
							Нижеподписавшийся заявляет, что вышепреведенные сведения соответствуют действительности: 
							что все товары полностьб произведены или подвергнуты достаточной переработке <br>
							<div><u>Республике Беларусь</u></div> <br>
							и что они отвечают требованиям происхождения, установленными в отношении таких товаров
							<br>
							<br>
							Подписал: ${cert.rukovod}<br>
							Дата: ${cert.datacert}
							
						</td>
					</tr>
				</tbody>
			</table>
			<br>

		</div>
	</div>
</div>