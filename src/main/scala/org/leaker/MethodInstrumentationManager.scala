package org.leaker

import org.slf4j.LoggerFactory
import rx.lang.scala.Subject

import scala.collection.concurrent.TrieMap

object MethodInstrumentationManager {

  val log = LoggerFactory.getLogger(MethodInstrumentationManager.getClass)

  val methodInstrumentationMap = new TrieMap[String, MethodInstrumentation]

  def makeInstrumentationForXML(xmlInstrumentationDefinition: String) {
    val definition = MethodInstrumentationDetails.createInstanceFromXML(xmlInstrumentationDefinition)
    val subject: Subject[Array[AnyRef]] = createObservable(definition)

    methodInstrumentationMap.putIfAbsent(definition.methodSignature,
      new MethodInstrumentation(definition, subject))
  }

  def getMethodInstrumentationsForClassName(className: String): Map[String, MethodInstrumentation] =
    // FIXME make it work with different signatures for the same method name
    methodInstrumentationMap.filter(_._1.startsWith(className)).map { entry =>
      val signature = entry._1
      signature.drop(signature.lastIndexOf('.') + 1).takeWhile(_ != '(') -> entry._2
    }.toMap

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

  def leakMethodCall(className: String, methodName: String, params: Array[AnyRef]): Unit = {
    log.info(s"Leaked call: $className.$methodName with parameters: ${params.mkString(", ")}")
  }

}
