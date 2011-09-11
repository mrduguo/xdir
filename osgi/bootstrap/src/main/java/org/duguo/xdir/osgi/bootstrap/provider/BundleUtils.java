package org.duguo.xdir.osgi.bootstrap.provider;


import java.io.File;
import java.util.Dictionary;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;


public class BundleUtils
{
    
    public static String retriveJarBundleNameAndVersionId(File bundleFile){
        StringBuilder nameAndVersion=new StringBuilder();
        try{
            JarFile jar = new JarFile(bundleFile);
            Manifest mf = jar.getManifest();
            Attributes attr = mf.getMainAttributes();
            
            nameAndVersion.append( attr.getValue(Constants.BUNDLE_SYMBOLICNAME) );
            nameAndVersion.append( ":" );
            nameAndVersion.append(attr.getValue(Constants.BUNDLE_VERSION));
        }catch(Throwable ex){
            throw new RuntimeException(Messages.format( Messages.ERROR_XDIR_BUNDLE_JAR_HEADER_FAILED,bundleFile.getPath()));
        }
        return nameAndVersion.toString();
    }

    public static void scanSingleBundle(String bundleFileNameBase, File baseFolder,List<String> bundleFiles) throws BundleException
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

    public static String parseBundleStatus( int status )
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

    public static String bundleDisplayString( Bundle bundle,String msg )
    {
        StringBuilder sb=new StringBuilder();
        sb.append( "Bundle [");
        sb.append( bundle.getBundleId() );
        sb.append( ":");
        sb.append( bundle.getSymbolicName() );
        sb.append( ":");
        sb.append( bundle.getVersion().toString() );
        sb.append( "] ");
        sb.append( msg );
        return sb.toString();
    }
}
