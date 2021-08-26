package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

@JsType
public class InvocationProps extends ExpressionProps{
    public final String invokedFunction;
    public final ContextEntryProps[] bindingEntries;

    public InvocationProps(final String name, final String dataType, final String invokedFunction, final ContextEntryProps[] bindingEntries) {
        super(name, dataType, "Invocation");
        this.invokedFunction = invokedFunction;
        this.bindingEntries = bindingEntries;
    }
}
