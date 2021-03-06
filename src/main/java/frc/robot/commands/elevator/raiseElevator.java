/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class raiseElevator extends Command {
  public raiseElevator() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.elevator); 
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    if(!(Robot.elevator.carriage_up.get() && Robot.elevator.stage2_up.get())){

      Robot.elevator.raiseElevator();
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // if(!(Robot.elevator.carriage_up.get() && Robot.elevator.stage2_up.get())){

    //   Robot.elevator.raiseElevator();
    // }
          Robot.elevator.raiseElevator();

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.elevator.position = Robot.elevator.getPosition();
    Robot.elevator.stopElevator();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.elevator.position = Robot.elevator.getPosition();
    Robot.elevator.stopElevator();
  }
}
