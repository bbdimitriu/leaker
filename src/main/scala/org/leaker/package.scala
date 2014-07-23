package org

import rx.functions.{Action1, Func1}

/**
 * Created by bogdan on 04/07/2014.
 */
package object leaker {

  type Transformer = (Array[AnyRef]) => AnyRef

  type MethodCallFilter = (Array[AnyRef]) => Boolean

  type InstrumentAction = (AnyRef) => Unit

  implicit def convertFunc1FilterToFunction(func: Func1[Array[AnyRef], Boolean]): MethodCallFilter = func.call

  implicit def convertFunc1TransformerToFunction(func: Func1[Array[AnyRef], AnyRef]): Transformer = func.call

  implicit def convertAction1TransformerToFunction(func: Action1[AnyRef]): InstrumentAction = func.call

}
