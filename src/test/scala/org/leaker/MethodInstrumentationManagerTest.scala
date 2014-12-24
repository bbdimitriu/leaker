package org.leaker

import org.scalatest.{Ignore, FunSuite}

@Ignore
class MethodInstrumentationManagerTest extends FunSuite {
/*
  test("Check that we receive events") {
    var i = 0
    val methodInstrumentationDetails = new MethodInstrumentationDetails(
      methodSignature = "mytestMethod",
      filterOption = Some((args: Array[AnyRef]) => args(0).asInstanceOf[java.lang.Integer] > 20),
      transformerOption = Some((args: Array[AnyRef]) =>
        (args(0).asInstanceOf[java.lang.Integer] * 1000).asInstanceOf[java.lang.Integer]),
      action = Some(obj => {
        i += 1
        println(s"Final value: $obj")
      }),
      "fill_with_XML")
    //MethodInstrumentationManager.instrumentMethod(methodInstrumentationDetails)
    val subjectOption = MethodInstrumentationManager.getObservableForMethod(methodInstrumentationDetails
      .methodSignature).get
    subjectOption.onNext(Array(Integer.valueOf(30)))
    assert(i == 1, "Expected that the value was incremented")

    // now check that the filter works
    subjectOption.onNext(Array(Integer.valueOf(10)))
    assert(i == 1, "Expected that the value was NOT incremented")
  }
*/
}
