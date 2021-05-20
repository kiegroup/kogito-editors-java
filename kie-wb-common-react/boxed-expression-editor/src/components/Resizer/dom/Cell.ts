/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as _ from "lodash";
import { notifyCell, widthValue } from "../common";

const RESIZER_PADDING = 14;
const BORDER = 1;

export class Cell {
  private id?: string;
  private lastColumn?: boolean;
  private rect?: DOMRect;
  private parentRow?: HTMLTableRowElement | null;

  constructor(public element: HTMLElement, public children: Cell[], public depth: number) {}

  getId(): string {
    if (!this.id) {
      this.id = _.first([].slice.call(this.element.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }
    return this.id;
  }

  getRect(): DOMRect {
    if (!this.rect) {
      this.rect = this.element.getBoundingClientRect();
    }
    return this.rect;
  }

  isLastColumn(): boolean {
    if (!this.lastColumn) {
      this.lastColumn = this.getParentRow()?.lastChild === this.element.closest("th, td");
    }
    return this.lastColumn;
  }

  setWidth(width: number): void {
    const cellWidth = widthValue(width);
    notifyCell(this.getId(), cellWidth);
    this.element.style.width = cellWidth + "px";
  }

  refreshWidthAsParent(): void {
    if (this.children.length > 0) {
      this.setWidth(this.fetchChildWidth() + RESIZER_PADDING);
    }
  }

  refreshWidthAsLastColumn(): void {
    if (!this.isLastColumn()) {
      return;
    }

    const parentRect = this.getParentRow()?.getBoundingClientRect();

    if (parentRect) {
      this.setWidth(Math.round(parentRect.right) - Math.round(this.getRect().x) - BORDER);
    }
  }

  private getParentRow() {
    if (!this.parentRow) {
      this.parentRow = this.element.closest("tr");
    }
    return this.parentRow;
  }

  private fetchChildWidth() {
    const thead = this.element.querySelector("thead, tbody");
    return widthValue(thead?.getBoundingClientRect().width);
  }
}
