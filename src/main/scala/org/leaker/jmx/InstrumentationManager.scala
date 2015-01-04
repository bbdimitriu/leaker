package org.leaker.jmx

import java.lang.instrument.Instrumentation

import org.leaker._
import org.slf4j.LoggerFactory

import scala.xml.{XML, Elem}

class InstrumentationManager(instrumentation: Instrumentation) extends InstrumentationManagerMBean {

  val log = LoggerFactory.getLogger(getClass)

  override def makeInstrumentationForXML(xmlInstrumentationDefinition: String) {
    try {
      val methodInstrumentationDefinition = createInstanceFromXML(xmlInstrumentationDefinition)
      MethodInstrumentationManager.makeInstrumentationForXML(methodInstrumentationDefinition)
      val classToInstrument: String = methodInstrumentationDefinition.methodClass
      // TODO this only instruments classes loaded with the system class loader. How to handle other loaders?
      // Maybe use http://docs.oracle.com/javase/7/docs/api/java/lang/instrument/Instrumentation.html#getAllLoadedClasses()
      val targetClazz = Class.forName(classToInstrument)
      val transformer: LeakerClassTransformer = new LeakerClassTransformer(classToInstrument)
      try {
        instrumentation.addTransformer(transformer, true)
        instrumentation.retransformClasses(targetClazz)
      } finally {
        instrumentation.removeTransformer(transformer)
      }
    } catch {
      case ex: Throwable =>
        log.error("Could not instrument with the given definition [" + xmlInstrumentationDefinition + "]", ex)
        throw new RuntimeException("Failed to apply transformation for the given definition" , ex)
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

  private def createInstanceFromXML(instrumentationXml: String): MethodInstrumentationDetails = {
    val xml: Elem = XML.loadString(instrumentationXml)
    val methodSignature = (xml \ "methodSignature").text.trim
    val filterString = (xml \ "filter").text.trim
    val transformerString = (xml \ "transformer").text.trim
    val actionString = (xml \ "action").text.trim

    import StringToFunctionCreator._
    val filter = createFilterFunction(filterString)
    val transformer = createTransformerFunction(transformerString)
    val action = createActionFunction(actionString)
    new MethodInstrumentationDetails(methodSignature, filter, transformer, action)
  }

}
