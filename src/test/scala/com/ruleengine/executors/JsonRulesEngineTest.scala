package com.ruleengine.executors

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.scalatest.FunSpec
import com.github.mrpowers.spark.fast.tests.DataFrameComparer

class JsonRulesEngineTest extends FunSpec with DataFrameComparer
    with SparkSessionTestWrapper {

  import spark.implicits._

  it("applies rule on sample json backed dataframe") {

    val actualDF = new JsonRuleEngine().executeJson()

    val expectedSchema = List(
      StructField("data_colors_color", StringType, true),
      StructField("data_colors_hex", StringType, true),
      StructField("data_item_props_neat", StringType, true))

    val expectedData = Seq(
      Row("red", "ff0000", "wow"),
      Row("blue", "0000ff", "wow"))

    val expectedDF = spark.createDataFrame(
      spark.sparkContext.parallelize(expectedData),
      StructType(expectedSchema))

    assert(actualDF.count() > 0 && actualDF.count() == expectedDF.count())
    assert(actualDF.except(expectedDF).toDF().count() == 0)
  }
}