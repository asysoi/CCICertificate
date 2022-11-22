<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<spring:url value="resources/css/login.css" var="LoginCss"/>
<link href="${LoginCss}" rel="stylesheet"/>


<div class="main">
	

	<div class="row placeholders">
	
    <h2 class="page-header">Вход на портал БелТПП</h2>
    
	<div class="container">
	        
			<form class="form-signin" action="/CCICertificate/j_spring_security_check" method="POST">
   				<input style="margin:10px;"  id="username" name="j_username" 
   					placeholder="Имя пользователя" class="form-control" type="text" value="${j_username}">
   				<input style="margin:10px;" id="password" name="j_password" 
 					placeholder="Пароль" class="form-control" type="password">
 					
 				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<br>
 				<button style="margin:10px;" class="btn btn-lg btn-primary btn-block" 
 					type="submit" style="background-color: #36478B;">Войти</button>
			</form>
			
			<p style="margin:10px;">${eMessage}</p>
			         
	</div>
	
	</div>

</div>


















