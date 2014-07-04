package org.leaker

import scala.collection.mutable.{Map => MMap}

/**
 * Created by bogdan on 04/07/2014.
 */
object InstrumentationManager {

  type Transformer = (Array[AnyRef]) => String

  type ParameterFilter = (Array[AnyRef] => Boolean)

  val instrumentedMethods = MMap.empty[String, Transformer]

  val DefaultTransformer = (params: Array[AnyRef]) => {
    // FIXME - got to escape commas in parameters
    params.mkString(",")
  }

  def enableMethodForInstrumentation(signature: String, transformer: Transformer) {
    instrumentedMethods += signature -> transformer
  }

  def enableMethodForInstrumentation(signature: String) {
    instrumentedMethods += signature -> DefaultTransformer
  }

  def disableMethodForInstrumentation(signature: String) {
    instrumentedMethods -= signature
  }

  def isMethodEnabledForInstrumentation(signature: String) = instrumentedMethods.contains(signature)

  def getTransformerForSignature(signature: String) = instrumentedMethods(signature)
}
