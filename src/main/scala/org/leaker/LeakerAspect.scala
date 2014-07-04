package org.leaker

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.{Aspect, Before, Pointcut}

/**
 * Created by bogdan on 04/07/2014.
 */
@Aspect
class LeakerAspect {

  @Pointcut("within(org.leaker..*) || within(scala..*)")
  def getSelfPointcut {
  }

  @Pointcut("call(* *..*(..))")
  def getAllMethodsPointcut {
  }

  @Pointcut("!getSelfPointcut() && getAllMethodsPointcut()")
  def getPrunedPointcut {
  }

  @Before("getPrunedPointcut()")
  def ourAfterAdvice(joinPoint: JoinPoint) {
    val signature: String = joinPoint.getSignature.toString
    if (InstrumentationManager.isMethodEnabledForInstrumentation(signature)) {
      dealWithArguments(signature, joinPoint.getArgs)
    }
  }

  private def dealWithArguments(signature: String, args: Array[AnyRef]) {
    val representation = InstrumentationManager.getTransformerForSignature(signature)(args)
    println(s"$signature: [$representation]")
  }
}
