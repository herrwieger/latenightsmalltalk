/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package de.wieger.smalltalk.eclipse.ui;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.IDocument;

import de.wieger.smalltalk.eclipse.core.SmalltalkPartitionScanner;
import de.wieger.smalltalk.eclipse.core.SmalltalkPlugin;



public class SmalltalkDocumentSetupParticipant implements IDocumentSetupParticipant {
   public void setup(IDocument document) {
        ScriptTextTools tools = SmalltalkPlugin.getDefault().getTextTools();
        tools.setupDocumentPartitioner(document, SmalltalkPartitionScanner.SMALLTALK_PARTITIONING);
    }
}
