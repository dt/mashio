package net.reisub.mashio

import play.api.libs.json._

package object api {
  val ContentServer = "http://localhost:9000"
  val StreamServer = ContentServer

  def getJsonString(json: JsValue): String = Json.stringify(json).replaceAll(""""([a-zA-Z0-9]+)":""", """"$1": """)
            .replaceAll(""","([a-zA-Z0-9]+)":""", """, "$1":""")
            .replaceAll(""""lastUpdated":\s+(\d+),""", """"lastUpdated": $1.0,""" )

  object Colors {
    def web       = make(Console.BLUE) _
    val unhandled = make(Console.CYAN) _
    val good      = make(Console.GREEN) _
    val warning   = make(Console.YELLOW) _
    val error     = make(Console.RED) _

    def make(color: String)(str: String) = color + str + Console.WHITE
  }
}