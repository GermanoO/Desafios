<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload RestFul</title>
</head>
<body>

	Escolha: <input type="file" name="arq_upload" id="arquivo"/> <br>
	<button id="btn_upload"> Enviar </button>
	
	
</body>
<script type="text/javascript"
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous">
</script>

<script type="text/javascript">
	$(document).ready(function() {
		$("#btn_upload").click(function() {
			var arquivo = $("input[name='arq_upload']").get(0).files[0];
			
			var formData = new FormData();
			formData.append('file', arquivo);
			
			$.ajax({
				url: 'rest/upload',
				type: 'POST',
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function() {
					alert('Upload feito com sucesso!');
				},
				error: function(e) {
					alert(e.status);
				}
			});
		});
	});
</script>

</html>