public void robotPeriodic() {}
    static double applyDeadband (double value, double deadband){
    
    if ((value > -deadband) && (value < deadband))
    {
      return 0.0;
    }
    else
    { 
      return value;
    }
  }
//bu kod joysitck de oluşan küçük titreşimleri engelemek içindir . deadband bu titreişimlerdir 
