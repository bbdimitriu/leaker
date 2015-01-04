package org.leaker

class MethodInstrumentationDetails(val methodSignature: String,
                                   val filterOption: Option[MethodCallFilter],
                                   val transformerOption: Option[Transformer],
                                   val action: Option[InstrumentAction]) {
  val methodClass = methodSignature.substring(0, methodSignature.lastIndexOf('.'))
}
