package org.consumer
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.Trigger
object KafkaConnection {

  def readJson(spark: SparkSession,schema: StructType): DataFrame ={
    val inputDf = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "127.0.0.1:9092")
    .option("subscribe", "test")
    .load()
    inputDf.selectExpr("CAST(value AS STRING)")
    .select(from_json(col("value"), schema).as("data"))
    .select("data.*")
  }

  def writeConsole(df: DataFrame,trigger: String = "1 minute"): Unit ={
    val query = df
      .writeStream
      .outputMode("complete")
      .trigger(Trigger.ProcessingTime(trigger))
      .format("console")
      .start()
    query.awaitTermination()
  }

  def writeBatchConsole( batchDF:DataFrame, batchID:Long ) : Unit = {
    batchDF.persist()
    batchDF.write.mode(SaveMode.Overwrite).format("console")
      .save()
    batchDF.unpersist()
  }

  def writeNintendo( batchDF:DataFrame, batchID:Long ) : Unit = {
    batchDF.persist()
    batchDF.write.mode(SaveMode.Overwrite).format("hive")
      .insertInto("videogames.groupedNintendoSales")
    batchDF.unpersist()
  }

  def writeHive(df: DataFrame,trigger: String = "1 minute"): Unit ={
    val query = df
      .writeStream
      .outputMode("complete")
      .trigger(Trigger.ProcessingTime(trigger))
      .foreachBatch(writeNintendo _)
      .start()
    query.awaitTermination()
  }

}
