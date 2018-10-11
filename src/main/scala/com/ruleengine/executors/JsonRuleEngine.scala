package com.ruleengine.executors

import org.apache.logging.log4j.{ LogManager, Logger }
import org.apache.spark.sql.SparkSession
import com.ruleengine.utils.RuleEngineUtils
import scala.io.Source

class JsonRuleEngine {

  val logger: Logger = LogManager.getLogger(JsonRuleEngine.this)

  def executeJson(): Unit = {

    val path = getClass.getResource("/Sample.json").getPath
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("Spark JSON Rules Engine")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._

    val raw_df = spark.read.json(path)
    println("raw data frame")
    raw_df.show()

    val flattened_df = RuleEngineUtils.flattenDataFrame(raw_df)
    println("flattened data frame")
    flattened_df.show()

    println("Applying Rule: Find colors with neat='wow'")
    flattened_df.createOrReplaceTempView("dataframe_view")
    val query = "select * from dataframe_view where data_item_props_neat='wow'"
    val queryDf = spark.sql(query)

    println("Post Rule Data Frame")
    queryDf.show()

  }
}