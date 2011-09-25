package org.duguo.xdir.core.internal.app;


import org.duguo.xdir.core.internal.model.ModelImpl;


public class ParentAwareApplication extends SimplePathApplication
{
    public int execute( ModelImpl model ) throws Exception
    {
        model.getPathInfo().moveToPreviousPath();
        if(getParent()!=null){
            return getParent().execute( model );
        }else{
            return super.execute( model );
        }
    }    
}
