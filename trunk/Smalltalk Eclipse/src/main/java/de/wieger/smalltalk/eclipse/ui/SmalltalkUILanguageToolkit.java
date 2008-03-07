/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package de.wieger.smalltalk.eclipse.ui;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;

import de.wieger.smalltalk.eclipse.core.SmalltalkLanguageToolkit;
import de.wieger.smalltalk.eclipse.core.SmalltalkPartitionScanner;
import de.wieger.smalltalk.eclipse.core.SmalltalkPlugin;
import de.wieger.smalltalk.eclipse.ui.editor.SimpleSmalltalkSourceViewerConfiguration;
import de.wieger.smalltalk.eclipse.ui.editor.SmalltalkEditor;

public class SmalltalkUILanguageToolkit implements IDLTKUILanguageToolkit {
	private static ScriptElementLabels sInstance = new ScriptElementLabels() {
		public void getElementLabel(IModelElement element, long flags,
				StringBuffer buf) {
			StringBuffer buffer = new StringBuffer(60);
			super.getElementLabel(element, flags, buffer);
			String s = buffer.toString();
			if (s != null && !s.startsWith(element.getElementName())) {
				if (s.indexOf('$') != -1) {
					s = s.replaceAll("\\$", ".");
				}
			}
			buf.append(s);
		}

		protected void getTypeLabel(IType type, long flags, StringBuffer buf) {
			StringBuffer buffer = new StringBuffer(60);
			super.getTypeLabel(type, flags, buffer);
			String s = buffer.toString();
			if (s.indexOf('$') != -1) {
				s = s.replaceAll("\\$", "::");
			}
			buf.append(s);
		}

		protected char getTypeDelimiter() {
			return '$';
		}
	};

	private static SmalltalkUILanguageToolkit sToolkit = null;

	public static IDLTKUILanguageToolkit getInstance() {
		if (sToolkit == null) {
			sToolkit = new SmalltalkUILanguageToolkit();
		}
		return sToolkit;
	}

	public ScriptElementLabels getScriptElementLabels() {
		return sInstance;
	}

	public IPreferenceStore getPreferenceStore() {
		return SmalltalkPlugin.getDefault().getPreferenceStore();
	}

	public IDLTKLanguageToolkit getCoreToolkit() {
		return SmalltalkLanguageToolkit.getDefault();
	}

	public IDialogSettings getDialogSettings() {
		return SmalltalkPlugin.getDefault().getDialogSettings();
	}

	public String getPartitioningId() {
		return SmalltalkPartitionScanner.SMALLTALK_PARTITIONING;
	}

	public String getEditorId(Object inputElement) {
		return SmalltalkEditor.EDITOR_ID;
	}

	public String getInterpreterContainerId() {
		return null;
	}

	public ScriptUILabelProvider createScripUILabelProvider() {
		return null;
	}

	public boolean getProvideMembers(ISourceModule element) {
		return true;
	}

	public ScriptTextTools getTextTools() {
		return SmalltalkPlugin.getDefault().getTextTools();
	}

	public ScriptSourceViewerConfiguration createSourceViwerConfiguration() {
        return new SimpleSmalltalkSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(),
                null, getPartitioningId(), false);
    }

	public String getInterpreterPreferencePage() {
		return null;
	}
}
