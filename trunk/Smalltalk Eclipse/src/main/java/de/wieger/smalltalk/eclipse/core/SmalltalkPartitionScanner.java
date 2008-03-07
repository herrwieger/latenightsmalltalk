package de.wieger.smalltalk.eclipse.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;



public class SmalltalkPartitionScanner extends RuleBasedPartitionScanner {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    public static final String SMALLTALK_PARTITIONING = "__smalltalk_partitioning";
    
    public static final String SMALLTALK_COMMENT   = "__smalltalk_comment";
    public static final String SMALLTALK_STRING    = "__smalltalk_string";
    
    public static final String[] SMALLTALK_PARTITION_TYPES = {
        IDocument.DEFAULT_CONTENT_TYPE, 
        
        SMALLTALK_COMMENT,
        SMALLTALK_STRING,
    };

    public final static String[] LEGAL_CONTENT_TYPES = new String[] {
        IDocument.DEFAULT_CONTENT_TYPE, 
        
        SMALLTALK_COMMENT,
        SMALLTALK_STRING,
    };

    
    
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private Token defaultToken;
    private Token string;
    private Token comment;
    private Map<String, Token> fTokenByKey = new HashMap<String, Token>();
    

    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    
    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public SmalltalkPartitionScanner() {
        super();
        
        defaultToken = new Token (IDocument.DEFAULT_CONTENT_TYPE);        
        setDefaultReturnToken(defaultToken);
        
        comment         = createAndRegisterToken(SMALLTALK_COMMENT);
        string          = createAndRegisterToken(SMALLTALK_STRING);

        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        rules.add(new MultiLineRule("\"", "\"", comment));

        rules.add(new MultiLineRule("\'", "\'", string));
        
        rules.add(new SmalltalkCodeRule(defaultToken));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }

    private Token createAndRegisterToken(String tokenType) {
        Token token = new Token (tokenType);
        fTokenByKey.put(tokenType, token);
        return token;
    }

    protected Token getToken(String pKey) {
        return fTokenByKey.get(pKey);
    }
}
