package com.ruleengine.driver

import org.apache.logging.log4j.{LogManager, Logger}
import com.ruleengine.executors.JsonRuleEngine

object RuleEngineDriver {
  
  val logger : Logger = LogManager.getLogger(RuleEngineDriver.this)
  
  def main(args:Array[String]) {
    logger.info("Starting Rules Engine")
    new JsonRuleEngine().executeJson()
  }
}