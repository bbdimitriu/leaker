package org.leaker

import org.scalatest.{Ignore, FunSuite}

/**
 * Created by bogdan on 23/07/2014.
 */
@Ignore
class MethodInstrumentationDeailsTest extends FunSuite {

  test("creating successful MethodInstrumentationDetails from XML") {
    val sample = MethodInstrumentationDetails.createInstanceFromXML(
      """
        |<instrumentation>
        | <methodSignature>
        | <![CDATA[
        | private static void org.test.InterruptTest.myMethod(int, int,
        | java.lang.String)
        | ]]>
        | </methodSignature>
        | <filter>
        | <![CDATA[
        | ((Integer)args[0]).intValue() > 50
        | ]]>
        | </filter>
        | <transformer>
        | <![CDATA[
        | "Action print: " + args[0] + " " + (((Integer)args[1]).intValue() % 2)
        | ]]>
        | </transformer>
        | <action>
        | <![CDATA[
        | System.out.println(arg)
        | ]]>
        | </action>
        |</instrumentation>
      """.stripMargin)
  }
}
