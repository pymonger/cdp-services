#!/usr/bin/env bash

# ==============================================================================
# This script installs a 3rd party jar to your local Maven repository (normally
# in '~/.m2/repository/'). Once in your local repository, it is searchable and
# can be added as a dependency in your Maven POM.
#
# Guide to installing 3rd party JARs
# http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
# "Often times you will have 3rd party JARs that you need to put in your local
# repository for use in your builds. The JARs must be placed in the local
# repository in the correct place in order for it to be correctly picked up by
# Maven. To make this easier, and less error prone, we have provide a goal in
# the install plug-in which should make this relatively painless."
# ==============================================================================


# ------------------------------------------------------------------------------
# Show usage if have insufficient number of required arguments.
# ------------------------------------------------------------------------------

function showUsage() {
    echo "Installs a 3rd party jar to your local Maven repository. (normally in '~/.m2/repository/')"
    echo "Usage:"
    echo "  ${0} <path-to-jar-file> <group-id> <artifact-id> <version>"
    echo "Example:"
    echo "  ${0}  myapp.jar  gov.nasa.jpl.cdp.myapp  myapp  0.0.1"
}

# if not have mandatory number of arguments, show usage
if [ ${#} -lt 4 ]; then
    showUsage
    exit
fi


# ------------------------------------------------------------------------------
# Get the command-line arguments
# ------------------------------------------------------------------------------

JAR_FILEPATH=${1}
GROUP_ID=${2}
ARTIFACT_ID=${3}
VERSION=${4}


# ------------------------------------------------------------------------------
# To install a JAR in the local repository use the following command:
# ------------------------------------------------------------------------------

mvn install:install-file -Dfile=${JAR_FILEPATH} -DgroupId=${GROUP_ID} -DartifactId=${ARTIFACT_ID} -Dversion=${VERSION} -Dpackaging=jar  # -DlocalRepositoryPath=<path-to-specific-local-repo>

