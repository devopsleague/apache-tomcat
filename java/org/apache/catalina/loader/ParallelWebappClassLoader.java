/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;

public class ParallelWebappClassLoader extends WebappClassLoaderBase {

    private static final Log log = LogFactory.getLog(ParallelWebappClassLoader.class);

    static {
        if (!JreCompat.isGraalAvailable()) {
            if (!ClassLoader.registerAsParallelCapable()) {
                log.warn(sm.getString("webappClassLoaderParallel.registrationFailed"));
            }
        }
    }

    public ParallelWebappClassLoader() {
        super();
    }


    public ParallelWebappClassLoader(ClassLoader parent) {
        super(parent);
    }


    /**
     * Returns a copy of this class loader without any class file transformers. This is a tool often used by Java
     * Persistence API providers to inspect entity classes in the absence of any instrumentation, something that can't
     * be guaranteed within the context of a {@link java.lang.instrument.ClassFileTransformer}'s
     * {@link java.lang.instrument.ClassFileTransformer#transform(ClassLoader, String, Class, java.security.ProtectionDomain, byte[])
     * transform} method.
     * <p>
     * The returned class loader's resource cache will have been cleared so that classes already instrumented will not
     * be retained or returned.
     *
     * @return the transformer-free copy of this class loader.
     */
    @Override
    public ParallelWebappClassLoader copyWithoutTransformers() {

        ParallelWebappClassLoader result = new ParallelWebappClassLoader(getParent());

        super.copyStateWithoutTransformers(result);

        try {
            result.start();
        } catch (LifecycleException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }
}
