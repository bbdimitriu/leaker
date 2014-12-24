package org.leaker

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import javassist.{CtMethod, CtClass, ClassPool}

import org.slf4j.LoggerFactory

class LeakerClassTransformer(className: String, classLoader: ClassLoader) extends ClassFileTransformer {

  private val log = LoggerFactory.getLogger(classOf[LeakerClassTransformer])

  private def createInstrumentedClass(className: String,
                                      methodsToInstrumentForClass: Set[String]): Array[Byte] = {
    val cp = ClassPool.getDefault
    val ctClass = cp.get(className)
    val classDeclaredMethodsSignatureMap = getDeclaredMethodsSignatureMap(ctClass)
    for (fullMethodName <- methodsToInstrumentForClass) {
      classDeclaredMethodsSignatureMap.get(fullMethodName) match {
        case Some(ctMethod) =>
          log.debug(s"Instrumenting method '$fullMethodName' in class $className")
          //ctClass.removeMethod(ctMethod)
          val leakCall =
            s"""org.leaker.MethodInstrumentationManager.leakMethodCall("$className", "$fullMethodName", $$args);"""
          ctMethod.insertBefore(leakCall)
          //ctClass.addMethod(ctMethod)
        case None =>
          log.warn(s"Requested instrumentation of non existing method with signature $fullMethodName")
      }
    }
    ctClass.toBytecode
  }

  private def getDeclaredMethodsSignatureMap(ctClass: CtClass): Map[String, CtMethod] =
    ctClass.getDeclaredMethods.map(ctMethod => ctMethod.getLongName -> ctMethod).toMap

  override def transform(loader: ClassLoader, className: String, classBeingRedefined: Class[_],
                         protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]): Array[Byte] = {
    try {
      val methodsToInstrumentForClass: Set[String] =
        MethodInstrumentationManager.getMethodInstrumentationsForClassName(className).keySet

      if (methodsToInstrumentForClass.size > 0 /*&& loader.equals(classLoader)*/ ) {
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
