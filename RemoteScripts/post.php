<?
	include('query.php');
	
	if (!isset($_POST['data']) || !isset($_POST['cu']) || !isset($_POST['date'])){
		http_response_code(400);
		return;
	} 
	$data = $_POST['data'];
	$cu = $_POST['cu'];
	$date = $_POST['date'];
	
	/* if (Auth con cu e date == false)
	 * 		http_response_code(401)
	 * else {....
	 */
	
	$json=json_decode($data);
	
	if (!property_exists($json,'update') || !isset($obj->update)) {
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
