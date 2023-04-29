package org.consumer

import org.apache.spark.sql.SparkSession


/**
 * @author ${user.name}
 */
object App {

  
  def main(args : Array[String]) {
    println( "Hello World!" )
    val spark = SparkSession.builder().master("local[2]")
      .appName("Spark hive connection ")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("spark.sql.warehouse.dir","/users/hive/warehouse")
      .enableHiveSupport()
      .getOrCreate()
  }

 }
