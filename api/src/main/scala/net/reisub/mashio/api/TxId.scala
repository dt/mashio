package net.reisub.mashio.api

import scala.util.Random

object TxId {
  private def rand = Random.nextInt(9999)
  def generate: String = "deadbeef-%04d-%04d-%04d-c0ffeec0ffee".format(rand, rand, rand)
  def isInternal(id: String): Boolean = id.startsWith("deadbeef") && id.endsWith("c0ffeec0ffee")
}

