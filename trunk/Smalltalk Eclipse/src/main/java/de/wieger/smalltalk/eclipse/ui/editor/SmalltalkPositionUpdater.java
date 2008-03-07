package de.wieger.smalltalk.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.SemanticHighlightingManager.HighlightedPosition;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.Highlighting;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.PositionUpdater;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlightingPresenter;

import antlr.Token;
import de.wieger.smalltalk.eclipse.core.SmalltalkSourceParser;
import de.wieger.smalltalk.parser.ClassReader;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;


public class SmalltalkPositionUpdater extends PositionUpdater {

    @Override
    public UpdateResult reconcile(ISourceModule pAst, SemanticHighlightingPresenter pPresenter,
            Highlighting[] pHighlightings, List pCurrentPositions) {
        ArrayList<HighlightedPosition> addedPositions = new ArrayList<HighlightedPosition>();
        ArrayList<HighlightedPosition> removedPositions = new ArrayList<HighlightedPosition>();

        try {
            SmalltalkSourceParser   parser = new SmalltalkSourceParser();
            ClassReader reader = parser.parse(pAst.getSourceAsCharArray());
            for (ClassDescription classDescription : reader.getParsedClassDescriptions()) {
                for (MethodDescription methodDescription : classDescription.getMethodDescriptions()) {
                    for (Token varNameToken : methodDescription.getVariableNameTokens()) {
                        addedPositions.add(new HighlightedPosition(methodDescription.getStart(varNameToken), varNameToken.getText().length(), pHighlightings[0], true));
                    }
                }
            }
        } catch (ModelException ex) {
            ex.printStackTrace();
        }
        return new UpdateResult(addedPositions, removedPositions);
    }

}
