package org.leaker

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import javassist.{CtNewMethod, ClassPool}

import org.slf4j.LoggerFactory

import scala.collection.immutable.Iterable

class LeakerClassTransformer(className: String, classLoader: ClassLoader) extends ClassFileTransformer {

  private val log = LoggerFactory.getLogger(classOf[LeakerClassTransformer])

  private def createInstrumentedClass(className: String,
                                      methodsToInstrumentForClass: Map[String, MethodInstrumentation]): Array[Byte] = {
    val cp = ClassPool.getDefault
    val ctClass = cp.get(className)
    for (methodEntry <- methodsToInstrumentForClass) {
      val method: String = methodEntry._1
      val methodInstrumentation = methodEntry._2
      log.debug(s"Instrumenting method '$method' in class $className")
      val ctMethodOpt = Option(ctClass.getDeclaredMethod(method))
      ctMethodOpt.foreach { ctMethod =>
        ctClass.removeMethod(ctMethod)
        val leakCall =
          s"""org.leaker.MethodInstrumentationManager.leakMethodCall("$className", "$method", $$args);"""
        ctMethod.insertBefore(leakCall)
        ctClass.addMethod(ctMethod)
      }
    }
    ctClass.toBytecode
  }

  override def transform(loader: ClassLoader, className: String, classBeingRedefined: Class[_],
                         protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]): Array[Byte] = {
    try {
      val methodsToInstrumentForClass: Map[String, MethodInstrumentation] =
        MethodInstrumentationManager.getMethodInstrumentationsForClassName(className)

      if (methodsToInstrumentForClass.size > 0 /*&& loader.equals(classLoader)*/) {
        log.info(s"Transforming class $className")
        createInstrumentedClass(className, methodsToInstrumentForClass)
      } else {
        null // no transformation to be performed
      }
    } catch {
      case t: Throwable =>
        log.error("Could not do retransformation", t)
        null
    }
  }
}
