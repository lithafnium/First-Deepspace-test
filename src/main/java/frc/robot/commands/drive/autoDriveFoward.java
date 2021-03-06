package frc.robot.commands.drive;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;


/**
 *
 */
public class autoDriveFoward extends Command implements PIDOutput{
	double pidOut; 
	double kP; 
	double kI; 
	double kD; 
	PIDController controller; 
    public autoDriveFoward() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain); 
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	controller = new PIDController(kP, kI, kD,  Robot.driveTrain.leftEncoder, this);

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
    
    public void pidWrite(double output){
    	synchronized(this){
    		pidOut = output; 
    		SmartDashboard.putNumber("pidOut:", pidOut); 
    		
    		
    	}
    }
}
