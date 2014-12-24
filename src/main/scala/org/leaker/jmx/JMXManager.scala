package org.leaker.jmx

import java.lang.instrument.Instrumentation
import java.lang.management.ManagementFactory
import javax.management.ObjectName

object JMXManager {

  def initialiseMBeans(instrumentation: Instrumentation) {
    val mbeanServer = ManagementFactory.getPlatformMBeanServer

    val instrumentationManagerObject = new ObjectName("org.leaker:type=MethodInstrumentationManager")
    mbeanServer.registerMBean(new InstrumentationManager(instrumentation), instrumentationManagerObject)
  }
}
