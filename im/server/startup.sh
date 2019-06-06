#-Xverify:none -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider
#-Xrunjdwp:transport=dt_socket,address=9999,suspend=n,server=y

nohup java -Xverify:none -Xms64m -Xmx200m -XX:+HeapDumpOnOutOfMemoryError -Dtio.default.read.buffer.size=1024 -XX:HeapDumpPath=./tio-im-pid.hprof -cp ./config:./lib/* org.tio.examples.im.server.ImServerStarter &