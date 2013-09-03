package su.rss.executer;

public abstract class Executer implements Runnable {

	Thread thread;
	ExecuterListener mListener;
	
	public abstract interface ExecuterListener {
		public abstract void onResult(Object result);
	}

	public void start() {
		if(thread == null) {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	public void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setListener(ExecuterListener listener) {
		mListener = listener;
	}
}
