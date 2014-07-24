package org.leaker.jmx

import org.leaker.{MethodInstrumentationManager, MethodInstrumentationDetails}

/**
 * Created by bogdan on 24/07/2014.
 */
class InstrumentationManager extends InstrumentationManagerMBean {

  override def makeInstrumentationForXML(xmlInstrumentationDefinition: String) {
    val definition = MethodInstrumentationDetails.createInstanceFromXML(xmlInstrumentationDefinition)
    MethodInstrumentationManager.createNewObservableForMethod(definition)
  }

  override def disableInstrumentationGlobally() {
    MethodInstrumentationManager.globalEnabled = false
  }

  override def enableInstrumentationGlobally() {
    MethodInstrumentationManager.globalEnabled = true
  }

  override def disableInstrumentationForMethod(signature: String) {
    MethodInstrumentationManager.removeInstrumentationForMethod(signature)
  }

  override def clearAllInstrumentationRules() {
    MethodInstrumentationManager.clearAllInstrumentationRules()
  }
}
