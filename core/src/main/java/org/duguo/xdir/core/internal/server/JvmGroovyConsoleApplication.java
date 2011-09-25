package org.duguo.xdir.core.internal.server;


import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;


public class JvmGroovyConsoleApplication extends DefaultAdminApplication
{
    private static final Logger logger = LoggerFactory.getLogger( JvmGroovyConsoleApplication.class );

    private GroovyShell consoleShell;
    private List<Object[]> consoleOutput;
    private Binding consoleBinding;

    public void openConsole( ModelImpl model ) throws Exception
    {
        if ( consoleShell == null )
        {
            consoleBinding = new Binding();
            consoleOutput = new ArrayList<Object[]>();
            consoleShell = new GroovyShell(this.getClass().getClassLoader(), consoleBinding);
            consoleOutput.add( new Object[]{ "command",model.getUserId(), "openconsole", Calendar.getInstance().getTimeInMillis() } );
            consoleOutput.add( new String[]{ "stout","Groovy Shell ("+InvokerHelper.getVersion()+", JVM: "+System.getProperty("java.version")+")"} );
        }
    }


    public List<Object[]> getConsoleOutput()
    {
        return consoleOutput;
    }


    public synchronized void closeConsole() throws Exception
    {
        consoleShell = null;
        consoleOutput = null;
        consoleBinding = null;
    }


    public synchronized void executeCommand( ModelImpl model, String command ) throws Exception
    {
        if ( logger.isDebugEnabled() )
            logger.debug( "starting execute command [{}]", command );
        if ( consoleShell != null )
        {
            consoleOutput.add( new Object[]{ "command",model.getUserId(), command, Calendar.getInstance().getTimeInMillis() } );
            if ( command.trim().length() > 0 )
            {
                if ( command.equals( "exit" ) )
                {
                    closeConsole();
                    return;
                }
                else  if ( command.equals( "clear" ))
                {
                    consoleOutput.clear();
                    return;
                }else{
                    safelyRunCommand( model, command );
                }

                if ( logger.isDebugEnabled() )
                    logger.debug( "execute command [{}] successfully", command );
            }
        }
        else
        {
            model.setStatus( "console already closed" );
            if ( logger.isDebugEnabled() )
                logger.debug( "ignore execute command [{}]: console already closed", command );
        }
    }


    protected void safelyRunCommand( ModelImpl model, String command )
    {
        Throwable commandException=null;
        Object resultObject=null;
        SystemPrintStream tempSystemOut=new SystemPrintStream();
        SystemPrintStream tempSystemErr=new SystemPrintStream();
        PrintStream realSystemOut = System.out;
        PrintStream realSystemErr = System.err;
        try{
            System.setOut( tempSystemOut );
            System.setErr( tempSystemErr );
            
            consoleBinding.setVariable( "model", model );
            resultObject=consoleShell.evaluate( command );
            String sterrMsg=tempSystemErr.getOutput();
            if(sterrMsg.length()>0){
                consoleOutput.add( new Object[]{ "sterr", sterrMsg} );
            }
            String stoutMsg=tempSystemOut.getOutput();
            if(stoutMsg.length()>0){
                consoleOutput.add( new Object[]{ "stout", stoutMsg} );
            }
        }catch(Throwable ex){
            commandException=ex;
        }finally{
            System.setOut( realSystemOut );
            System.setErr( realSystemErr );
            consoleBinding.setVariable( "model", null );
        }
        if(resultObject!=null){
            consoleOutput.add( new Object[]{ "result", resultObject.toString()} );
            model.put( "result", resultObject);
        }
        
        if(commandException!=null){
            consoleOutput.add( new Object[]{ "sterr", commandException.getMessage() } );
            logger.warn( "execute groovy command failed", commandException);                    
        }
    }

}
