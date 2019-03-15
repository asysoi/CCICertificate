<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">

<jsp:include page="fragments/headTag.jsp" />

<body>

	<jsp:include page="fragments/menu_login.jsp" />

	<div class="container-fluid">
		<div class="row">

			<h1>HTTP Status 403 - Access is denied</h1>

			<c:choose>
				<c:when test="${empty username}">
					<h2>You do not have permission to access this page!</h2>
				</c:when>
				<c:otherwise>
					<h2>
						Имя пользователя : ${username} <br /> Вы не имеете прав для доступа к этой странице!
					</h2>
				</c:otherwise>
			</c:choose>
		</div>
	</div>


</body>
</html>