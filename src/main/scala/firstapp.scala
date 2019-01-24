
package firstapp

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils, LocationStrategies}
import org.apache.kafka.clients.consumer.ConsumerRecord

object StockDataStreaming {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("StockApp")
    val HIGH_STREAMING_INTERVAL: Int = 60
    val mappingTopicsSet: Set[String] = List("stock_timeseries").toSet
    val sparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    val ssc = new StreamingContext(sparkSession.sparkContext, Seconds(HIGH_STREAMING_INTERVAL))
    val KafkaParams: Map[String, Object] = Map(
      "bootstrap.servers" -> "localhost:9092"
    )

    val messages: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferBrokers,
      Subscribe[String, String](mappingTopicsSet, KafkaParams)
    )
    val jsonRDD = messages.map(_.value())
    jsonRDD.map(rdd => {
      rdd.map(row => {
        print(row)
      })
    })
  }
}