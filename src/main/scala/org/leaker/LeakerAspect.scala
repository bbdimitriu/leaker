package org.leaker

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.{Aspect, Before, Pointcut}

/**
 * Created by bogdan on 04/07/2014.
 */
@Aspect
class LeakerAspect {

  @Pointcut("within(org.leaker..*) || within(scala..*) || within(java..*)")
  def exceptionsPointcut() {
  }

  @Pointcut("call(* *..*(..))")
  def allMethodsPointcut() {
  }

  @Pointcut("!exceptionsPointcut() && allMethodsPointcut()")
  def prunedPointcut() {
  }

  @Before("prunedPointcut()")
  def ourBeforeAdvice(joinPoint: JoinPoint) {
    if (MethodInstrumentationManager.globalEnabled) {
      val signature: String = joinPoint.getSignature.toLongString
      // pushes the event into the Observable to notify the Observers
      MethodInstrumentationManager.getObservableForMethod(signature).foreach(_.onNext(joinPoint.getArgs))
    }
  }

}
