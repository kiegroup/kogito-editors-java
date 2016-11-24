/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.monitor.LibraryMonitor;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.InfoPopup;
import org.kie.workbench.common.screens.library.client.util.LibraryBreadcrumbs;
import org.kie.workbench.common.screens.library.client.util.LibraryParameters;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;

@WorkbenchScreen( identifier = "NewProjectScreen" )
public class NewProjectScreen {

    public interface View extends UberElement<NewProjectScreen> {

        void addOrganizationUnit( String ou );

        void clearOrganizationUnits();

        void setOrganizationUnitSelected( String identifier );

        void setOUAlias( String ouAlias );

    }

    @Inject
    Caller<LibraryService> libraryService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private LibraryBreadcrumbs breadcrumbs;

    @Inject
    private View view;

    @Inject
    private TranslationService ts;

    @Inject
    private Event<LibraryContextSwitchEvent> libraryContextSwitchEvent;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private LibraryMonitor libraryMonitor;

    private DefaultPlaceRequest backPlaceRequest;

    String selectOu = "";

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        setupBreadCrumbs();
        setupBackPlaceRequest( place );
        loadSelectedOU( place );
    }

    private void setupBreadCrumbs() {
        breadcrumbs.setupLibraryBreadCrumbs();
    }

    private void loadSelectedOU( PlaceRequest place ) {
        this.selectOu = place.getParameter( LibraryParameters.SELECTED_OU, "" );
        load();
    }

    void load() {
        libraryService.call( new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback( LibraryInfo lib ) {
                view.setOUAlias( lib.getOuAlias() );
                clearOrganizationUnits();
                lib.getOrganizationUnits()
                        .forEach( ou -> addOrganizationUnit( ou.getIdentifier() ) );
                if ( selectOu.isEmpty() ) {
                    NewProjectScreen.this.selectOu = lib.getSelectedOrganizationUnit().getIdentifier();
                }
                setOrganizationUnitSelected( selectOu );
            }
        } ).getDefaultLibraryInfo();
    }

    private void addOrganizationUnit( String identifier ) {
        view.addOrganizationUnit( identifier );
    }

    private void clearOrganizationUnits() {
        view.clearOrganizationUnits();
    }

    private void setOrganizationUnitSelected( String identifier ) {
        view.setOrganizationUnitSelected( identifier );
    }

    private void setupBackPlaceRequest( PlaceRequest place ) {
        String placeTarget = place.getParameter( LibraryParameters.BACK_PLACE, LibraryPlaces.EMPTY_LIBRARY_SCREEN );
        this.backPlaceRequest = new DefaultPlaceRequest( placeTarget );

    }

    public void back() {
        placeManager.goTo( backPlaceRequest );
    }

    public void createProject( String projectName ) {
        busyIndicatorView.showBusyIndicator( ts.getTranslation( LibraryConstants.NewProjectScreen_Saving ) );

        libraryService.call( getSuccessCallback(), getErrorCallBack() ).newProject( projectName, selectOu, getBaseURL() );
    }

    private String getBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        return baseUrl;
    }

    private ErrorCallback<?> getErrorCallBack() {
        return ( o, throwable ) -> {
            hideLoadingBox();
            notificationEvent
                    .fire( new NotificationEvent( ts.getTranslation( LibraryConstants.NewProjectScreen_Error ),
                                                  NotificationEvent.NotificationType.ERROR ) );
            return false;
        };
    }

    private void hideLoadingBox() {
        busyIndicatorView.hideBusyIndicator();
    }

    RemoteCallback<KieProject> getSuccessCallback() {
        return project -> {
            libraryMonitor.setThereIsAtLeastOneProjectAccessible( true );
            hideLoadingBox();
            notifySuccess();
            gotoAuthoring( project );
        };
    }

    void gotoAuthoring( KieProject project ) {
        if ( hasAccessToPerspective( LibraryPlaces.AUTHORING ) ) {
            setupBreadCrumbs( project );
            openProject( project );
        } else {
            InfoPopup.generate( ts.getTranslation( LibraryConstants.Error_NoAccessRights ) );
        }
    }

    boolean hasAccessToPerspective( String perspectiveId ) {
        ResourceRef resourceRef = new ResourceRef( perspectiveId, ActivityResourceType.PERSPECTIVE );
        return authorizationManager.authorize( resourceRef, sessionInfo.getIdentity() );
    }

    private void setupBreadCrumbs( KieProject project ) {
        breadcrumbs.setupAuthoringBreadCrumbsForProject( project.getProjectName() );
    }

    private void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( ts.getTranslation( LibraryConstants.Projected_Created ) ) );
    }

    private void openProject( KieProject project ) {
        placeManager.goTo( LibraryPlaces.AUTHORING );
        libraryContextSwitchEvent
                .fire( new LibraryContextSwitchEvent( LibraryContextSwitchEvent.EventType.PROJECT_SELECTED,
                                                      project.getIdentifier() ) );
    }


    @PostConstruct
    public void setup() {
        view.init( this );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation( LibraryConstants.NewProjectScreen );
    }

    @WorkbenchPartView
    public UberElement<NewProjectScreen> getView() {
        return view;
    }

}
