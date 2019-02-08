


select cert_id, datacert, otd_name, nomercert, otd_id, kontrp, nblanka, issuedate, koldoplist, parentnumber 
from cert_view 
where cert_id in  (select  a.cert_id  from 
(SELECT cert_id FROM (select cert_id from cert_view  where  OTD_ID = 1  ORDER by issuedate asc, cert_id asc) where rownum <= :highposition ) a 
left join 
(SELECT cert_id FROM (select cert_id from cert_view  where  OTD_ID = 1  ORDER by issuedate asc, cert_id asc) where rownum <= :lowposition ) b 
on a.cert_id = b.cert_id where b.cert_id is null) 
ORDER by issuedate asc, cert_id asc