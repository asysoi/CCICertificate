<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.navbar-fixed-top {
  min-height: 58px;
}
.container-fluid {
  min-height: 58px;
}
</style>

<div class="navbar navbar-inverse navbar-fixed-top" role="navigation" >


				
   <div class="container-fluid">
      
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="main.do" style="font-size: 200%">
          <img src="resources/images/logo_40.png" width="40" height="40" style="vertical-align: middle;"/>
          Портал БелТПП
          </a>
        </div>

		<div class="navbar-collapse collapse">
		  <security:authorize access="isAuthenticated()">
			<ul class="nav navbar-nav navbar-right">
			
				<form class="navbar-form navbar-right">
            		<input type="text" class="form-control" placeholder="Поиск..." style="height: 24px"/>
          		</form>

			    <!-- 
				<li><a href="main.do">Главная</a></li>
				-->
	
				<security:authorize ifAnyGranted="ROLE_CLIENT,ROLE_EXPERT">				
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">Клиенты<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="members.do">Члены БелТПП</a></li>
						<li><a href="clients.do">Контрагенты</a></li>
					</ul>
				</li>
				</security:authorize>
				
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">ВЭС<b class="caret"></b></a>
					<ul class="dropdown-menu">
					    <li><a href="#">Все Мероприятия</a></li>
                        <li><a href="#">Деловые советы</a></li>					
						<li><a href="#">Форумы</a></li>					
						<li><a href="#">Выствыки</a></li>
						<li><a href="#">Ярмарки</a></li>

					</ul>
				</li>
				

				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">Кадры<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="employees.do">Сотрудники</a></li>					
						<li><a href="#">Приказы</a></li>
						<li><a href="#">Документы</a></li>

					</ul>
				</li>

				<security:authorize ifAnyGranted="ROLE_EXPERT,ROLE_MINSK,ROLE_GOMEL,ROLE_VITEBSK,ROLE_BREST,ROLE_GRODNO,ROLE_MOGILEV">				
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">Сертификаты<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="certs.do">Сертификаты происхождения</a></li>
						<li><a href="reportcerts.do">Отчет о загрузке сертификатов происхождения</a></li>
						<li class="divider"></li>
						<li><a href="owncerts.do">Сертификаты собственного производства</a></li>
						<li><a href="fscerts.do">Сертификаты свободной продажи</a></li>
						<li class="divider"></li>						
						<li><a href="#">Акты экспертиз</a></li>
						<li><a href="certcheck.do">Верификация</a></li>
					</ul>
				</li>
			    </security:authorize>				

                
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">Справочники<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="#">Телефонный</a></li>
						<li><a href="#">Арендные помещения</a></li>
					</ul>
				</li>

				<security:authorize ifAnyGranted="ROLE_ACCOUNT">
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown">Логистика<b class="caret"></b></a>
					<ul class="dropdown-menu">
                        <li><a href="purchases.do">Сделки</a></li>
						<li><a href="#">Договора</a></li>					
					</ul>
				</li>
			    </security:authorize>
				
				<li>
				    <a href="logout.do">
					<security:authorize access="isAuthenticated()">
    				Выйти <security:authentication property="principal.username" /> 
					</security:authorize>
				    </a>
				</li>
				
				<li><a href="help.do">Справка</a></li>
			</ul>
		  </security:authorize>
		</div>
						
      </div>
      <div style="height: 2px; width: 100%; background-color: yellow; border: none   !important;"> </div>
    </div>



