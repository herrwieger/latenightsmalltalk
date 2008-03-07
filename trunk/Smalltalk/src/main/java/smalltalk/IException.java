package smalltalk;



public interface IException {
    // --------------------------------------------------------------------------
    // java methods
    // --------------------------------------------------------------------------

    boolean $shouldReturn();

    boolean $shouldPass();

    boolean $shouldRetry();

    boolean $shouldResume();

    Object getResult();

    void $clear();


    // --------------------------------------------------------------------------
    // smalltalk methods
    // --------------------------------------------------------------------------

    Object defaultAction();
}
