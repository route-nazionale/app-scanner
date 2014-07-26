<?php

	include(__DIR__ . '/../config/params.php');
	
	$sqlite_filename = __DIR__ . '/../data/rn2014.db';
	$gzipped_filename = __DIR__ . '/../web/rn2014.db.gz';
	$mysql_host = MYSQL_HOST;
	$mysql_db = MYSQL_DB_VARCHI;
	$mysql_user = MYSQL_USER_VARCHI;
	$mysql_password = MYSQL_PASS_VARCHI;
	
	
	if (file_exists($sqlite_filename)) { 
		// Se esiste il db sqlite lo cancello
		unlink($sqlite_filename);
	}
	
	set_time_limit(0);
	
	// creo il file db sqlite
	$sq = new SQLite3($sqlite_filename);
	$con = mysqli_connect($mysql_host,$mysql_user,$mysql_password,$mysql_db);
	
	// Controllo la connessione mysql
	if (mysqli_connect_errno()) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	} else {
	
		// creo la tabella persone in sqlite 
		$sql = "CREATE TABLE IF NOT EXISTS `persone` (  `codiceUnivoco` varchar(30) NOT NULL,  `ristampaBadge` decimal(10,0) DEFAULT NULL,  `nome` varchar(50) DEFAULT NULL,  `cognome` varchar(50) DEFAULT NULL,  `idGruppo` varchar(10) DEFAULT NULL,  `codiceAgesci` varchar(10) DEFAULT NULL,  `idUnita` varchar(5) DEFAULT NULL,  `quartiere` int(11) DEFAULT NULL,  `contrada` int(11) DEFAULT NULL,  PRIMARY KEY (`codiceUnivoco`))" ;
		$sq->query($sql);
		
		$result = mysqli_query($con,"SELECT * FROM persone");
		while($row = mysqli_fetch_array($result)) {		
			
			$sql = "INSERT INTO `persone`(`codiceUnivoco`, `ristampaBadge`, `nome`, `cognome`, `idGruppo`, `codiceAgesci`, `idUnita`, `quartiere`, `contrada`) VALUES ('" . $row['codiceUnivoco']  . "', '" . $row['ristampaBadge']  . "', '***', '***', '" . $row['idGruppo'] . "', '" . $row['codiceAgesci']  . "', '" . $row['idUnita'] . "', '" .  $row['quartiere']  . "', '" . $row['contrada'] . "')" ;
			$sq->query($sql);

			// echo $row['codiceUnivoco'] ;
			// echo $row['ristampaBadge'] ;
			// echo $row['nome'] ;
			// echo $row['cognome'] ;
			// echo $row['idGruppo'] ;
			// echo $row['codiceAgesci'] ;
			// echo $row['idUnita'] ;
			// echo $row['quartiere']; 
			// echo $row['contrada'];
			// echo "<br>";
		}
		
		// creo la tabella eventi in sqlite 
		$sql = "CREATE TABLE IF NOT EXISTS `eventi` (  `idEvento` varchar(20) NOT NULL,  `nome` varchar(100) DEFAULT NULL,  `codiceStampa` varchar(20) NOT NULL,  `stradaCoraggio` INTEGER DEFAULT NULL,  `quartiere` INTEGER DEFAULT NULL,  `contrade` INTEGER DEFAULT NULL,  `tipoEvento` varchar(3) DEFAULT NULL,  PRIMARY KEY (`idEvento`)) " ;
		$sq->query($sql);
		
		$result = mysqli_query($con,"SELECT * FROM eventi");
		while($row = mysqli_fetch_array($result)) {		
			
			$sql = "INSERT INTO `eventi`(`idEvento`, `nome`, `codiceStampa`, `stradaCoraggio`, `quartiere`, `contrade`, `tipoEvento`) VALUES ('" . $row['idEvento']  . "', '" . SQLite3::escapeString($row['nome'])  . "', '" . $row['codiceStampa'] . "', '" . $row['stradaCoraggio']  . "', '" . $row['quartiere'] . "', '" .  $row['contrade']  . "', '" . $row['tipoEvento'] . "')" ;			
			$sq->query($sql);

		}		
		
		// creo la tabella gruppi in sqlite
		$sql = "CREATE TABLE IF NOT EXISTS `gruppi` (  `idGruppo` varchar(20) NOT NULL,  `idUnita` varchar(5) DEFAULT NULL,  `nome` varchar(50) NOT NULL) " ;
		$sq->query($sql);
		
		$result = mysqli_query($con,"SELECT * FROM gruppi");
		while($row = mysqli_fetch_array($result)) {
				
			$sql = "INSERT INTO `gruppi`(`idGruppo`, `nome`, `idUnita`) VALUES ('" . $row['idGruppo']  . "', '" . SQLite3::escapeString($row['nome'])  . "', '" . $row['idUnita'] . "')" ;
			$sq->query($sql);
		}
		
		// creo la tabella assegnamenti in sqlite 
		$sql = "CREATE TABLE IF NOT EXISTS `assegnamenti` ( `idAssegnamenti` INTEGER PRIMARY KEY AUTOINCREMENT, `codiceUnivoco` varchar(30) DEFAULT NULL, `idEvento` varchar(20) DEFAULT NULL, `slot` decimal(10,0) DEFAULT NULL, `staffEvento` tinyint(1) DEFAULT '0')";
		$sq->query($sql);
		
		$result = mysqli_query($con,"SELECT * FROM assegnamenti");
		while($row = mysqli_fetch_array($result)) {	
			$sql = "INSERT INTO `assegnamenti` ( `codiceUnivoco`, `idEvento`, `slot`, `staffEvento` ) VALUES ('" . $row['codiceUnivoco']  . "', '" . $row['idEvento'] . "', '" . $row['slot']  . "', '" . $row['staffEvento'] . "')" ;			
			$sq->query($sql);

		}
		echo "DB generato correttamente";
		mysqli_close($con);
		$sq->close();
		
		// Comprime l'archivio
		$gizipped = gzopen($gzipped_filename, 'w9');
		gzwrite ($gizipped, file_get_contents($sqlite_filename));
		gzclose ($gizipped);
	}
?>
