/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.compiler.internal;

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;


public class RuntimeVersionChecker
        implements JpfLanguageConstants {

    private String _runtimeVersion;

    /**
     * This package-protected constructor allows a RuntimeVersionChecker to be created even if there's
     * no webapp root.  This will cause other compile errors -- in this case we'll just assume the version
     * is high enough.
     */
    RuntimeVersionChecker() {
        _runtimeVersion = getHighVersion();
    }

    /*
    * TODO: in the future, we should have a way to (optionally) find the runtime jar(s) to check the versions.
    * For now (1.0), this is unnecessary.
    *
    public RuntimeVersionChecker()
    {
        File pageflowJar = new File( webappRoot.getPath() + PAGEFLOW_RUNTIME_JAR );
                
        if ( pageflowJar.exists() )
        {
            try
            {
                Manifest mf = new JarFile( pageflowJar ).getManifest();
                        
                if ( mf != null )
                {
                    Attributes attrs = mf.getMainAttributes();
                            
                    if ( attrs != null )
                    {
                        String version = attrs.getValue( RUNTIME_VERSION_ATTRIBUTE );
                        _runtimeVersion = ( version != null ? version : "0" );
                    }
                }
            }
            catch ( IOException e )
            {
                // 
                // This will cause other compile errors.  Just assume that the version is high enough.
                //
                _runtimeVersion = getHighVersion();
            }
        }
        else
        {
            // 
            // This will cause other compile errors.  Just assume that the version is high enough.
            //
            _runtimeVersion = getHighVersion();
        }
    }
    */

    private static String getHighVersion() {
        return new Integer(Integer.MAX_VALUE).toString();
    }

    int getRuntimeVersion() {
        return Integer.parseInt(_runtimeVersion);
    }

    public boolean checkRuntimeVersion(String requiredRuntimeVersion, AnnotationValue value, Diagnostics diags,
                                       String errMsg, Object[] errMsgParams) {
        if (requiredRuntimeVersion != null) {
            int runtimeVersion = getRuntimeVersion();

            if (Integer.parseInt(requiredRuntimeVersion) > runtimeVersion) {
                diags.addError(value, errMsg, errMsgParams);
                return false;
            }
        }

        return true;
    }

    public boolean checkRuntimeVersion(String requiredRuntimeVersion, Declaration element, Diagnostics diags,
                                       String errMsg, Object[] errMsgParams) {
        if (requiredRuntimeVersion != null) {
            int runtimeVersion = getRuntimeVersion();

            if (Integer.parseInt(requiredRuntimeVersion) > runtimeVersion) {
                diags.addError(element, errMsg, errMsgParams);
                return false;
            }
        }

        return true;
    }

    public boolean checkRuntimeVersion(String requiredRuntimeVersion, AnnotationInstance element, Diagnostics diags,
                                       String errMsg, Object[] errMsgParams) {
        if (requiredRuntimeVersion != null) {
            int runtimeVersion = getRuntimeVersion();

            if (Integer.parseInt(requiredRuntimeVersion) > runtimeVersion) {
                diags.addError(element, errMsg, errMsgParams);
                return false;
            }
        }

        return true;
    }
}
