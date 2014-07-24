package org.leaker.jmx

import java.lang.management.ManagementFactory
import javax.management.ObjectName

/**
 * Created by bogdan on 24/07/2014.
 */
object JMXManager {

  def initialiseMBeans() {
    val mbeanServer = ManagementFactory.getPlatformMBeanServer();

    val instrumentationManagerObject = new ObjectName("com.leaker:type=MethodInstrumentationManager");
    mbeanServer.registerMBean(new InstrumentationManager(), instrumentationManagerObject);
  }
}
