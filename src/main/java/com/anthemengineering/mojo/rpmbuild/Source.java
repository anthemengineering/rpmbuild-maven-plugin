/*
 * Copyright 2016 Anthem Engineering LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anthemengineering.mojo.rpmbuild;

import java.io.File;

/**
 * A description of a source tarball or patch file.
 */
public class Source {

    private String sourceFile;

    private String destinationName;

    public String getSourceFile() {
        return sourceFile;
    }

    public String getDestinationName() {
        if (destinationName != null) {
            return destinationName;
        }

        return new File(sourceFile).getName();
    }
}
