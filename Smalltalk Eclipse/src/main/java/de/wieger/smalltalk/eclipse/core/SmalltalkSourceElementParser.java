package de.wieger.smalltalk.eclipse.core;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.ISourceElementParser;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;


public class SmalltalkSourceElementParser implements ISourceElementParser {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private ISourceElementRequestor fRequestor;
    private IProblemReporter        fReporter;


    
    //--------------------------------------------------------------------------  
    // ISourceElementParser methods
    //--------------------------------------------------------------------------

    public ModuleDeclaration parseSourceModule(
            char[]              pContents,
            ISourceModuleInfo   pAstCashe,
            char[]              pFilename) {
        String content = new String(pContents);
        
        SmalltalkSourceParser sourceParser = new SmalltalkSourceParser(fReporter);
        ModuleDeclaration moduleDeclaration = sourceParser.parse(content);
        
        try {
            moduleDeclaration.traverse(new SmalltalkSourceElementRequestor(fRequestor));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return moduleDeclaration;
    }

    public void setRequestor(ISourceElementRequestor pRequestor) {
        fRequestor = pRequestor;
    }

    public void setReporter(IProblemReporter pReporter) {
        fReporter = pReporter;
    }
}
