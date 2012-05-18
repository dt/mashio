package net.reisub.mashio

import play.api.libs.json._

package object api {
  private var enableDebug: Boolean = false
  def debugOn: Boolean = enableDebug
  def setDebug(on: Boolean) { enableDebug = on }
  def debug(msg: => String) { if (debugOn) println(msg) }

  private var enableInfo: Boolean = true
  def infoOn: Boolean = enableInfo
  def setInfo(on: Boolean) { enableInfo = on }
  def info(msg: => String) { if (infoOn) println(msg) }

  private var enableGathering: Boolean = false
  def gatheringOn: Boolean = enableGathering
  def setGathering(on: Boolean) { enableGathering = on }

  def error(msg: => String) { println(msg) }

  def getJsonString(json: JsValue): String = Json.stringify(json).replaceAll(""""([a-zA-Z0-9]+)":""", """"$1": """)
            .replaceAll(""","([a-zA-Z0-9]+)":""", """, "$1":""")
            .replaceAll(""""lastUpdated":\s+(\d+),""", """"lastUpdated": $1.0,""" )
}