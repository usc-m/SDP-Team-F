package sdp.comms;

import java.io.IOException;

import sdp.comms.packets.*;
import sdp.util.DriveDirection;
import sdp.control.HolonomicRobotController;

public class RobotCommand {
    private static HolonomicRobotController robotController = new HolonomicRobotController(null, (byte) 0);
    //protected HolonomicRobotController robotController;
	protected RobotCommand() {		
	}

	public interface Command {
		public void sendToBrick(Radio radio)
				throws IOException;
		
		public void sendToBrick(Radio radio, HolonomicRobotController rC)
				throws IOException;
	}

	private static abstract class GenericCommand implements Command {
		protected abstract Packet getOpcode();

		@Override
		public void sendToBrick(Radio radio)
				throws IOException {
			Packet opcode = getOpcode();		
			
			radio.sendPacket(getOpcode());
		}
		
		@Override
		public void sendToBrick(Radio radio, HolonomicRobotController rC) throws IOException{
			//super.
			Packet opcode = getOpcode();		
			
			radio.sendPacket(getOpcode());
		}
	}
	
	// Classes below represent every possible brick command

	public static class Stop extends GenericCommand {
		@Override
		protected Packet getOpcode() {
            DriveDirection dir = DriveDirection.FORWARD;
            byte zero = 0;
			return new DrivePacket(zero, dir, zero, dir, zero, dir, zero);
		}
	}

	public static class Kick extends GenericCommand {
		public Kick() {}

		@Override
		protected Packet getOpcode() {
			return new KickPacket();
		}
	}

	public static class Catch extends GenericCommand {
		@Override
		protected Packet getOpcode() {
			return new EngageCatcherPacket();
		}
	}

	public static class Rotate extends GenericCommand {
		private double angle;

		public Rotate(double angle){            
			this.angle = angle;
		}

		@Override
		protected Packet getOpcode() {
			return null;//robotController.rotate(angle);
		}
	}

	public static class Travel extends GenericCommand {
		private double displacement;
		
		public Travel(double displacement) {
			this.displacement = displacement;
		}
		
		@Override
		protected Packet getOpcode() {
			return null;//robotController.travel(displacement);
		}
	}
	
	public static class TravelSideways extends GenericCommand {
		private double distance;
		
		public TravelSideways(double distance) {
			this.distance = distance;
		}
		
		@Override
		protected Packet getOpcode() {
			return null;//robotController.travelSideways(distance);
		}
	}
	
	
	public static class TravelDiagonally extends GenericCommand {
		private double angle, distance;
		
		public TravelDiagonally(double angle, double distance) {
			this.angle = angle;
			this.distance = distance;
		}
		
		@Override
		protected Packet getOpcode() {
			return robotController.travelDiagonally(angle, distance);
		}
	}
	
	public static class ResetCatcher extends GenericCommand {
		@Override
		protected Packet getOpcode() {
			return new DisengageCatcherPacket();
		}
	}
}
