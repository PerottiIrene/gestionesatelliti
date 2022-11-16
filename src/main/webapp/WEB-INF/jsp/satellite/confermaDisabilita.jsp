<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="it" class="h-100" >
	 <head>

	 	<!-- Common imports in pages -->
	 	<jsp:include page="../header.jsp" />
	 	
	   <title>Visualizza Elemento</title>
	   
	 </head>
	   <body class="d-flex flex-column h-100">
	   
	   		<!-- Fixed navbar -->
	   		<jsp:include page="../navbar.jsp"></jsp:include>
	    
			
			<!-- Begin page content -->
			<main class="flex-shrink-0">
			  <div class="container">
			  
			  		<div class='card'>
					    <div class='card-header'>
					        <h5>Visualizza dettaglio</h5>
					    </div>
					    
					<h2> Sta per essere applicata la procedura di emergenza. Si e' sicuri di voler procedere?</h2>
					    <div class='card-body'>
					    	<dl class="row">
							  <dt class="col-sm-3 text-right">Numero satelliti presenti:</dt>
							  <dd class="col-sm-9">${satellite_list_attribute.size()}</dd>
					    	</dl>
					    	
					     <dl class="row">
							  <dt class="col-sm-3 text-right">Numero satelliti che verranno modificati:</dt>
							  <dd class="col-sm-9">${satellite_list_disabilita.size()}</dd>
					    	</dl>
					    	
					    	
					    	
					    	
					    </div>
					    <!-- end card body -->
					    
					<div class='card-footer'>
						<form action="${pageContext.request.contextPath}/satellite/disabilitaTutti" method="post">
						 	<button type="submit" name="submit" value="submit" id="submit" class="btn btn-secondary" style='width:210px'>Conferma disabilitazione</button>
						</form>
	
					</div>
					<!-- end card -->
					</div>	
			  
			    
			  <!-- end container -->  
			  </div>
			  
			</main>
			
			<!-- Footer -->
			<jsp:include page="../footer.jsp" />
	  </body>
</html>