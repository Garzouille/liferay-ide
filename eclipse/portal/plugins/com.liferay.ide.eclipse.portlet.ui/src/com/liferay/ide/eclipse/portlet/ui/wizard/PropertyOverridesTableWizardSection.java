/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.wizard;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.ui.wizard.StringArrayTableWizardSection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class PropertyOverridesTableWizardSection extends StringArrayTableWizardSection {

	public class AddPropertyOverrideDialog extends AddStringArrayDialog {

		protected String[] buttonLabels;

		public AddPropertyOverrideDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels) {

			super(shell, windowTitle, labelsForTextField);

			setShellStyle(getShellStyle() | SWT.RESIZE);

			this.buttonLabels = buttonLabels;

			setWidthHint(450);
		}

		@Override
		protected Text createField(Composite parent, final int index) {
			Label label = new Label(parent, SWT.LEFT);
			label.setText(labelsForTextField[index]);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

			// Composite composite = new Composite(parent, SWT.NONE);
			// GridData data = new GridData(GridData.FILL_HORIZONTAL);
			// composite.setLayoutData(data);
			// composite.setLayout(new GridLayout(2, false));

			final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			// data.widthHint = 200;

			text.setLayoutData(data);

			if (buttonLabels[index] != null) {
				Composite buttonComposite = new Composite(parent, SWT.NONE);

				String[] buttonLbls = buttonLabels[index].split(",");

				GridLayout gl = new GridLayout(buttonLbls.length, true);
				gl.marginWidth = 0;
				gl.horizontalSpacing = 1;

				buttonComposite.setLayout(gl);

				for (final String lbl : buttonLbls) {
					Button button = new Button(buttonComposite, SWT.PUSH);
					button.setText(lbl);
					button.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							handleArrayDialogButtonSelected(index, lbl, text);
						}

					});
				}
			}

			return text;
		}

		protected void handleArrayDialogButtonSelected(int index, String label, Text text) {
			if (index == 0) { // select event
				handleSelectPropertyButton(text);
			}
		}

		protected void handleSelectPropertyButton(Text text) {
			String[] hookProperties = new String[] {};

			ILiferayRuntime runtime;

			try {
				runtime = ServerUtil.getLiferayRuntime(project);

				hookProperties = runtime.getSupportedHookProperties();
			}
			catch (CoreException e) {
				PortletUIPlugin.logError(e);
			}

			PropertiesFilteredDialog dialog = new PropertiesFilteredDialog(getParentShell());
			dialog.setTitle("Property selection");
			dialog.setMessage("Please select a property");
			dialog.setInput(hookProperties);

			if (dialog.open() == Window.OK) {
				Object[] selected = dialog.getResult();

				text.setText(selected[0].toString());
			}
		}

	}

	protected String[] buttonLabels;

	protected IProject project;

	public PropertyOverridesTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles, fieldLabels, labelProviderImage, model, propertyName);

		this.buttonLabels = new String[] {
			"Select...", null
		};
	}


	public void setProject(IProject project) {
		this.project = project;
	}

	@Override
	protected void handleAddButtonSelected() {
		AddPropertyOverrideDialog dialog =
			new AddPropertyOverrideDialog(getShell(), dialogTitle, fieldLabels, buttonLabels);

		if (dialog.open() == Window.OK) {
			String[] stringArray = dialog.getStringArray();

			addStringArray(stringArray);
		}
	}

}
