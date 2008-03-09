package de.wieger.smalltalk.eclipse.core;

import java.io.CharArrayReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.ISourceParser;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.parser.ClassReader;
import de.wieger.smalltalk.parser.ClassReaderLexer;
import de.wieger.smalltalk.parser.ErrorListener;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.DeclaredVariable;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.ClassDescription.VariabilityType;
import de.wieger.smalltalk.universe.ClassDescriptionManager;


public class SmalltalkSourceParser implements ISourceParser, ClassDescriptionManager, ErrorListener {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private IProblemReporter              fProblemReporter;

    private Map<String, ClassDescription> fClassDescriptionsByName = new HashMap<String, ClassDescription>();
    
    

    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkSourceParser() {
        fProblemReporter = new IProblemReporter() {
            public IMarker reportProblem(IProblem pProblem) throws CoreException {
                return null;
            }
        };
    }

    public SmalltalkSourceParser(IProblemReporter pProblemReporter) {
        fProblemReporter = pProblemReporter;
    }

    
    
    //--------------------------------------------------------------------------  
    // SmalltalkSourceParser methods
    //--------------------------------------------------------------------------

    public ModuleDeclaration parse(String pContent) {
        return this.parse(null, pContent.toCharArray(), null);
    }


    @Override
    public ModuleDeclaration parse(char[] pFileName, char[] pSource, IProblemReporter pReporter) {
        ClassReader reader = parse(pSource);
        
        ModuleDeclaration moduleDeclaration = new ModuleDeclaration(0);
        for (ClassDescription classDescription : reader.getParsedClassDescriptions()) {
            TypeDeclaration typeDeclaration = new TypeDeclaration(classDescription.getName(), 0, 0, 0, 0);
            moduleDeclaration.addStatement(typeDeclaration);
            for (DeclaredVariable declaredVariable : classDescription.getInstanceVariables()) {
                typeDeclaration.getStatements().add(new FieldDeclaration(declaredVariable.getName(), 0, 0, 0, 0));                
            }
            for (MethodDescription methodDescription : classDescription.getMethodDescriptions()) {
                MethodDeclaration methodDeclaration = new MethodDeclaration(methodDescription.getSelector(),
                        methodDescription.getNameStart(), methodDescription.getNameEnd(),
                        methodDescription.getStart(), methodDescription.getEnd());
                typeDeclaration.getStatements().add(methodDeclaration);
                for (DeclaredVariable methodArgument : methodDescription.getParameters()) {
                    SimpleReference argumentName = new SimpleReference(0,0, methodArgument.getName());
                    methodDeclaration.addArgument(new Argument(argumentName, 0, null, 0));
                }
            }
        }
        return moduleDeclaration;
    }


    public ClassReader parse(char[] pSource) {
        ClassReaderLexer    lexer       = new ClassReaderLexer(new CharArrayReader(pSource));
        ClassReader         reader      = new ClassReader(lexer);
        reader.setup(this, reader);
        reader.addErrorListener(this);
        try {
            reader.fileIn();
        } catch (RecognitionException ex) {
            ex.printStackTrace();
        } catch (TokenStreamException ex) {
            ex.printStackTrace();
        }
        return reader;
    }

    
    
    //--------------------------------------------------------------------------  
    // ClassDescriptionManager methods (implementation)
    //--------------------------------------------------------------------------

    public ClassDescription getClassDescription(String pName) {
        ClassDescription classDescription = fClassDescriptionsByName.get(pName);
        if (classDescription != null) {
            return classDescription;
        }
        ClassDescription clazzClassDescription = new ClassDescription(this, pName + " class", null, null, VariabilityType.NONE);
        ClassDescription superClassDescription = new ClassDescription(this, "Object", null, null, VariabilityType.NONE);
        classDescription = new ClassDescription(this, pName, superClassDescription, clazzClassDescription, VariabilityType.NONE);
        addClassDescription(clazzClassDescription);
        addClassDescription(classDescription);
        return classDescription;
    }


    public void addClassDescription(ClassDescription pNewClassDescription) {
        fClassDescriptionsByName.put(pNewClassDescription.getName(), pNewClassDescription);
    }

    
    
    //--------------------------------------------------------------------------  
    // ErrorListener methods (implementation)
    //--------------------------------------------------------------------------

    public void error(String pMessage, int pStart, int pEnd) {
        try {
            fProblemReporter.reportProblem(new DefaultProblem("", pMessage, 0, null, 0, pStart, pEnd, 0));
        } catch (CoreException ex) {
            throw new RuntimeException(ex);
        }
    }
}
