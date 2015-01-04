package org.leaker

/**
 * This object is a wrapper around the MethodInstrumentationManager class because we need a class that is loaded by
 * the system class loader, not by the LeakerClassLoader, since this will be called from the application weaved
 * methods. If we were to call the MethodInstrumentationManager methods directly from the weaved classes, then a new
 * MethodInstrumentationManager class would be loaded in the sytem loader, which would be different than the one
 * loaded by LeakerClassLoader
 */
object MethodInstrumentationManagerProxy {

  val methodInstrumentationClass: Class[_] =
    LeakerAgent.leakerClassLoader.loadClass("org.leaker.MethodInstrumentationManager")

  def leakMethodCall(methodName: String, params: Array[AnyRef]): Unit = {
    methodInstrumentationClass.getMethod("leakMethodCall", classOf[String], classOf[Array[Object]]).invoke(null,
      methodName, params)
  }
}
