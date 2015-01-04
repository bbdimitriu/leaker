package org.leaker.jmx

trait InstrumentationManagerMBean {

  def makeInstrumentationForXML(xmlInstrumentationDefinition: String)

  def disableInstrumentationForMethod(signature: String)

  def enableInstrumentationGlobally()

  def disableInstrumentationGlobally()

  def clearAllInstrumentationRules()
}
