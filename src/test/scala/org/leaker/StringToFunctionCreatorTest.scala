package org.leaker

import org.scalatest.{Ignore, FunSuite}

/**
 * Created by bogdan on 23/07/2014.
 */
@Ignore
class StringToFunctionCreatorTest extends FunSuite {

  test("Creating filter function succeeds") {
    val filter = StringToFunctionCreator.createFilterFunction("((Integer)args[1]).intValue() > 20")
    val func = filter.get
    assert(false == func(Array(new Integer(2),new Integer(3), new Integer(4))))
    assert(true == func(Array(new Integer(2),new Integer(30), new Integer(4))))
  }

}
