package org.leaker

import rx.lang.scala.Subject

class MethodInstrumentation(val methodInstrumentationDetails: MethodInstrumentationDetails,
                            val observable: Subject[Array[AnyRef]])