/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.api.definition.model.common;

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.model.HasEqualsIgnoreId;

public class ListComparerHelper {

    public static <E extends HasEqualsIgnoreId> boolean compare(final List<E> l1,
                                                                final List<E> l2,
                                                                boolean ignoreId) {
        if (ignoreId) {
            return compareIgnoreId(l1, l2);
        }
        return l1.equals(l2);
    }

    public static <E extends HasEqualsIgnoreId> boolean compareIgnoreId(final List<E> l1,
                                                                        final List<E> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            if (!l1.get(i).equals(l2.get(i), true)) {
                return false;
            }
        }

        return true;
    }
}
