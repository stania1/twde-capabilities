package com.thoughtworks.ca.de.streaming

import com.typesafe.config.ConfigFactory
import org.apache.log4j.LogManager
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.Column
import scala.concurrent.duration._


object KafkaConsumer {
  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory.load
    val log = LogManager.getRootLogger
    val spark = SparkSession.builder
      .appName("StructuredNetworkWordCount")
      .master("local")
      .getOrCreate()
    val rawRecords: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "citybikes")
      .load()

//    {"==> merged_citibike_tripdata.csv <==": "503", "null": ["2017-07-01 06:39:39", "2017-07-01 06:48:03", "477", "W 41 St & 8 Ave", "40.75640548", "-73.9900262", "472", "E 32 St & Park Ave", "40.7457121", "-73.98194829", "21443", "Subscriber", "1990", "1"]}

    val citybikeSchema = StructType(Array(
      StructField("==> merged_citibike_tripdata.csv <==", StringType),
      StructField("null", ArrayType(
        StringType
      ))
    ))
//    val citybikeSchema = StructType(Array(
//      StructField("tripDuration", IntegerType),
//      StructField("startTime", TimestampType),
//      StructField("stopTime", TimestampType),
//      StructField("startStationId", IntegerType),
//      StructField("startStationName", StringType),
//      StructField("startStationLatitude", DoubleType),
//      StructField("endStationId", IntegerType),
//      StructField("endStationName", StringType),
//      StructField("endStationLatitude", DoubleType),
//      StructField("endStationLongitude", IntegerType),
//      StructField("bikeId", IntegerType),
//      StructField("userType", StringType),
//      StructField("birthYear", DateType),
//      StructField("gender", IntegerType)
//    ))
    val records = rawRecords.selectExpr("CAST(value AS STRING)")
      .select(from_json(col("value"), citybikeSchema).as("data"))
      .select("data.*")

    val stream = records.writeStream
      .format("console")
      .option("truncate", "true")
      .outputMode(OutputMode.Append())
      .trigger(Trigger.ProcessingTime(10.seconds))
      .foreach(new ForeachWriter[Row] {

        override def process(row: Row): Unit = {
          println(s"Processing ${row}")
        }

        override def close(errorOrNull: Throwable): Unit = {}

        override def open(partitionId: Long, version: Long): Boolean = {
          true
        }
      })
      .start()
      .awaitTermination()


    /*
    Writer code here to:
    1. read citybikes topic stream from kafka
    2. Convert kafka JSON payload to dataframe rows
    3. Convert start time and stop time columns to Timestamp
    4. Convert birth year column to Date
    5. Count number of rides per minute window
    6. Write to file system/hdfs in parquet format partitioned by minute window
     */

  }
}
