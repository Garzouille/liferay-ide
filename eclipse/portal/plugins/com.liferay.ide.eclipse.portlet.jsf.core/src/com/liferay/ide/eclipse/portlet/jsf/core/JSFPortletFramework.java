/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.eclipse.portlet.jsf.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.AbstractPortletFramework;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.jsf.core.IJSFCoreConstants;
import org.eclipse.jst.jsf.core.internal.project.facet.IJSFFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class JSFPortletFramework extends AbstractPortletFramework implements IJSFFacetInstallDataModelProperties {

	public static final String JSF_FACET_SUPPORTED_VERSION = "2.0";

	public JSFPortletFramework() {
		super();
	}

	public IStatus configureNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		IProjectFacetVersion jsfFacetVersion = getJSFProjectFacet(facetedProject);
		IProjectFacet jsfFacet = JSFCorePlugin.JSF_FACET;

		if (jsfFacetVersion == null) {

			jsfFacetVersion = jsfFacet.getVersion(JSF_FACET_SUPPORTED_VERSION);
			facetedProject.addProjectFacet(jsfFacetVersion);
		}

		Action action = facetedProject.getProjectFacetAction(jsfFacet);
		IDataModel jsfFacetDataModel = (IDataModel) action.getConfig();

		jsfFacetDataModel.setProperty(SERVLET_URL_PATTERNS, null);
		jsfFacetDataModel.setProperty(WEBCONTENT_DIR, "docroot");

		LibraryInstallDelegate libraryInstallDelegate =
			(LibraryInstallDelegate) jsfFacetDataModel.getProperty(LIBRARY_PROVIDER_DELEGATE);

		List<ILibraryProvider> providers = libraryInstallDelegate.getLibraryProviders();

		ILibraryProvider noOpProvider = null;

		for (ILibraryProvider provider : providers) {
			if (provider.getId().equals("jsf-no-op-library-provider")) {
				noOpProvider = provider;
				break;
			}
		}

		if (noOpProvider != null) {
			libraryInstallDelegate.setLibraryProvider(noOpProvider);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IProjectFacet[] getFacets() {
		return new IProjectFacet[] { JSFCorePlugin.JSF_FACET };
	}

	@Override
	public IStatus getUnsupportedSDKErrorMsg() {
		return JSFCorePlugin.createErrorStatus( "JSF framework requires SDK version " + requiredSDKVersion +
			". Download a compatible SDK at www.portletfaces.org/projects/portletfaces-bridge/liferay-ide" );
	}


	@Override
	public IStatus postProjectCreated(IDataModel dataModel, IFacetedProject facetedProject) {
		/*
		 * we need to copy the original web.xml from the project template because of bugs in the JSF facet installer
		 * will overwrite our web.xml that comes with in the template
		 */

		SDK sdk = SDKUtil.getSDK( facetedProject.getProject() );

		if (sdk == null) {
			return JSFCorePlugin.createErrorStatus("Could not get SDK from newly created project.");
		}

		try {
			File originalWebXmlFile =
				sdk.getLocation().append("tools/portlet_jsf_tmpl/docroot/WEB-INF/web.xml").toFile();

			IFolder docroot = CoreUtil.getDocroot( facetedProject.getProject() );

			docroot.getFile("WEB-INF/web.xml").setContents(
				new FileInputStream(originalWebXmlFile), IResource.FORCE, null);
		}
		catch (Exception e) {
			return JSFCorePlugin.createErrorStatus("Could not copy original web.xml from JSF template in SDK.", e);
		}

		return Status.OK_STATUS;
	}

	protected IProjectFacetVersion getJSFProjectFacet(IFacetedProjectWorkingCopy project) {
		Set<IProjectFacetVersion> facets = project.getProjectFacets();

		for (IProjectFacetVersion facet : facets) {
			if (facet.getProjectFacet().getId().equals(IJSFCoreConstants.JSF_CORE_FACET_ID)) {
				return facet;
			}
		}

		return null;
	}

}
