// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The methods in this class are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private int counter = 0; // counter adli degiskene sadece bu sinif icinden mudahele edilebilir.
  private final PWMVictorSPX m_leftMotor = new PWMVictorSPX(0);
  private final PWMVictorSPX m_rightMotor = new PWMVictorSPX(1);
  private final XboxController driver = new XboxController(0);

  static int loopsFor(double seconds) { // otonom motorun ne kadar sure calisacagini belirler, otonom suresini dongu
                                        // sayisina cevirir.
    return (int) (seconds / 0.02); // saniyeyi 20 milisaniyelik dongu sayisina boler sonucunu tam sayiya cevirir.
  }

  static double secondsFor(int loops) { // dongu sayisini saniyeye cevirir.
    return loops * 0.02; // dongu sayisini zamana cevirmek icin 0.02 ile carpilir.
  }

  public double leftSpeed(double forward, double turn) {
    return (forward - turn);
  }

  public double rightSpeed(double forward, double turn) {
    return -(forward + turn);
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  public Robot() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    counter = 0;
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    counter++;
    double output = 0.0;

    switch (m_autoSelected) {
      case kCustomAuto:
        output = (counter < loopsFor(1.0)) ? 0.75 : 0.0; // kosul dogruysa 0.75 secer, yanlissa 0.0 secer.
        m_leftMotor.set(-output); // output hangi degerse o degeri motora gonderir.
        m_rightMotor.set(output);
        break;
      case kDefaultAuto:
      default:
        output = (counter > loopsFor(2.0)) ? 0.5 : 0.0;
        m_leftMotor.set(-output);
        m_rightMotor.set(output);
        break;
    }
    SmartDashboard.putNumber("Auto loops", counter); // gecen 20 ms dongu sayisini dahsboard a gonderir.
    SmartDashboard.putNumber("Auto output", output); // o anki motor cikis hizini (or. 0.5 veya 0.0) dashboard a
                                                     // gonderir

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    double rawForward = -driver.getLeftY(); // minus: stick up gives a negative number
    double forward = applyDeadband(rawForward, 0.1) * 0.3;
    double turn = applyDeadband(-driver.getRightX(), 0.1) * 0.3;
    double left = leftSpeed(forward, turn);
    double right = rightSpeed(forward, turn);
    left = clamp(left, -1.0, 1.0);
    right = clamp(right, -1.0, 1.0);
    m_leftMotor.set(left);
    m_rightMotor.set(right);

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    timer.stop();
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  static double applyDeadband(double value, double deadband) {
    if ((value > -deadband) && (value < deadband)) {
      return 0.0;
    } else {
      return value;
    }
  }

  Timer timer = new Timer();

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    timer.restart();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    if (timer.get() <= 2.5 ) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(-0.3);
    } else if (timer.get() <= 3.05) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(0.3);
    } else if (timer.get() <= 5.05) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(-0.3);
    } else if (timer.get() <= 5.68) { // For the second turn we gotta addd 0.2 more cause there is a drift in the movement
      m_leftMotor.set(0.3);
      m_rightMotor.set(0.3);
    } else if(timer.get() <= 7.9) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(-0.3);
    } else if (timer.get() <= 8.5) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(0.3);
    } else if (timer.get() <= 10.37) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(-0.3);
    } else if (timer.get() <= 10.9) {
      m_leftMotor.set(0.3);
      m_rightMotor.set(0.3);
    }
    else {
      m_leftMotor.stopMotor();
      m_rightMotor.stopMotor();
    }

  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }

  static double clamp(double value, double min, double max) {
    if (value > max) {
      return max;
    } else if (value < min) {
      return min;
    } else {
      return value;
    }
  }

}
