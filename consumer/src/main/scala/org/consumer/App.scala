package org.consumer
import org.apache.spark
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


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
    //spark.conf.set("spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation","true") -> Spark 2
    val schema = StructType(List(
      StructField("cid",IntegerType,true),
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
    val dfNintendo = df.filter(df("Publisher") === "Nintendo")
    //nitendoManagement(spark,dfNintendo)
    //employeeCreation(spark)
    //employeeManagement(spark)
    employeeView(spark)
    spark.stop()
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
    spark.sql("select * from emp.employee").show()
  }

  def nitendoManagement(spark: SparkSession,dfNintendo : DataFrame){
    val dfReadNintendo = spark.sql("select * from videogames.nintendo")
    dfReadNintendo.printSchema()
    dfReadNintendo.show(5)
  }

 }
