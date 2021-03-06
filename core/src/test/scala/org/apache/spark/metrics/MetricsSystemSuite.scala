/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.metrics

import org.apache.spark.metrics.source.Source
import org.scalatest.{BeforeAndAfter, FunSuite, PrivateMethodTester}

import org.apache.spark.{SecurityManager, SparkConf}
import org.apache.spark.deploy.master.MasterSource

import scala.collection.mutable.ArrayBuffer


class MetricsSystemSuite extends FunSuite with BeforeAndAfter with PrivateMethodTester{
  var filePath: String = _
  var conf: SparkConf = null
  var securityMgr: SecurityManager = null

  before {
    filePath = getClass.getClassLoader.getResource("test_metrics_system.properties").getFile
    conf = new SparkConf(false).set("spark.metrics.conf", filePath)
    securityMgr = new SecurityManager(conf)
  }

  test("MetricsSystem with default config") {
    val metricsSystem = MetricsSystem.createMetricsSystem("default", conf, securityMgr)
    val sources = PrivateMethod[ArrayBuffer[Source]]('sources)
    val sinks = PrivateMethod[ArrayBuffer[Source]]('sinks)

    assert(metricsSystem.invokePrivate(sources()).length === 0)
    assert(metricsSystem.invokePrivate(sinks()).length === 0)
    assert(metricsSystem.getServletHandlers.nonEmpty)
  }

  test("MetricsSystem with sources add") {
    val metricsSystem = MetricsSystem.createMetricsSystem("test", conf, securityMgr)
    val sources = PrivateMethod[ArrayBuffer[Source]]('sources)
    val sinks = PrivateMethod[ArrayBuffer[Source]]('sinks)

    assert(metricsSystem.invokePrivate(sources()).length === 0)
    assert(metricsSystem.invokePrivate(sinks()).length === 1)
    assert(metricsSystem.getServletHandlers.nonEmpty)

    val source = new MasterSource(null)
    metricsSystem.registerSource(source)
    assert(metricsSystem.invokePrivate(sources()).length === 1)
  }
}
