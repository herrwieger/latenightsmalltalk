/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package de.wieger.smalltalk.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

import de.wieger.smalltalk.eclipse.core.SmalltalkPartitionScanner;
import de.wieger.smalltalk.eclipse.ui.SmalltalkOutlineInformationControl;

public class SmalltalkSourceViewerConfiguration extends ScriptSourceViewerConfiguration {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private List<AbstractScriptScanner>      fScanners;
    private SmalltalkKeywordScanner          fKeywordScanner;
    private SmalltalkStringScanner           fStringScanner;
    private SmalltalkCommentScanner          fCommentScanner;
    

    
    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public SmalltalkSourceViewerConfiguration(IColorManager pColorManager, IPreferenceStore pPreferenceStore,
            ITextEditor pEditor, String pPartitioning) {
        super(pColorManager, pPreferenceStore, pEditor, pPartitioning);
    }



    // --------------------------------------------------------------------------
    // ScriptSourceViewerConfiguration methods (mandatory implementation)
    // --------------------------------------------------------------------------

    protected IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer pSourceViewer,
            final String pCommandId) {
        return new IInformationControlCreator() {

            public IInformationControl createInformationControl(Shell parent) {
                int shellStyle = SWT.RESIZE;
                int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
                return new SmalltalkOutlineInformationControl(parent, shellStyle, treeStyle, pCommandId);
            }
        };
    }

    
    protected void initializeScanners() {
        fScanners = new ArrayList<AbstractScriptScanner>();
        
        fKeywordScanner    = new SmalltalkKeywordScanner(getColorManager(), fPreferenceStore);
        fScanners.add(fKeywordScanner);
        
        fStringScanner              = new SmalltalkStringScanner(getColorManager(), fPreferenceStore);
        fScanners.add(fStringScanner);
        
        fCommentScanner             = new SmalltalkCommentScanner(getColorManager(), fPreferenceStore);
        fScanners.add(fCommentScanner);
    }

    public boolean affectsTextPresentation(PropertyChangeEvent pEvent) {
        for (AbstractScriptScanner scanner : fScanners) {
            if (scanner.affectsBehavior(pEvent)) {
                return true;
            }
        }
        return false;
    }

    public void handlePropertyChangeEvent(PropertyChangeEvent pEvent) {
        for (AbstractScriptScanner scanner : fScanners) {
            if(scanner.affectsBehavior(pEvent)) {
                scanner.adaptToPreferenceChange(pEvent);
            }
        }
    }


    
    // --------------------------------------------------------------------------
    // ScriptSourceViewerConfiguration methods (overridden)
    // --------------------------------------------------------------------------

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new ScriptPresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(fKeywordScanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
        dr = new DefaultDamagerRepairer(fStringScanner);
        reconciler.setDamager(dr, SmalltalkPartitionScanner.SMALLTALK_STRING);
        reconciler.setRepairer(dr, SmalltalkPartitionScanner.SMALLTALK_STRING);

        dr = new DefaultDamagerRepairer(fCommentScanner);
        reconciler.setDamager(dr, SmalltalkPartitionScanner.SMALLTALK_COMMENT);
        reconciler.setRepairer(dr, SmalltalkPartitionScanner.SMALLTALK_COMMENT);

        return reconciler;
    }
    
    @Override
    protected void initializeQuickOutlineContexts(InformationPresenter presenter,
            IInformationProvider provider) {
        presenter.setInformationProvider(provider, SmalltalkPartitionScanner.SMALLTALK_COMMENT);
        presenter.setInformationProvider(provider, SmalltalkPartitionScanner.SMALLTALK_STRING);
        presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
    }
    
    
    
    //--------------------------------------------------------------------------  
    // SourceViewerConfiguration methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return SmalltalkPartitionScanner.SMALLTALK_PARTITION_TYPES;
    }
}
