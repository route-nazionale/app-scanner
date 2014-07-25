<?php
	function make_connession()
	{
		$dbhost='172.16.10.151'; 
		$dbusername='varchi';
		$dbpassword='';
		$dbname='varchi';
		
		
		$connessione = mysql_connect($dbhost,$dbusername,$dbpassword)or die("Connessione non riuscita: " . mysql_error());
		mysql_select_db($dbname, $connessione) or die("Errore nella selezione del database");
		return $connessione;
	}
	
	function make_query($connessione,$query_string){
		$query = mysql_query($query_string,$connessione);
		if ($query == FALSE) {
			error_log("Errore nella composizione della query " . $query_string);
			return false;
		}
		mysql_close($connessione);
		return $query;
	}
	
	function safe($value){ 
		return mysql_real_escape_string($value); 
	}
	
	function insertStatistics($idScansione,$codiceUnivoco,$codiceRistampa,$time,$operatore,$slot,$imei,$tipo,$idEvento)
	{
		
		// TODO Devo controllare la coppia Timestamp+IMEI onde evitare doppioni
		
		$connessione=make_connession();
		$idScansione=safe($idScansione);
		$codiceUnivoco=safe($codiceUnivoco);
		$codiceRistampa=safe($codiceRistampa);
		$time=safe($time);
		$operatore=safe($operatore);
		$slot=safe($slot);
		$imei=safe($imei);
		$tipo=safe($tipo);
		$idEvento=safe($idEvento);
		$query_string="INSERT INTO statistics (idScansione,codiceUnivoco,codiceRistampa,time,operatore,slot,imei,tipo,idEvento) ". 
		"VALUES ('".$idScansione
			."','".$codiceUnivoco
			 ."','".$codiceRistampa
			 ."','".$time
			 ."','".$operatore
			 ."','".$slot
			 ."','".$imei
			 ."','".$tipo
			 ."','".$idEvento
			 ."')";
		return make_query($connessione,$query_string);
	}
?>
