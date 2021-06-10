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

package com.ait.lienzo.client.core.image;

import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.widget.RootPanel;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.Image;

public abstract class ImageLoader {

    public ImageLoader(final String url) {
        this(url,
             new Image());
    }

    public ImageLoader(final String url,
                       final HTMLImageElement image) {
        setVisible(image, false);

        final String crossOrigin = url.startsWith("http:") || (url.startsWith("https:")) ? "anonymous" : null;

        if (null != crossOrigin) {
            setCrossOrigin(image, crossOrigin);
        }

        image.onload = e ->
        {
            image.onload = null;
            image.onerror = null;
            doImageElementLoadAndRetry(image, crossOrigin, url);
            return null;
        };

        image.onerror = e ->
        {
            image.onload = null;
            image.onerror = null;
            image.remove();
            onImageElementError("Resource " + url + " failed to load");
            return null;
        };

        RootPanel.get().add(image);

        // @FIXME I removed "isValidDataURL(url) && isValidSVG(url)" as it seems with Elemental2 it all end up at .src. But we should double check this (mdp)
        image.src = url;
    }

    public ImageLoader(final ImageResource resource) {
        this(resource,
             new Image());
    }

    public static void setVisible(HTMLElement image, boolean visible) {
        image.style.display = visible ? "" : Style.Display.NONE.getCssName(); // @FIXME check that it should really be "", I just followed GWT UIObject setVisible.

        if (visible) {
            image.removeAttribute("aria-hidden");
        } else {
            image.setAttribute("aria-hidden", "true");
        }
    }

    public ImageLoader(final ImageResource resource,
                       final HTMLImageElement image) {
        setVisible(image, false);

        image.onload = e ->
        {
            image.onload = null;
            image.onerror = null;
            onImageElementLoad(image);
            return null;
        };

        image.onerror = e ->
        {
            image.onload = null;
            image.onerror = null;
            image.remove();
            onImageElementError("Resource " + resource.getName() + " failed to load");
            return null;
        };

        // @FIXME double check this works, ImageReources can includ clippping information, so I had to case from Elemental2 to GWT (mdp)

        String urlAsString = resource.getSafeUri().asString();
        if (resource instanceof ImageResourcePrototype.Bundle) {
            // lifted from com.google.gwt.user.client.ui.impl.ClippedImageImpl adjust
            image.style.background = "url(\"" + urlAsString + "\") no-repeat " + (-resource.getLeft() + "px ") + (-resource.getTop() + "px");
            image.style.width = CSSProperties.WidthUnionType.of(resource.getHeight());
            image.style.height = CSSProperties.HeightUnionType.of(resource.getHeight());
        } else {
            // lifted from com.google.gwt.user.client.ui.Image setResource
            image.src = urlAsString;
            image.width = resource.getWidth();
            image.height = resource.getHeight();
        }

        RootPanel.get().add(image);
    }

    private final void doImageElementLoadAndRetry(final HTMLImageElement image,
                                                  final String orig,
                                                  final String url) {
        final int w = Math.max(image.width, image.width);

        final int h = Math.max(image.height, image.height);

        if ((w < 1) || (h < 1)) {

            image.onload = e ->
            {
                image.onload = null;
                image.onerror = null;
                // @FIXME removed 'naturalHeight' in image I think this is supported by all browser now. Needs double check (mdp)
                if (image.naturalHeight + image.naturalWidth == 0 || image.height + image.width == 0) {
                    // it failed, so undo
                    // @FIXME check this cast works (mdp)
                    RootPanel.get().remove(image);
                    onImageElementError("Image " + url + " failed to load");
                    image.crossOrigin = orig;
                } else {
                    onImageElementLoad(image);
                }
                return null;
            };

            image.onerror = e ->
            {
                image.onload = null;
                image.onerror = null;
                // it failed, so undo
                // @FIXME check this cast works (mdp)
                RootPanel.get().remove(image);
                onImageElementError("Image " + url + " failed to load");
                image.crossOrigin = orig;
                return null;
            };

            image.src = url;
        } else {
            image.width = w;

            image.height = h;

            onImageElementLoad(image);
        }
    }

    public boolean isValidDataURL(final String url) {
        if ((url.startsWith("data:")) && (url.length() > 6) && (!("data:,".equals(url)))) {
            return true;
        }
        return false;
    }

    public boolean isValidSVG(final String url) {
        return url.toLowerCase().contains("svg+xml");
    }

    private final void setCrossOrigin(HTMLImageElement element, String value) {
        element.crossOrigin = value;
    }

    public abstract void onImageElementLoad(HTMLImageElement elem);

    public abstract void onImageElementError(String message);
}