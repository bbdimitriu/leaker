package org.leaker

import scala.xml.{Elem, XML}

/**
 * Created by bogdan on 17/07/2014.
 */
class MethodInstrumentationDetails(val methodSignature: String,
                                   val filterOption: Option[MethodCallFilter],
                                   val transformerOption: Option[Transformer],
                                   val action: Option[InstrumentAction],
                                   val instrumentationDetailsAsXML: String)


object MethodInstrumentationDetails {

  def createInstanceFromXML(instrumentationXml: String) = {
    val xml: Elem = XML.loadString(instrumentationXml)
    val methodSignature = (xml \ "methodSignature").text.trim
    val filterString = (xml \ "filter").text.trim
    val transformerString = (xml \ "transformer").text.trim
    val actionString = (xml \ "action").text.trim

    import StringToFunctionCreator._
    val filter = createFilterFunction(filterString)
    val transformer = createTransformerFunction(transformerString)
    val action = createActionFunction(actionString)
    new MethodInstrumentationDetails(methodSignature, filter, transformer, action, instrumentationXml)
  }
}
