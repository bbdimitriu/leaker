package org.leaker

import org.slf4j.LoggerFactory
import rx.lang.scala.Subject

import scala.collection.concurrent.TrieMap

object MethodInstrumentationManager {

  val log = LoggerFactory.getLogger(getClass)

  val methodInstrumentationMap = new TrieMap[String, MethodInstrumentation]

  def makeInstrumentationForXML(instrumentationDefinition: MethodInstrumentationDetails) {
    val subject: Subject[Array[AnyRef]] = createObservable(instrumentationDefinition)
    methodInstrumentationMap.putIfAbsent(instrumentationDefinition.methodSignature,
      new MethodInstrumentation(instrumentationDefinition, subject))
  }

  def getMethodInstrumentationsForClassName(className: String): Map[String, MethodInstrumentation] =
    methodInstrumentationMap.filter(_._1.startsWith(className)).toMap

  private def createObservable(methodInstrumentationDetails: MethodInstrumentationDetails): Subject[Array[AnyRef]] = {
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
    subject
  }

  def getObservableForMethod(method: String): Option[Subject[Array[AnyRef]]] =
    methodInstrumentationMap.get(method).map(_.observable)

  def removeInstrumentationForMethod(method: String) {
    methodInstrumentationMap.remove(method)
    // TODO need to call retransform
  }

  /**
   * This method is called from the instrumented methods "leaking" the parameters
   *
   * @param methodName the full method name (including the full class name)
   * @param params the parameters that the instrumented method was called with
   */
  def leakMethodCall(methodName: String, params: Array[AnyRef]): Unit = {
    log.debug(s"Leaked call: $methodName with parameters: ${params.mkString(", ")}")
    for (methodInstrumentation <- methodInstrumentationMap.get(methodName)) {
      methodInstrumentation.observable.onNext(params)
    }
  }

}
