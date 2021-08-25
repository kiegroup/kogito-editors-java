/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./EditableCell.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { CellProps } from "../../api";

export const READ_MODE = "editable-cell--read-mode";
export const EDIT_MODE = "editable-cell--edit-mode";

export interface EditableCellProps extends CellProps {
  /** Cell's value */
  value: string;
  /** Function executed each time a cell gets updated */
  onCellUpdate: (rowIndex: number, columnId: string, value: string) => void;
  /** Enable/Disable readonly */
  readOnly?: boolean;
}

export function EditableCell({ value, row: { index }, column: { id }, onCellUpdate, readOnly }: EditableCellProps) {
  const [isSelected, setIsSelected] = useState(false);
  const [mode, setMode] = useState(READ_MODE);
  const textarea = useRef<HTMLTextAreaElement>(null);

  const onChange = useCallback((e) => {
    setMode(EDIT_MODE);
    onCellUpdate(index, id, e.target.value);
  }, []);

  const onBlur = useCallback(() => {
    setMode(READ_MODE);
    setIsSelected(false);
  }, []);

  const onSelect = useCallback(() => {
    setIsSelected(true);

    if (document.activeElement === textarea.current) {
      return;
    }

    textarea.current?.focus();
    if (value) {
      textarea.current?.setSelectionRange(value.length, value.length);
    } else {
      textarea.current?.setSelectionRange(0, 0);
    }
  }, []);

  const onDoubleClick = useCallback(() => {
    setMode(EDIT_MODE);
  }, []);

  const cssClass = useCallback(() => {
    const selectedClass = isSelected ? "editable-cell--selected" : "";
    return `editable-cell ${selectedClass} ${mode}`;
  }, [isSelected, mode]);

  const onKeyPress = useCallback(
    (event) => {
      if (event.key.toLowerCase() !== "enter") {
        return;
      }

      event.preventDefault();

      if (mode === READ_MODE) {
        setMode(EDIT_MODE);
        return;
      }

      const newValue = event.target.value;

      if (event.altKey || event.ctrlKey) {
        onCellUpdate(index, id, `${newValue}\n`);
        return;
      }

      onCellUpdate(index, id, newValue);
      setMode(READ_MODE);
    },
    [mode, index, id]
  );

  return (
    <div onDoubleClick={onDoubleClick} onClick={onSelect} className={cssClass()}>
      <span>
        {Array.isArray(value) ? JSON.stringify(value[index][id]) : typeof value === "object" ? value[id] : value}
      </span>
      <textarea
        ref={textarea}
        value={
          Array.isArray(value) ? JSON.stringify(value[index][id]) : typeof value === "object" ? value[id] : value || ""
        }
        onFocus={onSelect}
        onKeyPress={onKeyPress}
        onChange={onChange}
        onBlur={onBlur}
        readOnly={readOnly}
      />
    </div>
  );
}
