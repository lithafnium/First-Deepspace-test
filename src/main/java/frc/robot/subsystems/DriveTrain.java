package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.drive.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;



public class DriveTrain extends Subsystem {
	//private SpeedController bottomLeft; 
	//private SpeedController bottomRight;

	

	private WPI_VictorSPX frontLeftSpx; 
	private WPI_VictorSPX frontRightSpx; 
	private WPI_TalonSRX backLeftSrx;
	private WPI_TalonSRX backRightSrx;

	public double MAXIMUM_VELOCITY = 3600; // take 90-80% of this 
	public double ramp = 3;

	public int vslot = 0;
	public double kF = 0.28544;
	public double kP = 0;
	public double kD = 0;

	// left talon id: 1
	// right talon id: 0 
	SpeedControllerGroup left; 
	SpeedControllerGroup right; 
	 
	public Encoder leftEncoder; 
	public Encoder rightEncoder;
	
	public DigitalInput colorSensor; 

	public boolean backwards = false; 
	public boolean quickTurn = false; 
	public AHRS gyroSensor; 

	public boolean drivingStraight; 

//	RobotDrive robotDrive; 
	DifferentialDrive robotDrive; 

	public DriveTrain(){

		drivingStraight = false; 

		//---------LEFT Controllers-----------
		backLeftSrx = new WPI_TalonSRX(RobotMap.BACK_LEFT_MOTOR);//1
		frontLeftSpx = new WPI_VictorSPX(RobotMap.FRONT_LEFT_MOTOR); //2 

		backLeftSrx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		left = new SpeedControllerGroup(backLeftSrx, frontLeftSpx);
		frontLeftSpx.follow(backLeftSrx);
		backLeftSrx.getSensorCollection().setQuadraturePosition(0, 10);

		//--------RIGHT Controllers-----------
		frontRightSpx = new WPI_VictorSPX(RobotMap.FRONT_RIGHT_MOTOR); // 3 
		backRightSrx = new WPI_TalonSRX(RobotMap.BACK_RIGHT_MOTOR); // 4

		right = new SpeedControllerGroup(backRightSrx, frontRightSpx); 
		backRightSrx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		frontRightSpx.follow(backRightSrx); 
    	backRightSrx.getSensorCollection().setQuadraturePosition(0, 10);

		//-------Velocity PID-------
		backLeftSrx.config_kF(vslot, kF);
		//backLeftSrx.config_kP(vslot, kP);
		backLeftSrx.selectProfileSlot(vslot, 0);

		backRightSrx.config_kF(vslot, kF);
		//backRightSrx.config_kP(vslot, kP);
		backRightSrx.selectProfileSlot(vslot, 0);

		robotDrive = new DifferentialDrive(left, right); 
		robotDrive.setSafetyEnabled(false);
		
		gyroSensor = new AHRS(SPI.Port.kMXP);
		gyroSensor.reset(); 
	}

	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
		setDefaultCommand(new curvatureDrive()); 

	}
	
	public void rotateClockWise(){
		robotDrive.tankDrive(0.5, -0.5);
	}
	
	public void rotateCounterClockWise(){
		robotDrive.tankDrive(-0.5, 0.5); 
	}

	public void setSpeed(double left_input, double right_input, boolean isQuickTurn){
		
		if(isQuickTurn && (left_input == 0 && right_input != 0)) {
			left_input = -right_input;
		}
		else if(isQuickTurn && (right_input == 0 && left_input != 0)){
			right_input = -left_input;
		}

		backLeftSrx.set(ControlMode.Velocity, left_input * MAXIMUM_VELOCITY); 
		backRightSrx.set(ControlMode.Velocity, right_input * MAXIMUM_VELOCITY); 
	}

	public void driveNow(Joystick left, Joystick right){
		
		frontLeftSpx.setInverted(backwards);
		backLeftSrx.setInverted(backwards);
		frontRightSpx.setInverted(backwards);
		backRightSrx.setInverted(backwards);
		if(backwards) robotDrive.tankDrive(-right.getY(), -left.getY(), true); 
		else robotDrive.tankDrive(-left.getY(),  -right.getY(), true);

	}
	
	public void curavtureDrive(double forward, double turn){
		frontLeftSpx.setInverted(backwards);
		backLeftSrx.setInverted(backwards);
		frontRightSpx.setInverted(backwards);
		backRightSrx.setInverted(backwards);
		if(forward < -0.1 ) turn = -turn; 

		forward = Math.abs(forward) < 0.2 ? 0 : forward; 
		turn = Math.abs(turn) < 0.2 ? 0 : turn; 

		double left_command  = forward + turn; 
		double right_command = forward - turn;
		//robotDrive.curvatureDrive(forward * Math.abs(forward) * 0.8, turn * Math.abs(turn) * 0.8, true);
		setSpeed(left_command * Math.abs(left_command) * 0.8, right_command * Math.abs(right_command) * 0.8, true);
	}

	public void driveCertainAmounts(double left, double right){
		robotDrive.tankDrive(left, right); 
	}

	public void stop(){
		robotDrive.tankDrive(0.0, 0.0);
	}

	public void driveSlow(){
		robotDrive.tankDrive(0.5, 0.5); 
	}

	public void calibrateGyro(){
		gyroSensor.reset();
	}

	public double getYaw(){
		return gyroSensor.getYaw(); 
	}

	public void resetGyro(){
		gyroSensor.reset(); 
	}

	public void resetEncoders(){
    	backLeftSrx.getSensorCollection().setQuadraturePosition(0, 10); 
    	backRightSrx.getSensorCollection().setQuadraturePosition(0, 10);

	}

	public double getLeft(){
		return backLeftSrx.getSensorCollection().getQuadraturePosition();
	}
	public double getRight(){
		return backRightSrx.getSensorCollection().getQuadraturePosition();
	}

	public void updateSmartDashboard(){
		//		SmartDashboard.putNumber("Ultrasonic sensor one", getDistanceOne()); 
		//		SmartDashboard.putNumber("Ultrasonic sensor two", getDistanceTwo()); 


		SmartDashboard.putNumber("Drivetrain Angle:", getYaw()); 
		SmartDashboard.putNumber("Left Encoder: ", getLeft());
		SmartDashboard.putNumber("Right Encoder: ", getRight()); 

		SmartDashboard.putBoolean("Driving straight:", this.drivingStraight); 


	}
	

}
