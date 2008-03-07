package de.wieger.smalltalk.eclipse.ui.editor;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;

import de.wieger.smalltalk.eclipse.core.SmalltalkLanguageToolkit;
import de.wieger.smalltalk.eclipse.core.SmalltalkPartitionScanner;
import de.wieger.smalltalk.eclipse.core.SmalltalkPlugin;
import de.wieger.smalltalk.eclipse.ui.SmalltalkDocumentSetupParticipant;


public class SmalltalkEditor extends ScriptEditor {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    public static final String EDITOR_ID = "de.wieger.smalltalk.eclipse.SmalltalkEditor";

    
    
    //--------------------------------------------------------------------------  
    // ScriptEditor methods (implementation)
    //--------------------------------------------------------------------------

    protected void connectPartitioningToElement(IEditorInput input,
            IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension = (IDocumentExtension3) document;
            if (extension.getDocumentPartitioner(SmalltalkPartitionScanner.SMALLTALK_PARTITIONING) == null) {
                SmalltalkDocumentSetupParticipant participant = new SmalltalkDocumentSetupParticipant();
                participant.setup(document);
            }
        }
    }
    
    protected FoldingActionGroup createFoldingActionGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    protected ScriptOutlinePage doCreateOutlinePage() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCallHierarchyID() {
        return "org.eclipse.dltk.callhierarchy.view";
    }

    public String getEditorId() {
        return EDITOR_ID;
    }

    protected IFoldingStructureProvider getFoldingStructureProvider() {
        return null;
    }

    public IDLTKLanguageToolkit getLanguageToolkit() {
        return SmalltalkLanguageToolkit.getDefault();
    }

    protected IPreferenceStore getScriptPreferenceStore() {
        return SmalltalkPlugin.getDefault().getPreferenceStore();
    }

    public ScriptTextTools getTextTools() {
        return SmalltalkPlugin.getDefault().getTextTools();
    }
}
