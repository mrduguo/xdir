package org.duguo.xdir.spi.util.bean;


import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.io.File;
import java.util.Dictionary;
import java.util.List;


public class BundleUtil
{

    public static void scanSingleBundle(String bundleFileNameBase, File baseFolder,List<String> bundleFiles) throws Exception
    {
        if ( baseFolder.exists() )
        {
            for ( File child : baseFolder.listFiles() )
            {
                if ( child.isFile() )
                {
                    String fileName = child.getName();
                    if ( fileName.endsWith( ".jar" ) && fileName.startsWith( bundleFileNameBase + "-" ) )
                    {
                        bundleFiles.add(child.getPath() );
                    }
                }
                else
                {
                    scanSingleBundle(bundleFileNameBase, child ,bundleFiles);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static boolean isFragment( Bundle bundle )
    {
        Dictionary<String, String> headerMap = bundle.getHeaders();
        return headerMap.get( Constants.FRAGMENT_HOST ) != null;
    }

    public static String displayBundleStatus( int status )
    {
        switch ( status )
        {
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            case Bundle.ACTIVE:
                return "ACTIVE";
            default:
                break;
        }
        return String.valueOf( status );
    }
}
