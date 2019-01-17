select `owncertificate`.`id` AS `id`,
`owncertificate`.`id_beltpp` AS `id_beltpp`,
`owncertificate`.`type` AS `type`,
`owncertificate`.`number` AS `number`,
`owncertificate`.`blanknumber` AS `blanknumber`,
`owncertificate`.`customername` AS `customername`,
`owncertificate`.`customeraddress` AS `customeraddress`,
`owncertificate`.`customerunp` AS `customerunp`,
`owncertificate`.`factoryaddress` AS `factoryaddress`,
`owncertificate`.`branches` AS `branches`,
`owncertificate`.`additionallists` AS `additionallists`,
`owncertificate`.`datestart` AS `datestart`,
`owncertificate`.`dateexpire` AS `dateexpire`,
`owncertificate`.`expert` AS `expert`,
`owncertificate`.`signer` AS `signer`,
`owncertificate`.`signerjob` AS `signerjob`,
`owncertificate`.`datecert` AS `datecert`, 
`owncertificate`.`dateload` AS `dateload`,
`beltpp`.`name` AS `beltppname`,
`beltpp`.`address` AS `beltppaddress`,
`beltpp`.`unp` AS `beltppunp`
from 
(`owncertificate` left join `beltpp` 
     on ((`owncertificate`.`id_beltpp` = `beltpp`.`id`))
)
