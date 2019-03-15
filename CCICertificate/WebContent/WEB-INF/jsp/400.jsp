<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="ru">

<jsp:include page="fragments/headTag.jsp"/>

<body>
   <div class="container">
     <jsp:include page="fragments/menu_invers.jsp"/>
     
     <div id="error" class="error" style="color: red; margin-top: 50px;" >${error}</div>
	      
     <jsp:include page="fragments/footer.jsp"/>
   </div>
</body>

</html>