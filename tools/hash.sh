#!/bin/bash
SCRIPT_PATH="${BASH_SOURCE[0]}"
if ! [ -x "$(command -v realpath)" ]; then
    if [ -L "$SCRIPT_PATH" ]; then
        
        [ -x "$(command -v readlink)" ] || { echo "Not able to resolve symlink. Install realpath or readlink." 1>&2;exit 1; }
        
        # try readlink (-f not needed because we know its a symlink)
        DIR="$( cd "$( dirname $(readlink "$SCRIPT_PATH") )" && pwd -P)"
    else
        DIR="$( cd "$( dirname "$SCRIPT_PATH" )" && pwd -P)"
    fi
else
    DIR="$( cd "$( dirname "$(realpath "$SCRIPT_PATH")" )" && pwd -P)"
fi

BIN_PATH="java"

if [ -z "$JAVA_HOME" ]; then
    echo "WARNING: JAVA_HOME not set, will use $(which $BIN_PATH)" 1>&2
else
    BIN_PATH="$JAVA_HOME/bin/java"
fi

"$BIN_PATH" $JAVA_OPTS -cp "$DIR/../../search-guard-ssl/*:$DIR/../*:$DIR/../deps/*:$DIR/../../../lib/*" com.floragunn.searchguard.tools.Hasher "$@"
