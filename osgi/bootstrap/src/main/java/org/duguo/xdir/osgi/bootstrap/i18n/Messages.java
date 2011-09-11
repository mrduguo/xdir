package org.duguo.xdir.osgi.bootstrap.i18n;

public class Messages
{
    public static String ERROR_XDIR_BUNDLE_DUPLICATED_BUNDLE        = "<FPBTR0001E> Bundle [{0}] already installed from [{1}], cannot install from new location [{2}]";
    public static String ERROR_XDIR_BUNDLE_JAR_HEADER_FAILED        = "<FPBTR0002E> Failed to retrive headers from jar file [{0}]";
    public static String ERROR_XDIR_BUNDLE_JAR_INVALID_JAR          = "<FPBTR0012E> Invalid bundle jar file [{0}], no Bundle-SymbolicName header found";
    public static String ERROR_XDIR_BUNDLE_HOT_DEPLOY_FAILED        = "<FPBTR0018E> Hot deploy bundles failed [{0}]";
    public static String ERROR_XDIR_CMD_INIT_FAILED                 = "<FPBTC0003E> Failed to initialise command class [{0}] with default constructor";
    public static String ERROR_XDIR_CMD_RESTART_FAILED              = "<FPBTC0004E> Server restart failed";
    public static String ERROR_XDIR_CMD_STOP_SEND_FAILED            = "<FPBTC0005E> Failed to send stop request, server may already stopped";
    public static String ERROR_XDIR_CMD_STOP_TIMEOUT                = "<FPBTC0006E> Wait server to stop timeout, server may already stopped";
    public static String ERROR_XDIR_CMD_UNKNOWN_COMMAND             = "<FPBTC0007E> Unknown command [{0}] parsed from command line args [{1}]";
    public static String ERROR_XDIR_CONF_LOAD_FILE_FAILED           = "<FPBTC0008E> Failed to load configuration file [{0}]";
    public static String ERROR_XDIR_CONF_LOAD_MESSAGES_FAILED       = "<FPBTC0009E> Failed to load messages";
    public static String ERROR_XDIR_EVENT_FRAMEWORK_UNKNOWN         = "<FPBTE0003E> Unknown framework event [{0}]";
    public static String ERROR_XDIR_EVENT_NO_CODE                   = "<FPBTE0002E> Framework event error message: {0}";
    public static String ERROR_XDIR_EVENT_TRIGGER_FAILED            = "<FPBTE0001E> Failed to trigger event [{0}]";
    public static String ERROR_XDIR_EVENT_DESKTOP_FAILED            = "<FPBTE0002E> Failed active desktop mode on port [{0}]";
    public static String ERROR_XDIR_FILE_FAIL_RESOLVE_XDIRHOME      = "<FPBTF0005E> Failed to resolve xdir.dir.home, you may set by -Dxdir.dir.home=YourXDirDistHomePath";
    public static String ERROR_XDIR_FILE_FOLDER_IS_NOT_FOLDER       = "<FPBTF0006E> Required folder [{0}] is not a folder";
    public static String ERROR_XDIR_FILE_REQIRED_FILE_NOT_EXIST     = "<FPBTF0007E> Required file [{0}] doesn't exist";
    public static String ERROR_XDIR_FILE_REQIRED_FOLDER_NOT_EXIST   = "<FPBTF0008E> Required folder [{0}] doesn't exist";
    public static String ERROR_XDIR_FILE_REQIRED_IS_NOT_FILE        = "<FPBTF0009E> Required file [{0}] is not a file";
    public static String ERROR_XDIR_FILE_WRITE_FAILED               = "<FPBTF0002E> Failed to write file [{0}]";
    public static String ERROR_XDIR_MAIN_UNKNOWN_EXCEPTION          = "<FPBTM0001E> Unknown exception [{0}], please report to development team";
    public static String ERROR_XDIR_RUNTIME_BUNDLE_START_TIMEOUT    = "<FPBTR0005E> Wait for bundle [{0}:{1}:{2}] start timeout";
    public static String ERROR_XDIR_RUNTIME_BUNDLE_TIMEOUT          = "<FPBTR0045E> Failed to start bundle [{0}:{1}]";
    public static String ERROR_XDIR_RUNTIME_BUNDLE_FAILED           = "<FPBTR0046E> Failed to lazy start bundle [{0}:{1}]";
    public static String ERROR_XDIR_RUNTIME_FACTORY_INIT_FAILED     = "<FPBTR0022E> Failed to init OSGi framework factory [{0}]";
    public static String ERROR_XDIR_RUNTIME_FACTORY_NOT_FOUND       = "<FPBTR0011E> Failed to retrive OSGi framework factory class name from [{0}]";
    public static String ERROR_XDIR_RUNTIME_FRAGMENT_FAILED         = "<FPBTR0007E> Failed to attach fragment bundle [{0}:{1}]";
    public static String ERROR_XDIR_RUNTIME_FRAMEWORK_FAILED        = "<FPBTR0006E> Framework start failed";
    public static String ERROR_XDIR_RUNTIME_GROUP_FAILED            = "<FPBTR0009E> Bundle group [{0}] start failed";
    public static String ERROR_XDIR_RUNTIME_PROVIDER_INIT_FAILED    = "<FPBTR0004E> Failed to init framework provider [{0}]";
    public static String ERROR_XDIR_RUNTIME_RUN_FAILED              = "<FPBTR0008E> Failed to run server";
    public static String ERROR_XDIR_RUNTIME_SHUTDOWN_HOOK_FAILED    = "<FPBTR0010E> ShutdownHook execution failed";
    public static String ERROR_XDIR_RUNTIME_STOP_FAILED             = "<FPBTR0019E> Failed to stop server";
    public static String ERROR_XDIR_RUNTIME_STOP_TIMEOUT            = "<FPBTR0012E> Stop server timeout";
    public static String ERROR_XDIR_RUNTIME_SHUTDOWN_HOOK_TIMEOUT   = "<FPBTR0013E> ShutdownHook timeout, will stop straight away";
    public static String ERROR_XDIR_RUNTIME_SYSTEM_FAILED           = "<FPBTR0017E> System bundles start failed";
    
