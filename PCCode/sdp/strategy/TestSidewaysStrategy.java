package sdp.strategy;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;

import sdp.comms.BrickCommServer;
import sdp.comms.RobotCommand;
import sdp.vision.Vector2f;
import sdp.vision.gui.tools.RobotDebugWindow;
import sdp.world.oldmodel.MovingObject;
import sdp.world.oldmodel.Point2;
import sdp.world.oldmodel.WorldState;

public class TestSidewaysStrategy extends GeneralStrategy {

	
	private BrickCommServer brick;
	private ControlThread controlThread;
	private Deque<Vector2f> ballPositions = new ArrayDeque<Vector2f>();
	
	private long caughtTime;
	private long kickTime;
	private boolean kicked = false;
	private boolean ballCaught = false;
	boolean isReady = false;
	
	

	public TestSidewaysStrategy(BrickCommServer brick) {
		this.brick = brick;
		this.controlThread = new ControlThread();
	}

	@Override
	public void stopControlThread() {
		this.controlThread.stop();
	}

	@Override
	public void startControlThread() {
		this.controlThread.start();
	}
	public boolean isRobotReady(){
		return isReady;
	}
	
	@Override
	public void sendWorldState(WorldState worldState) {
		super.sendWorldState(worldState);
		brick.robotController.setWorldState(worldState);
		
		double targetAngle;//calcTargetAngle(dx, dy);
		double dx;
		double dy;	
		double ourX, ourY;
		double angleDifference;
		
		//Always aim towards enemy goal
		dx = goalX - attackerRobotX;
		dy = 0;//goalY[1] - attackerRobotY;
		targetAngle = calcTargetAngle(dx, dy);
		
		
			
		if(this.brick.name.equals("attacker")){
			ourX = attackerRobotX;
			ourY = attackerRobotY;
			dx = brick.testTarget.getX() - attackerRobotX; //ballX - attackerRobotX;
			dy = brick.testTarget.getY() - attackerRobotY;//ballY - attackerRobotY;
			angleDifference = calcAngleDiff(attackerOrientation, targetAngle);
		}
		else{
			ourX = defenderRobotX;
			ourY = defenderRobotY;
			dx = brick.testTarget.getX() - defenderRobotX; //ballX - attackerRobotX;
			dy = brick.testTarget.getY() - defenderRobotY;//ballY - attackerRobotY;
			angleDifference = calcAngleDiff(defenderOrientation, targetAngle);
		}
		
		
		
		
		
		
		
		
		boolean rotate = false;
		if(Math.abs(angleDifference) > ROTATION_THRESHOLD)
		{
			rotate = true;
		}
		
		
		
		
		/*int border_threshold = 70;
		if(ourY < topY + border_threshold){			
			dy = topY + border_threshold - attackerRobotY;
		}
		else if( ourY > bottomY - border_threshold){
			dy = bottomY - border_threshold - attackerRobotY;
		}*/
		
		
		
		
		boolean move_robot = false;	
		
		if(Math.abs(dy) > 20){
			move_robot = true;
			if(attackerOrientation > 90){
				dy = -dy;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		synchronized (this.controlThread) {
			
			
			
			if(rotate){
				this.controlThread.operation.op = Operation.Type.DEFROTATE;
				controlThread.operation.angleDifference = angleDifference;
			}
			else if(move_robot){
				this.controlThread.operation.op = Operation.Type.DESIDEWAYS;
				controlThread.operation.travelDistance = dy;
			}
			else{
				this.controlThread.operation.op = Operation.Type.STOP;				
			}
			
			
			/*
			 * if(Math.abs(angleDifference )> 0) {
				this.controlThread.operation.op = Operation.Type.DEFROTATE;
				controlThread.operation.rotateBy = (int) angleDifference;
			} else if(move_robot) {
				////System.out.println("A: ");
				this.controlThread.operation.op = Operation.Type.DEFTRAVEL;
				controlThread.operation.travelDistance = (int) Math.abs(targetDistance);
			}
			else if(true){
				//RobotDebugWindow.messageAttacker.setMessage("SAVE: " + targetDistance);
				this.controlThread.operation.op = Operation.Type.DESIDEWAYS;
				controlThread.operation.travelDistance = (int) 220;
			}
			else if(alignWithEnemyAttacker){
				
				this.controlThread.operation.op = Operation.Type.DESIDEWAYS;
				controlThread.operation.travelDistance = (int) targetDistance;
			}*/
			
			
		}
	}
	
	protected class ControlThread extends Thread {
		public Operation operation = new Operation();
		private ControlThread controlThread;

		public ControlThread() {
			super("Robot control thread");
			setDaemon(true);
		}
		@Override
		
		
		public void run() {
			try {
				while (true) {
					Operation.Type op;
					double rotateBy, travelDist;
					synchronized (this) {
						op = this.operation.op;
						rotateBy = this.operation.angleDifference;
						travelDist = this.operation.travelDistance;
					}
					//System.out.println("operation: " + op);
					switch (op) {
					case STOP:
						brick.robotController.stop();
						//RobotDebugWindow.messageAttacker.setMessage("STOP");
						break;
					case DEFROTATE:
						brick.robotController.rotate(-rotateBy);
						//RobotDebugWindow.messageAttacker.setMessage("Rotating: "+angleDifference);
						break;
					case DEFTRAVEL:
						 brick.robotController.travel(travelDist);
						break;
					case DESIDEWAYS:
						brick.robotController.travelSideways(travelDist);
						break;
					/*case DEBACK:
						if (travelDist != 0) {
							brick.execute(new RobotCommand.Trave(
									travelDist));
						}
						break;*/
					case DEFCATCH:
						if((System.currentTimeMillis() - kickTime > 3000)){
							brick.execute(new RobotCommand.Catch());
							ballCaught = true;
							caughtTime = System.currentTimeMillis();
							kicked = false;
						}
						break;
					case DEFKICK:
						if((System.currentTimeMillis() - caughtTime > 1000)){
							brick.execute(new RobotCommand.Kick());
							
							kicked = true;
							ballCaught = false;
							kickTime = System.currentTimeMillis();
						}
						break;
					case DEFUNCATCH:
						brick.execute(new RobotCommand.ResetCatcher());
						break;
					default:
						break;
					}
					
					
					
					Thread.sleep(StrategyController.STRATEGY_TICK);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			finally {}
			
		}
	}

}