
public class Barrier {

	Semaphore syncSem;
	Semaphore counterMutex;
	Semaphore barrierSem;
	boolean isBarrierOn;
	int barrierCounter;
	int prevCounter;
	public Barrier () {
		/* We use 3 semaphores in total, 1 for managing critical sections around the barrier counter (counterSem),
		 * one for forbidding cars from entering the barrier before all cars have left it (barrierSem),
		 * and one for stopping the cars at the barrier (syncSem)
		 */
		syncSem = new Semaphore(0);
		counterMutex = new Semaphore(1);
		barrierSem = new Semaphore(9);
		isBarrierOn = false;
		barrierCounter = 0;
	}
	public void sync() throws InterruptedException {
		barrierSem.P();
		
		counterMutex.P();
		barrierCounter++;
		if(barrierCounter == 9) {
			syncSem.V();
		}
		counterMutex.V();
		
		syncSem.P();
		
		counterMutex.P();
		barrierCounter--;
		syncSem.V();
		if(barrierCounter == 0) { // Re-initiate the semaphores so the barrier can be reused.
			syncSem.P();
			for(int i = 0; i<9;i++) barrierSem.V();
		}
		counterMutex.V();
	}
	
	
	
	public void on() {
		if(!isBarrierOn) {
			isBarrierOn = true;
			syncSem = new Semaphore(0);
		}
	}
	
	public void off() {
		if(isBarrierOn) {
			int i = barrierCounter;
			while(i>0){
				syncSem.V();
				i--;
			}	
		}
		isBarrierOn = false;
	}
	
}
