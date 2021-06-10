/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ImageDataUtil;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;

public abstract class AbstractTableImageDataFilter<T extends AbstractTableImageDataFilter<T>> extends AbstractImageDataFilter<T> {

    protected AbstractTableImageDataFilter(final ImageFilterType type) {
        super(type);
    }

    protected AbstractTableImageDataFilter(final ImageFilterType type, final Object node, final ValidationContext ctx) throws ValidationException {
        super(type, node, ctx);
    }

    @Override
    public ImageData filter(ImageData source, final boolean copy) {
        if (null == source) {
            return null;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        if (false == isActive()) {
            return source;
        }
        final Uint8ClampedArray data = source.data;

        if (null == data) {
            return source;
        }
        FilterCommonOps.doFilterTable(data, getTable(), source.width, source.height);

        return source;
    }

    @Override
    public final boolean isTransforming() {
        return true;
    }

    protected abstract FilterTableArray getTable();

    protected static abstract class TableImageDataFilterFactory<T extends AbstractTableImageDataFilter<T>> extends ImageDataFilterFactory<T> {

        protected TableImageDataFilterFactory(final ImageFilterType type) {
            super(type);
        }
    }
}