/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ColorUtils;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNStyle;

public class FontSetPropertyConverter {

    public static FontSet wbFromDMN(final DMNStyle dmn) {
        FontSet result = new FontSet();
        if (null != dmn.getFontName()) {
            result.getFontFamily().setValue(dmn.getFontName());
        }
        if (null != dmn.getFontSize()) {
            result.getFontSize().setValue(dmn.getFontSize());
        }
        if (null != dmn.getFontColor()) {
            result.getFontColour().setValue(ColorUtils.wbFromDMN(dmn.getFontColor()));
        }
        if (null != dmn.getFontBorderSize()) {
            result.getFontBorderSize().setValue(dmn.getFontBorderSize());
        }
        return result;
    }

    public static DMNStyle dmnFromWB(final FontSet wb) {
        DMNStyle result = new DMNStyle();
        if (null != wb.getFontFamily().getValue()) {
            result.setFontName(wb.getFontFamily().getValue());
        }
        if (null != wb.getFontSize().getValue()) {
            result.setFontSize(wb.getFontSize().getValue());
        }
        if (null != wb.getFontColour().getValue()) {
            result.setFontColor(ColorUtils.dmnFromWB(wb.getFontColour().getValue()));
        }
        if (null != wb.getFontBorderSize().getValue()) {
            result.setFontBorderSize(wb.getFontBorderSize().getValue());
        }
        return result;
    }
}