package smalltalk;



public interface IBlock {
    Object argCount();

    Object value();

    Object value(Object pParam);

    Object $returnFromException(Object pResult);
}
