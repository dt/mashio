package net.reisub.mashio.models
case class RdioData[T](key: RdioKey[T], url: String, isFake: Boolean = false, canStream: Boolean = false)