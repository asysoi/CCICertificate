select tt."CERT_ID", bb.CERT_ID parent_id, tt.parentnumber from c_cert tt left join c_cert bb on tt.parentnumber = bb.nomercert where tt.parentnumber is not null;

select CERT_ID, parent_id, parentnumber from c_cert;

alter table c_cert ADD parent_id integer;

alter table c_cert ADD OTD_NAME varchar(255);
alter table c_cert ADD OTD_ADDR_INDEX varchar(255);
alter table c_cert ADD OTD_ADDR_CITY varchar(255); 
alter table c_cert ADD OTD_ADDR_LINE varchar(255);
alter table c_cert ADD OTD_ADDR_BUILDING varchar(255);
alter table c_cert ADD EOTD_NAME varchar(255);
alter table c_cert ADD EOTD_ADDR_CITY varchar(255);
alter table c_cert ADD EOTD_ADDR_LINE varchar(255);

update c_cert aa set 
parent_id = (select cert_id from c_cert bb where bb.nomercert = aa.parentnumber) 
where aa.parentnumber is not NULL and LENGTH(TRIM(parentnumber)) > 0;


update c_cert aa  set 
 OTD_NAME = (select OTD_NAME from c_otd where c_otd.id = aa.otd_id),
 OTD_ADDR_INDEX = (select OTD_ADDR_INDEX from c_otd where c_otd.id = aa.otd_id),
 OTD_ADDR_CITY = (select OTD_ADDR_CITY from c_otd where c_otd.id = aa.otd_id), 
 OTD_ADDR_LINE = (select OTD_ADDR_LINE from c_otd where c_otd.id = aa.otd_id),
 OTD_ADDR_BUILDING = (select OTD_ADDR_BUILDING from c_otd where c_otd.id = aa.otd_id),
 EOTD_NAME = (select EOTD_NAME from c_otd where c_otd.id = aa.otd_id),
 EOTD_ADDR_CITY = (select EOTD_ADDR_CITY from c_otd where c_otd.id = aa.otd_id),
 EOTD_ADDR_LINE = (select EOTD_ADDR_LINE from c_otd where c_otd.id = aa.otd_id);
 

CREATE OR REPLACE VIEW CERT_VIEW
AS select a."CERT_ID",a."FORMS",a."UNN",a."KONTRP",a."KONTRS",a."ADRESS",a."POLUCHAT",a."ADRESSPOL",
a."DATACERT",a."NOMERCERT",a."EXPERT",a."NBLANKA",a."RUKOVOD",a."TRANSPORT",a."MARSHRUT",a."OTMETKA",
a."STRANAV",a."STRANAPR",a."STATUS",a."FLEXP",a."UNNEXP",a."EXPP",a."EXPS",a."EXPADRESS",a."FLIMP",
a."IMPORTER",a."ADRESSIMP",a."FLSEZ",a."SEZ",a."FLSEZREZ",a."STRANAP",a."OTD_ID",a."PARENTNUMBER",
a."PARENTSTATUS",a."ISSUEDATE",a."CODESTRANAV",a."CODESTRANAPR",a."CODESTRANAP",a."CATEGORY",
a."KOLDOPLIST",a."PARENT_ID",a."OTD_NAME",a."OTD_ADDR_INDEX",a."OTD_ADDR_CITY",a."OTD_ADDR_LINE",
a."OTD_ADDR_BUILDING",a."EOTD_NAME",a."EOTD_ADDR_CITY",a."EOTD_ADDR_LINE", 
decode(a."EXPP", null, a."KONTRP", a."EXPP") as exporter, decode(a."IMPORTER", null, a."POLUCHAT", a."IMPORTER") as importerfull 
from c_cert a;


CREATE OR REPLACE VIEW CERT_REPORT
AS select file_in_id, aa.cert_id as cert_id, bb.nomercert, bb.nblanka,
bb.datacert, bb.issuedate, bb.expert, aa.DATE_LOAD, bb.otd_id, bb.otd_name 
from C_FILES_IN aa inner join C_CERT bb on bb.cert_id = aa.cert_id;

