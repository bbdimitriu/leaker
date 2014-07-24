package org.leaker.jmx

/**
 * Created by bogdan on 24/07/2014.
 */
trait InstrumentationManagerMBean {

  def makeInstrumentationForXML(xmlInstrumentationDefinition: String)

  def disableInstrumentationForMethod(signature: String)

  def enableInstrumentationGlobally()

  def disableInstrumentationGlobally()

  def clearAllInstrumentationRules()
}
