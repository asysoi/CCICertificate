﻿<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<spring:url value="resources/css/login.css" var="LoginCss"/>
<link href="${LoginCss}" rel="stylesheet"/>


<div class="main">
	

	<div class="row placeholders">
	
    <h2 class="page-header">Вход в систему</h2>
    
	<div class="container">
			<form class="form-signin" action="/CCICertificate/j_spring_security_check" method="POST">
   				<input id="username" name="j_username" placeholder="Имя пользователя" class="form-control" type="text">
 				<input id="password" name="j_password" placeholder="Пароль" class="form-control" type="password">
				<br>
 				<button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
			</form>			
	</div>
	
	</div>

</div>


















