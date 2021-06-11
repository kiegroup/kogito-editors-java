package org.kie.lienzo.client;

import jsinterop.annotations.JsType;

@JsType
public class JsLienzoExamples {

    BaseLienzoExamples examples;

    public void goToExample(int index) {
        examples.goToTest(index);
    }
}