    public static String WARN_XDIR_BUNDLE_DUPLICATED_BUNDLE         = "<FPBTR0008W> Bundle [{0}] already installed from [{1}], will ignore the bundle from new location [{2}]";
    public static String WARN_XDIR_CMD_IGNORE_EXTRA_COMMAND         = "<FPBTC0002W> Command already parsed as [{0}] and ignore override value [{1}]";
    public static String WARN_XDIR_CMD_RESTART_STOPPED              = "<FPBTC0006W> Server is not running and ignore the restart command";
    public static String WARN_XDIR_CMD_STOP_DISABLED                = "<FPBTC0005W> Server is running but stop command was disabled for security reason";
    public static String WARN_XDIR_CMD_UNSUPPORTED_FLAG             = "<FPBTC0041W> Unsupported command line flag [{0}]\nCurrent supported flags: -debug -console -clean";
    public static String WARN_XDIR_CONF_FILE_NOT_FOUND              = "<FPBTC0003W> Configuration file [{0}] not found and will using default values";
    public static String WARN_XDIR_CONF_INVALID_FORMAT              = "<FPBTC0004W> Invalid configuration for [{0}] with value [{1}], ignore and using default value [{2}]";
    public static String WARN_XDIR_CONSOLE_BUNDLE_NOT_FOUND         = "<FPBTC0013W> Console bundles missing and console disabled";
    public static String WARN_XDIR_EVENT_NO_CODE                    = "<FPBTE0003W> Framework event warn message: {0}";
    public static String WARN_XDIR_MAIN_SERVER_IGNORE_RESTART       = "<FPBTM0002W> Server restart disabled due to runtime return none zero status code [{0}]";
    public static String WARN_XDIR_MAIN_SERVER_RUNNING_IGNORE_START = "<FPBTM0031W> Server is running and ignore start command";
    public static String WARN_XDIR_MAIN_UNKNOWN_EXCEPTION           = "<FPBTM0021W> Unknown exception [{0}], please report to development team";
    public static String WARN_XDIR_RUNTIME_FAILED_WITH_CONSOLE      = "<FPBTR0013W> Server start failed but console is enabled, so we keep server running for investigation";
    public static String WARN_XDIR_RUNTIME_PROVIDER_DEFAULT         = "<FPBTR0011W> Framework provider [{0}] is not tested with XDir Platform";
    public static String WARN_XDIR_RUNTIME_USER_FAILED              = "<FPBTR0002W> There are some error during user bundles start, server continue to run";

