package org.leaker

import org.leaker.jmx.JMXManager
import rx.lang.scala.Subject

/**
 * Created by bogdan on 14/07/2014.
 */
object MethodInstrumentationManager {

  val methodObservables =
    new java.util.concurrent.ConcurrentHashMap[String, Subject[Array[AnyRef]]]
  val methodInstrumentationDetailsMap =
    new java.util.concurrent.ConcurrentHashMap[String, MethodInstrumentationDetails]
  @volatile var globalEnabled: Boolean = true

  JMXManager.initialiseMBeans()

  // TODO remove this section later - initialisation with a demo instrumentation
  //  val sample = new MethodInstrumentationDetails(
  //    filterOption = None,//Some(args => args(0).asInstanceOf[java.lang.Integer] > 2),
  //    transformerOption =
  //      Some(args => (args(0).asInstanceOf[java.lang.Integer] + args(1).asInstanceOf[java.lang.Integer]).toString),
  //    action = Some(arg => println(arg)),
  //    instrumentationDetailsAsXML = "XML goes here"
  //  )
  /*
  val sample = MethodInstrumentationDetails.createInstanceFromXML(
    """
      |<instrumentation>
      | <methodSignature>
      | <![CDATA[
      | private static void org.test.InterruptTest.myMethod(int, int, java.lang.String)
      | ]]>
      | </methodSignature>
      | <filter>
      | <![CDATA[
      | ((Integer)args[0]).intValue() > 50
      | ]]>
      | </filter>
      | <transformer>
      | <![CDATA[
      | "Action print: " + args[0] + " " + (((Integer)args[1]).intValue() % 2)
      | ]]>
      | </transformer>
      | <action>
      | <![CDATA[
      | System.out.println(arg)
      | ]]>
      | </action>
      |</instrumentation>
    """.stripMargin)
  createNewObservableForMethod(sample)
  */
  // TODO end of section to be removed

  def createNewObservableForMethod(methodInstrumentationDetails: MethodInstrumentationDetails) = {
    val subject: Subject[Array[AnyRef]] = Subject[Array[AnyRef]]()
    val filtered =
      if (methodInstrumentationDetails.filterOption.isDefined)
        subject.filter(methodInstrumentationDetails.filterOption.get)
      else
        subject
    val transformed =
      if (methodInstrumentationDetails.transformerOption.isDefined)
        filtered.map(methodInstrumentationDetails.transformerOption.get)
      else
        filtered
    // TODO subscribe on a different scheduler
    if (methodInstrumentationDetails.action.isDefined) {
      transformed.subscribe(methodInstrumentationDetails.action.get)
    } else {
      // TODO print warning, no action is taken, useless case
    }
    synchronized {
      methodObservables.putIfAbsent(methodInstrumentationDetails.methodSignature, subject)
      methodInstrumentationDetailsMap.putIfAbsent(methodInstrumentationDetails.methodSignature,
        methodInstrumentationDetails)
    }
  }

  def getObservableForMethod(method: String): Option[Subject[Array[AnyRef]]] =
    Option(methodObservables.get(method))

  def removeInstrumentationForMethod(method: String) {
    synchronized {
      methodObservables.remove(method)
      methodInstrumentationDetailsMap.remove(method)
    }
  }

  def clearAllInstrumentationRules() {
    synchronized {
      methodObservables.clear()
      methodInstrumentationDetailsMap.clear()
    }
  }

  def getInstrumentationDetailsForMethod(method: String): Option[String] =
    Option(methodInstrumentationDetailsMap.get(method)).flatMap {
      (methodInstrumentationDetails: MethodInstrumentationDetails) =>
        Some(methodInstrumentationDetails.instrumentationDetailsAsXML)
    }

}
