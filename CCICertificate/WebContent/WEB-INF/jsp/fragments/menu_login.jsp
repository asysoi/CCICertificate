<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.navbar-fixed-top {
  min-height: 58px;
}
.container-fluid {
  min-height: 58px;
}

.dropdown>a .navbar-brand>li>a {
  color: white !important;
}
</style>

<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">

   <div class="container-fluid">
      
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="main.do" style="font-size: 240%; color: white; ">
          <img src="resources/images/logo.svg" width="38" height="38" style="vertical-align: middle;"/>
          Портал БелТПП
          </a>
        </div>

		<div class="navbar-collapse collapse">
		
			<ul class="nav navbar-nav navbar-right">
				<li><a target="_blank" href="https://certs.cci.by" style="color: white;">
    			Проверка сертификата 
				</a></li>
			</ul>
		</div>
		
      </div>
      <div style="height: 2px; width: 100%; background-color: white; border: none   !important;"> </div>
</div>
 
