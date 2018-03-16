package com.frc5104.utilities;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonFactory {
	TalonSRX talons[];
	
	public TalonFactory(int[] talonIds) {
		talons = new TalonSRX[talonIds.length];
		for (int i = 0; i < talonIds.length; i++) {
			talons[i] = new TalonSRX(talonIds[i]);
		}
		init();
	}//TalonFactory
	
	public void init() {
		for (TalonSRX taloni:talons) {
			fixTalon(taloni);
			System.out.println("Configured Talon: "+taloni.getDeviceID());
		}
	}//init
	
	public static void fixTalon (TalonSRX talon) {
			talon.configOpenloopRamp(0, 0);
			talon.configClosedloopRamp(0, 0);
			talon.configPeakOutputForward(1, 0);
			talon.configPeakOutputReverse(-1, 0);
			talon.configNominalOutputForward(0, 0);
			talon.configNominalOutputReverse(0, 0);
			talon.configNeutralDeadband(0.04, 0);
			talon.configVoltageCompSaturation(0, 0);
			talon.configVoltageMeasurementFilter(32, 0);
			talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
			//talon.configSelectedFeedbackSensor(RemoteFeedbackDevice.None, 0, 0);
			//talon.configSelectedFeedbackCoefficient(1.0);
			//talon.configRemoteFeedbackFilter(off 0);
			//talon.configSensorTerm	Quad (0) for all term types);
			talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, 0);
			talon.configVelocityMeasurementWindow(64, 0);
			//talon.configReverseLimitSwitchSource(0, and "Normally Open");
			//talon.configForwardLimitSwitchSource(LimitSwitchSource.?, LimitSwitchNormal.NormallyOpen, 0);
			talon.configForwardSoftLimitThreshold(0, 0);
			talon.configReverseSoftLimitThreshold(0, 0);
			talon.configForwardSoftLimitEnable(false, 0);
			talon.configReverseSoftLimitEnable(false, 0);
			talon.config_kP(0, 0, 0);
			talon.config_kI(0, 0, 0);
			talon.config_kD(0, 0, 0);
			talon.config_kF(0, 0, 0);
			talon.config_IntegralZone(0, 0, 0);
			talon.configAllowableClosedloopError(0, 0, 0);
			talon.configMaxIntegralAccumulator(0, 0, 0);
			//talon.configClosedLoopPeakOutput(1.0);
			//talon.configClosedLoopPeriod(1);
			//talon.configAuxPIDPolarity(false);
			talon.configMotionCruiseVelocity(0, 0);
			talon.configMotionAcceleration(0, 0);
			//talon.configMotionProfileTrajectoryPeriod(0);
			talon.configSetCustomParam(0, 0, 0);
			talon.configPeakCurrentLimit(0, 0);
			talon.configContinuousCurrentLimit(0, 0);
	}//fixTalon
 }