    public static String INFO_XDIR_BUNDLES_GROUP_STARTED            = "<FPBTB0001I> Bundle group [{0}:{1}] started";
    public static String INFO_XDIR_BUNDLES_SUMMARY                  = "<FPBTE0005I> Server started with {0} bundles in {1} seconds\nWelcome to XDir Platform";
    public static String INFO_XDIR_CMD_RESTART_SUCCESS              = "<FPBTC0005I> Server will start again";
    public static String INFO_XDIR_EXTENDER_STARTED                 = "<FPBTE0025I> XDir extender started";
    public static String INFO_XDIR_CMD_STATUS_RUNNING               = "<FPBTC0004I> Server is running";
    public static String INFO_XDIR_CMD_STATUS_STOPPED               = "<FPBTC0003I> Server stopped";
    public static String INFO_XDIR_CMD_STOP_ALREADY                 = "<FPBTC0009I> Server stopped already";
    public static String INFO_XDIR_CMD_STOP_SUCCESS                 = "<FPBTC0001I> Server stopped successfully";
    public static String INFO_XDIR_CMD_LOG_LOCATION                 = "<FPBTC0021I> You may see more details in log file:\n{0}";
    public static String INFO_XDIR_EVENT_NO_CODE                    = "<FPBTE0003I> Framework event message: {0}";
    public static String INFO_XDIR_EVENT_DESKTOP_MODE_ENABLED       = "<FPBTE0018I> Desktop mode activated, listen on port [{0}]";
    public static String INFO_XDIR_EVENT_RUNTIME_STARTED            = "<FPBTE0001I> OSGi runtime started";
    public static String INFO_XDIR_EVENT_RUNTIME_STOPPED            = "<FPBTE0007I> Server stopped=====\nGoodBye! Thanks for using http://xdirplatform.org";
    public static String INFO_XDIR_EVENT_SYSTEM_BUNDLES_STARTED     = "<FPBTE0006I> System bundles started";
    public static String INFO_XDIR_EVENT_USER_BUNDLES_STARTED       = "<FPBTE0013I> User bundles started";
    public static String INFO_XDIR_RUNTIME_PERFORM_START            = "<FPBTR0001I> Starting server";
    public static String INFO_XDIR_RUNTIME_PERFORM_RESTART          = "<FPBTR0002I> Restarting server";
    public static String INFO_XDIR_RUNTIME_PERFORM_STOP             = "<FPBTR0008I> Stopping server";
    
    public static String STRING_LOGGER_LEVEL_DEBUG = "DEBUG";
    public static String STRING_LOGGER_LEVEL_ERROR = "ERROR";
    public static String STRING_LOGGER_LEVEL_INFO  = "INFO";
    public static String STRING_LOGGER_LEVEL_STERR = "STERR";
    public static String STRING_LOGGER_LEVEL_STOUT = "STOUT";
    public static String STRING_LOGGER_LEVEL_WARN  = "WARN";
    public static String STRING_LOGGER_LEVEL_UNKNOWN = "N/A";
    
    
    public static String format(String... msgs){
        return MessagesInitialiser.format( msgs );
    }

    public static String parseCombinedMessage(String msg){
        return MessagesInitialiser.parseCombinedMessage( Messages.class,msg);
    }
}
