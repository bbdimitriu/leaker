package org.leaker

import java.lang.instrument.Instrumentation

object LeakerAgent {

  val leakerClassLoader = new LeakerClassLoader(getClass.getClassLoader, getCurrentJarFileLocation)

  def agentmain(agentArgs: String, instrumentation: Instrumentation): Unit = {
    val clazz = leakerClassLoader.loadClass("org.leaker.jmx.JMXManager")
    clazz.getMethod("initialiseMBeans", classOf[Instrumentation]).invoke(null, instrumentation)
  }

  def premain(agentArgs: String, instrumentation: Instrumentation): Unit = {
    agentmain(agentArgs, instrumentation)
  }

  private def getCurrentJarFileLocation: String = {
    val url = getClass.getResource("").getPath
    url.substring(url.indexOf(':') + 1, url.indexOf('!'))
  }

}
