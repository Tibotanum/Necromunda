package necromunda;

public class ShotHandler {
	private Necromunda3dProvider provider;
	private ShotHandler nextHandler;
	
	public void handle(ShotInfo shotInfo) {
		if (nextHandler != null) {
			nextHandler.handle(shotInfo);
		}
	}

	public ShotHandler getNextHandler() {
		return nextHandler;
	}

	public void setNextHandler(ShotHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public Necromunda3dProvider getProvider() {
		return provider;
	}

	public void setProvider(Necromunda3dProvider provider) {
		this.provider = provider;
	}
}
