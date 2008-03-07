/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package de.wieger.smalltalk.eclipse.ui;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.DLTKColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class SmalltalkPreferenceConstants {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    public static final String SMALLTALK_STRING  = DLTKColorConstants.DLTK_STRING;
    public static final String SMALLTALK_COMMENT = DLTKColorConstants.DLTK_MULTI_LINE_COMMENT;
    public static final String SMALLTALK_KEYWORD = DLTKColorConstants.DLTK_KEYWORD;
    public static final String SMALLTALK_VARIABLE = "Smalltalk_variable";

    
    
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    public static void initializeDefaultValues(IPreferenceStore store) {
        PreferenceConstants.initializeDefaultValues(store);

        PreferenceConverter.setDefault(store, SMALLTALK_STRING, new RGB(42, 0, 255));
        PreferenceConverter.setDefault(store, SMALLTALK_COMMENT, new RGB(63, 127, 95));
        
        PreferenceConverter.setDefault(store, SMALLTALK_KEYWORD, new RGB(127, 0, 85));
        store.setDefault(SMALLTALK_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
        
        PreferenceConverter.setDefault(store, SMALLTALK_VARIABLE, new RGB(127, 0, 85));
    }
}
