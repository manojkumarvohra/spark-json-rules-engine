package com.ruleengine.utils

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.Column
import org.apache.spark.sql.types.MetadataBuilder
import org.apache.spark.sql.types.ArrayType
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object RuleEngineUtils {
  
  val levelDelimiter: String = "_"

  def flattenDataFrame(df: DataFrame): DataFrame = {

    var flattenedDf: DataFrame = df

    if (isNested(df)) {
      val flattenedSchema: Array[(Column, Boolean)] = flattenSchema(df.schema)
      var simpleColumns: List[Column] = List.empty[Column]
      var complexColumns: List[Column] = List.empty[Column]

      flattenedSchema.foreach {
        case (col, isComplex) => {
          if (isComplex) {
            complexColumns = complexColumns :+ col
          } else {
            simpleColumns = simpleColumns :+ col
          }
        }
      }

      var crossJoinedDataFrame = df.select(simpleColumns: _*)
      complexColumns.foreach(col => {
        crossJoinedDataFrame = crossJoinedDataFrame.crossJoin(flattenDataFrame(df.select(col)))
      })
      crossJoinedDataFrame
    } else {
      flattenedDf
    }
  }

  private def flattenSchema(schema: StructType, prefix: String = null): Array[(Column, Boolean)] = {

    schema.fields.flatMap(field => {
      val columnName = if (prefix == null) field.name else prefix + "." + field.name
      field.dataType match {
        case arrayType: ArrayType => {
          val cols: Array[(Column, Boolean)] = Array[(Column, Boolean)](((explode_outer(col(columnName)).as(columnName.replace(".", levelDelimiter))), true))
          cols
        }
        case structType: StructType => {
          flattenSchema(structType, columnName)
        }
        case _ => {
          val columnNameWithUnderscores = columnName.replace(".", levelDelimiter)
          val metadata = new MetadataBuilder().putString("encoding", "ZSTD").build()
          Array(((col(columnName).as(columnNameWithUnderscores, metadata)), false))
        }
      }
    }).filter(field => field != None)
  }

  def isNested(df: DataFrame): Boolean = {
    var foundNestedType = false;
    df.schema.fields.foreach(field => {
      field.dataType match {
        case arrayType: ArrayType => {
          return true
        }
        case structType: StructType => {
          return true
        }
        case _ => {
          foundNestedType = false
        }
      }
    })
    foundNestedType
  }
}