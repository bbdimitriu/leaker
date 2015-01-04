package org.leaker

import java.util.concurrent.atomic.AtomicInteger
import javassist.{LoaderClassPath, CtNewMethod, ClassPool}

import rx.functions.{Action1, Func1}

object StringToFunctionCreator {

  val transformerClassNameIndex = new AtomicInteger(0)
  val methodCallFilterClassNameIndex = new AtomicInteger(0)
  val instrumentActionClassNameIndex = new AtomicInteger(0)

  val pool = new ClassPool(false)
  pool.appendClassPath(new LoaderClassPath(getClass.getClassLoader))
  pool.appendSystemPath()

  def createFilterFunction(filterString: String): Option[MethodCallFilter] = {
    if (filterString.isEmpty)
      None
    else {
      val className = "MethodCallFilter" + methodCallFilterClassNameIndex.getAndIncrement

      val filterClass = pool.makeClass(className)
      filterClass.addInterface(pool.get("rx.functions.Func1"))
      // Javaassist doesn't know about generics or autoboxing :(
      val fullFilterMethodString =
        s"""public Object call(Object arg) {
           |  Object[] args = (Object[]) arg;
           |  return new Boolean($filterString);
           |}""".stripMargin
      val filterMethod = CtNewMethod.make(fullFilterMethodString, filterClass)
      filterClass.addMethod(filterMethod)
      val filterInstance = filterClass.toClass(getClass.getClassLoader, getClass.getProtectionDomain).newInstance()
      Some(filterInstance.asInstanceOf[Func1[Array[AnyRef], Boolean]])
    }
  }

  def createTransformerFunction(transformerString: String): Option[Transformer] = {
    if (transformerString.isEmpty)
      None
    else {
      val className = "Transformer" + transformerClassNameIndex.getAndIncrement
      val transformerClass = pool.makeClass(className)
      transformerClass.addInterface(pool.get("rx.functions.Func1"))
      // Javaassist doesn't know about generics or autoboxing :(
      val fullTransformerMethodString =
        s"""public Object call(Object arg) {
           |  Object[] args = (Object[]) arg;
           |  return $transformerString;
           |}""".stripMargin
      val transformerMethod = CtNewMethod.make(fullTransformerMethodString, transformerClass)
      transformerClass.addMethod(transformerMethod)
      val transformerInstance = transformerClass.toClass(getClass.getClassLoader, getClass.getProtectionDomain).newInstance()
      Some(transformerInstance.asInstanceOf[Func1[Array[AnyRef], AnyRef]])
    }
  }

  def createActionFunction(actionString: String): Option[InstrumentAction] = {
    if (actionString.isEmpty)
      None
    else {
      val className = "InstrumentAction" + instrumentActionClassNameIndex.getAndIncrement
      val actionClass = pool.makeClass(className)
      actionClass.addInterface(pool.get("rx.functions.Action1"))
      // Javaassist doesn't know about generics or autoboxing :(
      val fullActionMethodString =
        s"""public void call(Object arg) {
           |  $actionString;
           |}""".stripMargin
      val actionMethod = CtNewMethod.make(fullActionMethodString, actionClass)
      actionClass.addMethod(actionMethod)
      val actionInstance = actionClass.toClass(getClass.getClassLoader, getClass.getProtectionDomain).newInstance()
      Some(actionInstance.asInstanceOf[Action1[AnyRef]])
    }
  }

}
