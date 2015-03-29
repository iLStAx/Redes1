JCC = javac
JFLAGS = -g

default: TCP_server.class PoolThread.class ThreadPool.class BlockingQueue.java

PoolThread.class: PoolThread.java
	$(JCC) $(JFLAGS) PoolThread.java

ThreadPool.class: ThreadPool.java
	$(JCC) $(JFLAGS) ThreadPool.java

BlockingQueue.class: BlockingQueue.java
	$(JCC) $(JFLAGS) BlockingQueue.java

TCP_server.class: TCP_server.java
	$(JCC) $(JFLAGS) TCP_server.java

clean: 
	$(RM) *.class