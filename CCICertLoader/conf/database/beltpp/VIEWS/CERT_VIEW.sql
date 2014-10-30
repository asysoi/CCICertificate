--------------------------------------------------------
--  DDL for View CERT_VIEW
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "CERT_VIEW" ("CERT_ID", "FORMS", "UNN", "KONTRP", "KONTRS", "ADRESS", "POLUCHAT", "ADRESSPOL", "DATACERT", "ISSUEDATE", "NOMERCERT", "EXPERT", "NBLANKA", "RUKOVOD", "TRANSPORT", "MARSHRUT", "OTMETKA", "STRANAV", "STRANAPR", "STATUS", "KOLDOPLIST", "FLEXP", "UNNEXP", "EXPP", "EXPS", "EXPADRESS", "FLIMP", "IMPORTER", "ADRESSIMP", "FLSEZ", "SEZ", "FLSEZREZ", "STRANAP", "OTD_ID", "PARENTNUMBER", "PARENTSTATUS", "DENORM", "PARENT_ID", "OTD_NAME", "NAME", "OTD_ADDRESS_INDEX", "ADDR_CITY", "ADDR_LINE", "ADDR_BUILDING") AS 
  select a."CERT_ID",a."FORMS",a."UNN",a."KONTRP",a."KONTRS",a."ADRESS",a."POLUCHAT",a."ADRESSPOL",a."DATACERT",a."ISSUEDATE",a."NOMERCERT",a."EXPERT",a."NBLANKA",a."RUKOVOD",a."TRANSPORT",a."MARSHRUT",a."OTMETKA",a."STRANAV",a."STRANAPR",a."STATUS",a."KOLDOPLIST",a."FLEXP",a."UNNEXP",a."EXPP",a."EXPS",a."EXPADRESS",a."FLIMP",a."IMPORTER",a."ADRESSIMP",a."FLSEZ",a."SEZ",a."FLSEZREZ",a."STRANAP",a."OTD_ID",a."PARENTNUMBER",a."PARENTSTATUS",a."DENORM", a."PARENT_ID", B.NAME otd_name, B.NAME, B.ADDR_INDEX otd_address_index, B.ADDR_CITY, B.ADDR_LINE, B.ADDR_BUILDING from CERT_PARENT_VIEW a left join C_OTD b on A.OTD_ID = B.ID;