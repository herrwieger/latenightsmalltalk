package de.wieger.smalltalk.eclipse.core;

import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.PositionUpdater;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

import de.wieger.smalltalk.eclipse.ui.SmalltalkPreferenceConstants;
import de.wieger.smalltalk.eclipse.ui.editor.SmalltalkPositionUpdater;
import de.wieger.smalltalk.eclipse.ui.editor.SmalltalkSourceViewerConfiguration;



public class SmalltalkTextTools extends ScriptTextTools {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private IPartitionTokenScanner fPartitionScanner      = new SmalltalkPartitionScanner();
    private SemanticHighlighting[] fSemanticHighlightings = new SemanticHighlighting[1];
    
    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    SmalltalkTextTools() {
        super(SmalltalkPartitionScanner.SMALLTALK_PARTITIONING, SmalltalkPartitionScanner.LEGAL_CONTENT_TYPES, true);
        
        fSemanticHighlightings[0] = new SemanticHighlighting() {
            public String getPreferenceKey() {
                return SmalltalkPreferenceConstants.SMALLTALK_VARIABLE;
            }
        };
    }

    
    //--------------------------------------------------------------------------  
    // ScriptTextTools methods (mandatory implementation)
    //--------------------------------------------------------------------------

    public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(IPreferenceStore pPreferenceStore,
            ITextEditor pEditor, String pPartitioning) {
        return new SmalltalkSourceViewerConfiguration(getColorManager(),
                pPreferenceStore, pEditor, pPartitioning);
    }

    public IPartitionTokenScanner getPartitionScanner() {
        return fPartitionScanner;
    }

    public SemanticHighlighting[] getSemanticHighlightings() {
        return fSemanticHighlightings;
    }

    public PositionUpdater getSemanticPositionUpdater() {
        return new SmalltalkPositionUpdater();
    }
}
