package de.wieger.smalltalk.eclipse.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;

import de.wieger.smalltalk.eclipse.ui.editor.SmalltalkEditor;


public class SmalltalkLanguageToolkit extends AbstractLanguageToolkit {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final String[] LANGUAGE_FILE_EXTENSIONS = {"st"};
    
    
    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    private static final IDLTKLanguageToolkit sfSmalltalkLanguageKit = new SmalltalkLanguageToolkit();

    
    
    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    public static IDLTKLanguageToolkit getDefault() {
        return sfSmalltalkLanguageKit ;
    }

    
    
    //--------------------------------------------------------------------------  
    // LanguageToolKit methods (mandatory implementation)
    //--------------------------------------------------------------------------

    public String[] getLanguageFileExtensions() {
        return LANGUAGE_FILE_EXTENSIONS;
    }

    public String getLanguageName() {
        return "Smalltalk";
    }

    public String getNatureId() {
        return SmalltalkNature.NATURE_ID;
    }

    public IStatus validateSourceModule(IModelElement pParent, String pName) {
        return validateSourceModule(pName);
    }

    protected String getCorePluginID() {
        return SmalltalkPlugin.PLUGIN_ID;
    }

    
    //--------------------------------------------------------------------------  
    // LanguageToolKit methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String getEditorID(Object inputElement) {
        return SmalltalkEditor.EDITOR_ID;
    }
}
