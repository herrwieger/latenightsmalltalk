/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package de.wieger.smalltalk.eclipse.ui.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class SmalltalkWhitespaceDetector implements IWhitespaceDetector {
	public boolean isWhitespace(char pCharacter) {
		return Character.isWhitespace(pCharacter);
	}
}
