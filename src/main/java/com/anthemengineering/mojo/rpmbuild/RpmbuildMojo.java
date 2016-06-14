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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Packages an RPM spec into an RPM using rpmbuild.
 */
@Mojo(name="rpmbuild", defaultPhase=LifecyclePhase.PACKAGE)
public class RpmbuildMojo extends AbstractMojo {

    @Parameter
    private File spec;

    @Parameter(defaultValue="${project.build.directory}/rpmbuild")
    private File topdir;

    @Parameter
    private List<Source> sources = Collections.emptyList();

    @Parameter
    private Map<String, String> defines = Collections.emptyMap();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        buildTopdir();
        copySpec();
        copySources();
        executeRpmbuild();
    }

    private void buildTopdir() throws MojoFailureException {
        final String[] subdirs = { "BUILD", "RPMS", "SOURCES", "SPECS", "SRPMS" };

        if (!topdir.exists()) {
            getLog().info( "Creating directory " + topdir.getAbsolutePath() );
            if (!topdir.mkdirs()) {
                throw new MojoFailureException("Unable to create directory " + topdir.getAbsolutePath());
            }
        }

        for (String sd : subdirs) {
            File dir = new File(topdir, sd);
            if (!dir.exists()) {
                getLog().info("Creating directory " + dir.getAbsolutePath());
                if (!dir.mkdir()) {
                    throw new MojoFailureException("Unable to create directory " + dir.getAbsolutePath());
                }
            }
        }
    }

    private void copySpec() throws MojoFailureException {
        try {
            File specDir = new File(topdir, "SPECS");
            getLog().info("Copying spec " + spec.getAbsolutePath() + " to " + specDir);
            FileUtils.copyFileToDirectory(spec, specDir);
        } catch (IOException e) {
            throw new MojoFailureException("Unable to copy spec file " + spec.getAbsolutePath(), e);
        }
    }

    private void copySources() throws MojoFailureException {
        for (Source s : sources) {
            File src = new File(s.getSourceFile());
            File dst = new File(new File(topdir, "SOURCES"), s.getDestinationName());
            getLog().info("Copying " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
            try {
                FileUtils.copyFile(src, dst);
            } catch (IOException e) {
                throw new MojoFailureException("Unable to copy " + src.getAbsolutePath() + " to " +
                        dst.getAbsolutePath(), e);
            }
        }
    }

    private void executeRpmbuild() throws MojoExecutionException {
        Commandline cl = new Commandline();
        cl.setExecutable("rpmbuild");
        cl.createArg().setValue("-ba");

        cl.createArg().setValue("--define");
        cl.createArg().setValue("_topdir " + topdir.getAbsolutePath());

        for (Map.Entry<String, String> e : defines.entrySet()) {
            cl.createArg().setValue("--define");
            cl.createArg().setValue(String.format("%s %s", e.getKey(), e.getValue()));
        }

        cl.createArg().setValue(new File(new File(topdir, "SPECS"), spec.getName()).getAbsolutePath());

        try {
            getLog().info("Executing " + cl.toString());
            Process process = cl.execute();
            int rval = process.waitFor();
            if (rval != 0) {
                throw new MojoExecutionException("Failed to execute: " + cl.getLiteralExecutable());
            }
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Failed to execute: " + cl.getLiteralExecutable(), e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Failed to execute: " + cl.getLiteralExecutable(), e);
        }
    }
}
