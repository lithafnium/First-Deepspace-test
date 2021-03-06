/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class moveElevator extends Command {
  double speed; 
  public moveElevator(double speed) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    this.speed = speed; 
    requires(Robot.elevator);

    
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    // VELOCITY : 1
    //Robot.elevator.setPID(RobotMap.elevator_velocity_kF, RobotMap.elevator_velocity_kP, RobotMap.elevator_velocity_kI, RobotMap.elevator_velocity_kD); 
    //Robot.elevator.elevator.selectProfileSlot(0, 0); 
    Robot.elevator.setSlot(RobotMap.ELEVATOR_VELOCITY_SLOT);


  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.elevator.setSpeed(this.speed); 
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.elevator.setSlot(RobotMap.ELEVATOR_POSITION_SLOT); 

    Robot.elevator.position = Robot.elevator.getPosition(); 

  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.elevator.setSlot(RobotMap.ELEVATOR_POSITION_SLOT); 
    Robot.elevator.position = Robot.elevator.getPosition(); 
  }
}
