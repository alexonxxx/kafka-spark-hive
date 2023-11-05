package org.consumer
import org.apache.spark
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructField, StructType,TimestampType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{sum}

object App {
  final var TRIFTH_URI="thrift://localhost:9083"
  final var WAREHOUSE="/opt/hive/data/warehouse"
  def main(args : Array[String]) {
    val spark = SparkSession.builder()
      .master("local[2]")
      .appName("Spark hive connection")
      .config("hive.metastore.uris", TRIFTH_URI)
      .config("spark.sql.warehouse.dir", WAREHOUSE)
      .enableHiveSupport()
      .getOrCreate()
    val schema = StructType(List(
      StructField("cid",IntegerType,true),
      StructField("cname",StringType,true),
      StructField("platform", StringType, true),
      StructField("cyear",IntegerType,true),
      StructField("genre", StringType, true),
      StructField("publisher", StringType, true),
      StructField("naSales", FloatType, true),
      StructField("euSales", FloatType, true),
      StructField("jpSales", FloatType, true),
      StructField("otherSales", FloatType, true),
      StructField("globalSales", FloatType, true),
      StructField("timestamp", TimestampType, true)
    ))
    //val dfRead = spark.read.option("header",true).schema(schema).csv("vgsales.csv")
    val dfRead = KafkaConnection.readJson(spark,schema)
    val dfNintendoSales = nintendoSalesTransform(dfRead)
    KafkaConnection.writeHive(dfNintendoSales,"30 seconds")
    //val df= dfRead.drop("genre","globalSales")
    //df.printSchema()
    //val dfNintendo = df.filter(df("Publisher") === "Nintendo")
    //nintendoWrite(spark,dfNintendo)
    //employeeCreation(spark)
    //employeeManagement(spark)
    //employeeView(spark)
    //spark.stop()
  }

  def employeeCreation(spark: SparkSession): Unit = {
    import spark.implicits._
    spark.sql("CREATE DATABASE IF NOT EXISTS emp")
    val empData = Seq((1, "James",30,"M"),
      (2, "Ann",40,"F"), (3, "Jeff",41,"M"),
      (4, "Jennifer",20,"F")
    );
    val rddEmp = spark.sparkContext.parallelize(empData)
    val sampleDF = rddEmp.toDF("id", "name","age","gender")
    sampleDF.write.mode(SaveMode.Append)
      .saveAsTable("emp.employee")
  }
  def employeeManagement(spark: SparkSession): Unit ={
    import spark.implicits._

    val empNewData = Seq((5, "Alex",30,"M"),
      (6, "Joan",25,"F"), (7, "Claudia",38,"M"),
      (8, "Judith",20,"F")
    );
    val rddEmp = spark.sparkContext.parallelize(empNewData)
    val sampleNewDF = rddEmp.toDF("id", "name","age","gender")
    sampleNewDF.write.mode(SaveMode.Append)
      .saveAsTable("emp.employee")
  }

  def employeeView(spark: SparkSession): Unit ={
    spark.sql("select * from emp." +
      "").show()
  }

  def nintendoWrite(spark: SparkSession,df : DataFrame){
    df.write.mode(SaveMode.Append).format("hive")
      .insertInto("videogames.nintendoSales")
    val dfReadNintendo = spark.sql("select * from videogames.nintendoSales")
    dfReadNintendo.printSchema()
    dfReadNintendo.show(5)
  }

  def nintendoSalesFilter(spark: SparkSession,df : DataFrame){
    df.write.mode(SaveMode.Append).format("hive")
      .saveAsTable("videogames.nintendoSales")
    val dfReadNintendo = spark.sql("select * from videogames.nintendoSales")
    dfReadNintendo.printSchema()
    dfReadNintendo.show(5)
  }

  def nintendoSalesTransform(df: DataFrame): DataFrame = {
    df.withWatermark("timestamp", "1 minute")
    .groupBy("cname").agg(sum("naSales").as("naSales"), sum("euSales").as("euSales"), sum("jpSales").as("jpSales"), sum("otherSales").as("otherSales"))
  }

 }
