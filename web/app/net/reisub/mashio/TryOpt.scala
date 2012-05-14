package net.reisub.mashio

object TryOpt {
  def apply[T](exceptions: Class[_ <: Exception]*)(f: => T): Option[T] = try {
    Option(f)
  } catch {
    case clazz if exceptions.exists(_.isAssignableFrom(clazz.getClass)) => None
  }
}