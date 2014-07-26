<?php
	include(__DIR__ . '/../src/query.php');
	
	if (!isset($_POST['json']) || !isset($_POST['cu']) || !isset($_POST['date'])){
		http_response_code(400);
		return;
	} 
	$data = $_POST['json'];
	$cu = $_POST['cu'];
	$date = $_POST['date'];
	$imei = $_POST['imei'];
		
	/* if (Auth con cu e date == false)
	 * 		http_response_code(401)
	 * else {....
	 */
	
	$json=json_decode($data);
	
	if (!property_exists($json,'update') || !isset($json->update)) {
		http_response_code(400);
	} else {
			
		foreach($json->update as $stat){
			$codiceUnivoco=$stat->cu;
			$codiceRistampa=$stat->reprint;
			$time=$stat->time;
			$operatore=$stat->operator;
			$slot=$stat->turn;
			$imei=$stat->imei;
			$tipo=$stat->type;
			$idVarco=$stat->gate;			
			
			if (insertStatistics($codiceUnivoco,
					$codiceRistampa,
					$time,
					$operatore,
					$slot,
					$imei,
					$tipo,
					$idVarco) == false) http_response_code(500);
		}
		
		http_response_code(200);
	}
?>
