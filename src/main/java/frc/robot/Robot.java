import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;


  @Override
  public void teleopPeriodic() {
    double rawForward = -driver.getLeftY();   // minus: stick up gives a negative number
    double forward = applyDeadband(rawForward, 0.1);
    double turn = applyDeadband(driver.getRightX(), 0.1);
    double left = leftSpeed(forward, turn);
    double right = rightSpeed(forward, turn);
    left = clamp(left, -1.0, 1.0);
    right = clamp(right, -1.0, 1.0);
    m_leftMotor.set(left);
    m_rightMotor.set(right);

  }



  @Override
  public void simulationPeriodic() {}
  static double clamp(double value, double min, double max){
    if (value > max){
      return max;
    }else if (value < min){
      return min;
    } else{
      return value;
    }
    }
