package org.leaker.jmx

import java.lang.instrument.Instrumentation

import org.leaker.{LeakerClassTransformer, MethodInstrumentationManager}

class InstrumentationManager(instrumentation: Instrumentation) extends InstrumentationManagerMBean {

  override def makeInstrumentationForXML(xmlInstrumentationDefinition: String) {
    MethodInstrumentationManager.makeInstrumentationForXML(xmlInstrumentationDefinition)
    val targetClazz = Class.forName("MyClass")
    val targetClassLoader = targetClazz.getClassLoader

    val transformer: LeakerClassTransformer = new LeakerClassTransformer("MyClass", targetClassLoader)
    instrumentation.addTransformer(transformer, true)
    try {
      instrumentation.retransformClasses(targetClazz)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new RuntimeException("Failed to transform [" + targetClazz.getName + "]", ex)
    } finally {
      instrumentation.removeTransformer(transformer)
    }
  }

  override def disableInstrumentationGlobally() {
    // TODO
  }

  override def enableInstrumentationGlobally() {
    // TODO
  }

  override def disableInstrumentationForMethod(signature: String) {
    MethodInstrumentationManager.removeInstrumentationForMethod(signature)
  }

  override def clearAllInstrumentationRules() {
  }
}
