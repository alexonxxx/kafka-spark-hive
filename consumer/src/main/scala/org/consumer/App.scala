package org.consumer

import org.apache.spark
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


object App {
  
  def main(args : Array[String]) {
    val spark = SparkSession.builder()
      .master("local[2]")
      .appName("Spark hive connection")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("spark.sql.warehouse.dir","/user/hive/warehouse")
      .enableHiveSupport()
      .getOrCreate()

    val schema = StructType(List(
      StructField("id",IntegerType,true),
      StructField("cname",StringType,true),
      StructField("platform", StringType, true),
      StructField("year",IntegerType,true),
      StructField("genre", StringType, true),
      StructField("publisher", StringType, true),
      StructField("naSales", FloatType, true),
      StructField("euSales", FloatType, true),
      StructField("jpSales", FloatType, true),
      StructField("otherSales", FloatType, true),
      StructField("globalSales", FloatType, true)
    ))
    val dfRead = spark.read.option("header",true).schema(schema).csv("vgsales.csv")
    val df= dfRead.drop("genre","globalSales")
    df.printSchema()
    nintendoManagement(spark,df)


  }

  def nintendoManagement(spark: SparkSession,df : DataFrame){
    val dfNintendo = df.filter(df("Publisher") === "Nintendo")
    val columnsToRename = Seq("nintendo.cid","nintendo.cname","nintendo.platform","nintendo.cyear","nintendo.publisher","nintendo.naSales", "nintendo.euSales","nintendo.jpSales","nintendo.otherSales")
    val dfNintendoFormatted = dfNintendo.toDF(columnsToRename:_*)
    //val dfNintendoFormatted = spark.createDataFrame(dfNintendo.rdd,schema=schema)
    dfNintendoFormatted.printSchema()
    dfNintendoFormatted.show(10)
    dfNintendoFormatted.write.mode(SaveMode.Append)
      //.option("path", "hdfs://localhost:8020/user/hive/warehouse")
      .saveAsTable("videogames.nintendo")
    val dfReadNintendo = spark.read.table("videogames.nintendo")
    dfReadNintendo.show(5)
    /*val dfReadNintendo = spark.read.
      format("jdbc").
      option("url", "jdbc:hive2://localhost:10000").
      option("dbtable", "videogames.nintendo").load()
    dfReadNintendo.show(5)*/
  }

 }