===========================================================================
' Создаем CTXCAT индекс на таблице c_product для ускорения поиска
привлкательность этого индекса в том, что он транзакционный
В отличии от CONTEXT индекса не требуется запускать 
exec CTX_DDL.SYNC_INDEX('INDX_CPRODUCT_CONTEXT');
exec CTX_DDL.SYNC_INDEX('INDX_CPRODUCT_DENORM_CTX');
после снесение изменения в таблицу.
Но он применим только к простым текстовым полям. 
' 
CREATE INDEX INDX_CPRODUCT_CTXCAT ON C_PRODUCT (TOVAR) INDEXTYPE IS CTXSYS.CTXCAT PARALLEL;

' Этот индекс невозможен, так как поле типа clob не индексируется с типом CTXCAT. Для CLOB возможен только CONTEXT'
' CREATE INDEX INDX_CPRODUCT_DENORM_CTXCAT ON C_PRODUCTDENORM (TOVAR) INDEXTYPE IS CTXSYS.CTXCAT PARALLEL;'

alter table c_product_denorm INMEMORY INMEMORY(CERT_ID) NO INMEMORY(tovar);  

alter table c_cert INMEMORY;  


===========================================================================
select  cert_id, datacert, otd_name, nomercert, otd_id, kontrp, nblanka, issuedate, koldoplist, parentnumber 
from cert_view where cert_id in  
(select  a.cert_id  from 
  (SELECT cert_id FROM (select cert_id from cert_view  where  UPPER(NOMERCERT) like '%47%'  AND  UPPER(MARSHRUT) like '%BY SEA%' ORDER by issuedate asc, cert_id asc) where rownum <= :highposition ) a 
left join 
  (SELECT cert_id FROM (select cert_id from cert_view  where  UPPER(NOMERCERT) like '%47%'  AND  UPPER(MARSHRUT) like '%BY SEA%' ORDER by issuedate asc, cert_id asc) where rownum <= :lowposition ) b 
 on a.cert_id = b.cert_id where b.cert_id is null) 
ORDER by issuedate asc, cert_id asc;


 select cert_id, datacert, otd_name, nomercert, otd_id, kontrp, nblanka, issuedate, koldoplist, parentnumber 
from cert_view where cert_id in 
 (select a.cert_id from 
   (SELECT cert_id FROM (select cert_id from cert_view  where cert_id in ( SELECT cert_id FROM C_Product_Denorm where contains(TOVAR, :tovar) > 0 )  ORDER by issuedate desc, cert_id desc) 
   where rownum <= :highposition ) a 
  left join 
   (SELECT cert_id FROM (select cert_id from cert_view  where cert_id in (SELECT cert_id FROM C_Product_Denorm where  contains(TOVAR, :tovar) > 0 )  ORDER by issuedate desc, cert_id desc)  
   where rownum <= :lowposition ) b 
  on a.cert_id = b.cert_id where b.cert_id is null) 
ORDER by issuedate desc, cert_id desc;

select * from table(dbms_xplan.display_cursor()); 

select * from v$sql where sql_text like 'select cert_id%' and sql_text like '%desc%'  and sql_text like '%трактор%' ;

select *  from v$sql_plan where sql_id='9js3u3bqw2ywg';

select * from v$sql_plan_MONITOR ;

============================================================================================
MYSQL
проблема UTF-8 в MySQL 
ALTER TABLE ownproductdenorm  MODIFY name VARCHAR(15000) CHARACTER SET utf8mb4;
ALTER TABLE ownproductdenorm CONVERT TO CHARACTER SET utf8mb4;
SHOW VARIABLES WHERE Variable_name LIKE 'character\_set\_%' OR Variable_name LIKE 'collation%';

CREATE TABLE `ownproductdenorm` (
	`idd` INT(11) NOT NULL AUTO_INCREMENT,
	`id` INT(11) NOT NULL,
	`id_certificate` INT(11) NOT NULL,
	`code` VARCHAR(250) NULL DEFAULT NULL,
	`nncode` BIGINT(20) NULL DEFAULT NULL,
	INDEX `id_ind` (`idd`),
	INDEX `ind_nncode` (`nncode`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=3891633
;

