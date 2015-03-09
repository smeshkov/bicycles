#!/bin/sh

### BEGIN INIT INFO
# Provides:          @serviceName@
# Required-Start:    $local_fs $remote_fs $network
# Required-Stop:     $local_fs $remote_fs $network
# Should-Start:      $named
# Should-Stop:       $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start App @serviceName@
# Description:       @packageDescription@
#                    (Version @version@)
### END INIT INFO


###   ENVIRONMENT   ###


set -e

if [ `id -u` -ne 0 ]; then
	echo "You need root privileges to run this script"
	exit 1
fi

PATH=/bin:/usr/bin:/sbin:/usr/sbin
JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")

. /lib/lsb/init-functions
# . /etc/default/rcS


###   DEFINITIONS   ###


USER=sm
GROUP=sm
UMASK=002

SERVICE_NAME=@serviceName@
JAR_NAME=@jarName@

APP_HOME="/var/run/sm/$SERVICE_NAME"
APP_JAR="/usr/lib/sm/$SERVICE_NAME/jars/${JAR_NAME}"

LOGS_PATH="/var/log/sm/$SERVICE_NAME"
LOG_FILE="$LOGS_PATH/console.log"
OOM_FILE="$LOGS_PATH/hd_$(date +'%Y-%m-%d-%H-%M-%S').hprof"
PID_FILE="${APP_HOME}/${SERVICE_NAME}.pid"

CONFIG_FILE="/etc/sm/${SERVICE_NAME}.sh"
DEFAULT_CONFIG_FILE="/etc/sm/backend.sh"


###   PROPERTIES   ###


PROPS_PATH="file:/etc/sm/"
PROPS_FILE="file:/etc/sm/backend.properties"
LOGGING=""


###   JAVA SETUP   ###

JAVA_MEMORY="-Xmx256m -Xms128m"
JAVA_AGENT=""
JAVA_DEBUG=""
JAVA_TUNE="-Djava.net.preferIPv4Stack=true -Xoptimize"
JAVA_GC="-XX:+UseG1GC -XX:+UseCompressedOops"
JAVA_OOM="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$OOM_FILE"
JAVA_CUSTOM_JVM_ARGS="@jvmArgs@"


###   READING LOCAL CONFIG   ###


if [ -e "${DEFAULT_CONFIG_FILE}" ]; then
    . ${DEFAULT_CONFIG_FILE}
fi

if [ -e "${CONFIG_FILE}" ]; then
    . ${CONFIG_FILE}
fi


###   BUILDING TOTAL ARGS   ###


PROPERTIES="-DpropsPath=${PROPS_PATH} -DpropsFile=${PROPS_FILE} ${LOGGING}"

JVM_OPTS="-Dname=$SERVICE_NAME $JAVA_DEBUG $JAVA_AGENT $JAVA_MEMORY $JAVA_TUNE $JAVA_GC $JAVA_OOM $JAVA_CUSTOM_JVM_ARGS"


###   RUN   ###


case "$1" in
    start)
        if [ -z "$JAVA_HOME" ]; then
            log_failure_msg "no JDK found - please set JAVA_HOME"
            exit 1
        fi
        log_daemon_msg "Starting $SERVICE_NAME"
        if start-stop-daemon --start --test --pidfile ${PID_FILE} --startas "$JAVA_HOME/bin/java" >/dev/null; then
            ARGS="$JVM_OPTS $PROPERTIES -jar $APP_JAR"

            if [ ! -d "$APP_HOME" ]; then
                mkdir -p "$APP_HOME";
                chown -R "$USER:$GROUP" "$APP_HOME";
            fi

            start-stop-daemon --background --no-close --start \
                --chdir ${APP_HOME} --pidfile ${PID_FILE} --make-pidfile \
                --chuid ${USER}:${GROUP} --umask ${UMASK} \
                --startas "$JAVA_HOME/bin/java" -- ${ARGS} \
                >> ${LOG_FILE} 2>&1

            sleep 3
            # Because we pushed it into the background, we need to check it's status
            if start-stop-daemon --test --start --pidfile ${PID_FILE} --startas "$JAVA_HOME/bin/java" >/dev/null; then
                log_end_msg 1
            else
                log_end_msg 0
            fi
        else
            log_progress_msg "(already running)"
            log_end_msg 0
        fi
        ;;
    stop)
        log_daemon_msg "Stopping $SERVICE_NAME"
        if start-stop-daemon --start --test --pidfile ${PID_FILE} --startas "$JAVA_HOME/bin/java" >/dev/null; then
            log_progress_msg "(not running)"
        else
            sleep 2
            start-stop-daemon --stop --pidfile ${PID_FILE} \
                --startas "$JAVA_HOME/bin/java"
            sleep 5
            rm -f ${PID_FILE}
        fi
        log_end_msg 0
        ;;
    status)
        if start-stop-daemon --start --test --pidfile ${PID_FILE} --startas "$JAVA_HOME/bin/java" >/dev/null; then
            if [ -f "${PID_FILE}" ]; then
                log_success_msg "$SERVICE_NAME is not running, but pid file exists."
                exit 1
            else
                log_success_msg "$SERVICE_NAME is not running."
                exit 3
            fi
        else
            log_success_msg "$SERVICE_NAME is running with pid `cat ${PID_FILE}`"
        fi
        ;;
    restart|force-reload)
        if start-stop-daemon --test --stop --pidfile "${PID_FILE}" --startas "$JAVA_HOME/bin/java" >/dev/null; then
            $0 stop
            sleep 1
        fi
            $0 start
        ;;
    *)
    log_success_msg "Usage: $0 {start|stop|restart|force-reload|status}"
    exit 1
    ;;
esac

exit 0
