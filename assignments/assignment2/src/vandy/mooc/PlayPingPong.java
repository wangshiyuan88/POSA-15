package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    
    Handler mPingHandler;
    Handler mPongHandler;

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    
    CyclicBarrier barrier = new CyclicBarrier(2);

    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;
        
        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
 
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            
        	mMyType = myType;
        	mIterationsCompleted = 0;
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
        	Log.i(mMyType.toString(), mMyType.toString()+" onLooperPrepared start!");
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            
        	
        	if(mMyType==PingPong.PING){
        		mPingHandler = new Handler(getLooper(), this);
        	}
        	if (mMyType == PingPong.PONG){
        		mPongHandler = new Handler(getLooper(), this);
        	}

            try {
                // Wait for both Threads to initialize their Handlers.
                
            	barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(mMyType.toString(), mMyType.toString()+" onLooperPrepared finish!");
            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            
            if(mMyType==PingPong.PING){
            	Message msg = mPingHandler.obtainMessage();
            	msg.obj = mPongHandler;
            	mPingHandler.sendMessage(msg);
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.
            Log.i(mMyType.toString(), mMyType.toString()+": handle msg"+ mIterationsCompleted);
            if (mIterationsCompleted<5) {
            	mIterationsCompleted++;
            	mOutputStrategy.print("\n"+mMyType.toString()+":"+mIterationsCompleted+"!");
            }

            if(mMyType==PingPong.PONG && mIterationsCompleted==5){
                quit();
            }
            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            
            Handler nextTargetHandler = (Handler) reqMsg.obj;
            Message nextMsg = nextTargetHandler.obtainMessage();
            nextMsg.obj = mMyType==PingPong.PING? mPingHandler : mPongHandler;
            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            
            nextTargetHandler.sendMessage(nextMsg);
            if(mMyType==PingPong.PING && mIterationsCompleted==5) {
                quit();
            }
            return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!");
       
        // Create the ping and pong threads.
        
        Log.i("PlayPingPong", "Define ping and pong thread");
        PingPongThread pingThread = new PingPongThread(PingPong.PING);
        PingPongThread pongThread = new PingPongThread(PingPong.PONG);
        // Start ping and pong threads, which cause their Looper to
        // loop.
        
        Log.i("PlayPingPong", "Ping and pong start");
        pingThread.start();
        pongThread.start();
        try {
			pingThread.join();
			pongThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
       
        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        

        // Let the user know we're done.
        mOutputStrategy.print("\nDone!");
    }
}
