use videogames;
create table if not exists nintendo(
  cid int,
  cname string,
  platform string,
  cyear int,
  publisher string,
  naSales float,
  euSales float,
  jpSales float,
  otherSales float
)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as PARQUET';