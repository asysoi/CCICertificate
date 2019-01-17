CREATE TABLE `certview` (
	`id` INT(11) NOT NULL,
	`id_beltpp` INT(11) NOT NULL,
	`type` VARCHAR(10) NOT NULL COLLATE 'utf8_general_ci',
	`number` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`blanknumber` VARCHAR(10) NOT NULL COLLATE 'utf8_general_ci',
	`customername` VARCHAR(250) NOT NULL COLLATE 'utf8_general_ci',
	`customeraddress` VARCHAR(250) NOT NULL COLLATE 'utf8_general_ci',
	`customerunp` VARCHAR(25) NOT NULL COLLATE 'utf8_general_ci',
	`factoryaddress` VARCHAR(250) NOT NULL COLLATE 'utf8_general_ci',
	`branches` VARCHAR(1000) NOT NULL COLLATE 'utf8_general_ci',
	`additionallists` VARCHAR(3) NOT NULL COLLATE 'utf8_general_ci',
	`datestart` DATE NOT NULL,
	`dateexpire` DATE NOT NULL,
	`expert` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`signer` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`signerjob` VARCHAR(120) NOT NULL COLLATE 'utf8_general_ci',
	`datecert` DATE NOT NULL,
	`dateload` DATE NOT NULL,
	`beltppname` VARCHAR(250) NULL COLLATE 'utf8_general_ci',
	`beltppaddress` VARCHAR(250) NULL COLLATE 'utf8_general_ci',
	`beltppunp` VARCHAR(25) NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;


-- Дамп структуры для таблица owncert.owncertificate
CREATE TABLE IF NOT EXISTS `owncertificate` (
  `id` int(11) NOT NULL,
  `id_beltpp` int(11) NOT NULL,
  `number` varchar(50) NOT NULL,
  `blanknumber` varchar(10) NOT NULL,
  `type` varchar(10) NOT NULL,
  `customername` varchar(250) NOT NULL,
  `customeraddress` varchar(250) NOT NULL,
  `customerunp` varchar(25) NOT NULL,
  `factoryaddress` varchar(250) NOT NULL,
  `branches` varchar(1000) NOT NULL,
  `additionallists` varchar(3) NOT NULL,
  `datecert` date NOT NULL,
  `dateexpire` date NOT NULL,
  `datesign` date NOT NULL,
  `datestart` date NOT NULL,
  `dateload` date NOT NULL,
  `expert` varchar(50) NOT NULL,
  `signer` varchar(50) NOT NULL,
  `signerjob` varchar(120) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ind_ownc_number` (`number`,`blanknumber`),
  KEY `FK_owncertificate_beltpp` (`id_beltpp`),
  CONSTRAINT `FK_owncertificate_beltpp` FOREIGN KEY (`id_beltpp`) REFERENCES `beltpp` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица owncert.ownproduct
CREATE TABLE IF NOT EXISTS `ownproduct` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_certificate` int(11) NOT NULL,
  `number` varchar(5) NOT NULL,
  `name` varchar(120) NOT NULL,
  `code` varchar(25) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ind_ownpr_name` (`name`),
  KEY `FK__owncertificate` (`id_certificate`),
  CONSTRAINT `FK__owncertificate` FOREIGN KEY (`id_certificate`) REFERENCES `owncertificate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.


-- Дамп структуры для представление owncert.certview
-- Удаление временной таблицы и создание окончательной структуры представления
DROP TABLE IF EXISTS `certview`;
CREATE ALGORITHM=UNDEFINED DEFINER=`cert`@`localhost` SQL SECURITY DEFINER 
VIEW `certview` AS select `owncertificate`.`id` AS `id`,`owncertificate`.`id_beltpp` AS `id_beltpp`,
`owncertificate`.`type` AS `type`,`owncertificate`.`number` AS `number`,
`owncertificate`.`blanknumber` AS `blanknumber`,`owncertificate`.`customername` AS `customername`,
`owncertificate`.`customeraddress` AS `customeraddress`,`owncertificate`.`customerunp` AS `customerunp`,
`owncertificate`.`factoryaddress` AS `factoryaddress`,`owncertificate`.`branches` AS `branches`,
`owncertificate`.`additionallists` AS `additionallists`,`owncertificate`.`datestart` AS `datestart`,
`owncertificate`.`dateexpire` AS `dateexpire`,`owncertificate`.`expert` AS `expert`,
`owncertificate`.`signer` AS `signer`,`owncertificate`.`signerjob` AS `signerjob`,
`owncertificate`.`datecert` AS `datecert`,`owncertificate`.`dateload` AS `dateload`,
`beltpp`.`name` AS `beltppname`,`beltpp`.`address` AS `beltppaddress`,`beltpp`.`unp` AS `beltppunp` 
from (`owncertificate` left join `beltpp` on((`owncertificate`.`id_beltpp` = `beltpp`.`id`)));