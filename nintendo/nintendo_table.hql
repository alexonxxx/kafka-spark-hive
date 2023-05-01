use videogames;
create external table if not exists nintendo(
  id int,
  name string,
  platform string,
  year int,
  publisher string,
  naSales float,
  euSales float,
  jpSales float,
  otherSales float
)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile location 'hdfs://namenode:8020/user/hive/warehouse/videogames.db/nintendo';