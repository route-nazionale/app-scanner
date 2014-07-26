<?php
	
	$mysql_host = '172.16.10.151';
	$mysql_db = 'varchi';
	$mysql_user = 'varchi';
	$mysql_password = '';
	
	if (!isset($_POST['search']) || !isset($_POST['cu']) || !isset($_POST['date'])){
		http_response_code(400);
		return;
	}
	$search = $_POST['search'];
	$cu = $_POST['cu'];
	$date = $_POST['date'];
	$imei = $_POST['imei'];
	
	/* TODO 
	 * Autenticazione
	 * Solo SECURITY!
	 */
	
	$con = mysqli_connect($mysql_host,$mysql_user,$mysql_password,$mysql_db);

	if (mysqli_connect_errno()) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
		http_response_code(500);
		return;
	} else {
	
		$sql = "SELECT * FROM `persone`,`date` WHERE persone.codiceUnivoco = date.cu AND   `codiceUnivoco` = '" . $_POST['search'] . "'"; 
		$result = mysqli_query($con,$sql);
		$size = mysqli_num_rows($result);
		
		if ($size == 1){
			$row = mysqli_fetch_array($result);

			$json->status = "found";
			$json->nome = $row['nome'];
			$json->cognome = $row['cognome'];
			$json->data = $row['datanascita'];
			
			
		} else {
			$json->status = "notfound";
		} 
			
		header('Content-Type: application/json');
		echo json_encode($json);
		mysqli_close($con);
	}
?>
