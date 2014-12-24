package org.leaker

import java.lang.instrument.Instrumentation

import org.leaker.jmx.JMXManager

object LeakerAgent {

  def agentmain(agentArgs: String, instrumentation: Instrumentation): Unit = {
    JMXManager.initialiseMBeans(instrumentation)
  }

  def premain(agentArgs: String, instrumentation: Instrumentation): Unit = {
    agentmain(agentArgs, instrumentation)
  }
}
